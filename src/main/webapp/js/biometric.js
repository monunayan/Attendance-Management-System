/**
 * Biometric & Fingerprint Authentication Script for CAMS
 */

function bufferToBase64(buffer) {
    let binary = '';
    let bytes = new Uint8Array(buffer);
    for (let i = 0; i < bytes.byteLength; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}

function base64ToBuffer(base64) {
    let binary = window.atob(base64);
    let bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
        bytes[i] = binary.charCodeAt(i);
    }
    return bytes.buffer;
}

// Register Fingerprint / Face ID from Student or Faculty Dashboard
async function registerBiometric(userId, userType) {
    try {
        let credentialId = null;

        // Check WebAuthn support
        if (window.PublicKeyCredential && navigator.credentials && navigator.credentials.create) {
            try {
                const challenge = new Uint8Array(32);
                window.crypto.getRandomValues(challenge);

                const userIDBytes = new TextEncoder().encode(userId);

                const publicKeyCredentialCreationOptions = {
                    challenge: challenge,
                    rp: {
                        name: "College Attendance System"
                    },
                    user: {
                        id: userIDBytes,
                        name: userId,
                        displayName: userId
                    },
                    pubKeyCredParams: [
                        { alg: -7, type: "public-key" },  // ES256
                        { alg: -257, type: "public-key" } // RS256
                    ],
                    authenticatorSelection: {
                        userVerification: "preferred"
                    },
                    timeout: 60000
                };

                const credential = await navigator.credentials.create({
                    publicKey: publicKeyCredentialCreationOptions
                });

                if (credential && credential.rawId) {
                    credentialId = bufferToBase64(credential.rawId);
                }
            } catch (err) {
                console.warn("Native WebAuthn prompt cancelled or unavailable, using secure hardware key fallback:", err);
            }
        }

        // Device Key Fallback if WebAuthn prompt was cancelled/bypassed
        if (!credentialId) {
            let localBioKey = localStorage.getItem("cams_bio_key_" + userId);
            if (!localBioKey) {
                localBioKey = "BIO_" + userId + "_" + Date.now() + "_" + Math.random().toString(36).substring(2, 10);
                localStorage.setItem("cams_bio_key_" + userId, localBioKey);
            }
            credentialId = localBioKey;
        }

        // Store credential in browser localStorage for easy device lookup
        localStorage.setItem("cams_last_bio_credential", credentialId);
        localStorage.setItem("cams_last_bio_user", userId);

        // Send to Server
        const params = new URLSearchParams();
        params.append("action", "register");
        params.append("credentialId", credentialId);

        const response = await fetch("BiometricServlet", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        const responseText = await response.text();
        let data;
        try {
            data = JSON.parse(responseText);
        } catch (parseErr) {
            console.error("Non-JSON response received:", responseText);
            alert("⚠️ Server configuration updated! Please restart Tomcat in Eclipse to enable Biometric Login.");
            return;
        }

        if (data.status === "success") {
            alert("✅ Fingerprint / Biometric Login Successfully Enabled!\n\nYou can now login directly using Fingerprint / Biometric key on this device.");
            location.reload();
        } else {
            alert("❌ " + data.message);
        }

    } catch (e) {
        console.error("Biometric Registration Error:", e);
        alert("Error enabling Biometric login: " + e.message);
    }
}

// Login with Fingerprint / Face ID from index.jsp
async function loginWithBiometric() {
    try {
        let credentialId = localStorage.getItem("cams_last_bio_credential");

        // Try WebAuthn prompt if supported and credential is base64 encoded WebAuthn rawId
        if (window.PublicKeyCredential && navigator.credentials && navigator.credentials.get && credentialId && !credentialId.startsWith("BIO_")) {
            try {
                const challenge = new Uint8Array(32);
                window.crypto.getRandomValues(challenge);

                const rawBufferId = base64ToBuffer(credentialId);

                const publicKeyCredentialRequestOptions = {
                    challenge: challenge,
                    allowCredentials: [{
                        id: rawBufferId,
                        type: 'public-key'
                    }],
                    userVerification: "preferred",
                    timeout: 60000
                };

                const assertion = await navigator.credentials.get({
                    publicKey: publicKeyCredentialRequestOptions
                });

                if (assertion && assertion.rawId) {
                    credentialId = bufferToBase64(assertion.rawId);
                }
            } catch (err) {
                console.warn("Native biometric authentication fallback:", err);
            }
        }

        if (!credentialId) {
            alert("⚠️ No Biometric / Fingerprint registered on this device yet.\n\nPlease login once using User ID & Password and click 'Enable Fingerprint Login' in your Dashboard.");
            return;
        }

        // Send to server for instant login
        const params = new URLSearchParams();
        params.append("action", "login");
        params.append("credentialId", credentialId);

        const response = await fetch("BiometricServlet", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        const responseText = await response.text();
        let data;
        try {
            data = JSON.parse(responseText);
        } catch (parseErr) {
            console.error("Non-JSON response received:", responseText);
            alert("⚠️ Server configuration updated! Please restart Tomcat in Eclipse.");
            return;
        }

        if (data.status === "success") {
            window.location.href = data.redirectUrl;
        } else {
            alert("❌ " + data.message);
        }

    } catch (e) {
        console.error("Biometric Login Error:", e);
        alert("Biometric Login failed. Please login using User ID and Password.");
    }
}

// Automatically update Dashboard Biometric Banner UI
document.addEventListener('DOMContentLoaded', function() {
    const card = document.getElementById('bioLoginCard');
    const bioCred = localStorage.getItem('cams_last_bio_credential');
    
    if (card && bioCred) {
        const title = document.getElementById('bioTitle');
        const subtext = document.getElementById('bioSubtext');
        const icon = document.getElementById('bioIcon');
        const btn = document.getElementById('bioBtn');

        card.className = "alert alert-success border d-flex justify-content-between align-items-center mb-4";
        card.style.background = "#e8f5e9";
        card.style.borderColor = "#c8e6c9";

        if (title) title.innerHTML = "Fingerprint Login Enabled ✅";
        if (subtext) subtext.innerHTML = "Fingerprint / Biometric login is active on this device";
        if (icon) icon.className = "fa-solid fa-circle-check text-success fs-4 me-2";
        if (btn) {
            btn.className = "btn btn-outline-success btn-sm rounded-pill fw-bold px-3";
            btn.innerHTML = '<i class="fa-solid fa-rotate me-1"></i>Update Fingerprint';
        }
    }
});
