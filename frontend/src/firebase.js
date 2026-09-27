// Import Firebase
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth, GoogleAuthProvider } from "firebase/auth";

// Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyDU9ejlxXkg2qfm7yX0_hs_sH0Pi5S2AtI",
    authDomain: "cp353002-lost-and-found.firebaseapp.com",
    projectId: "cp353002-lost-and-found",
    storageBucket: "cp353002-lost-and-found.firebasestorage.app",
    messagingSenderId: "803926199464",
    appId: "1:803926199464:web:64ed8eed7928e60b61069d",
    measurementId: "G-WLRS3CEG4G",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Analytics
const analytics = getAnalytics(app);

// Authentication
export const auth = getAuth(app);

// Google Login Provider
export const googleProvider = new GoogleAuthProvider();
