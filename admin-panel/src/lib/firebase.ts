import { initializeApp, getApps, type FirebaseApp } from 'firebase/app';
import { getAuth, type Auth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyA_DEMO_KEY_FOR_LOCAL_DEV",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "bold-trading-admin.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "bold-trading-admin",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "bold-trading-admin.appspot.com",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "123456789012",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:123456789012:web:abcdef123456"
};

let app: FirebaseApp;
let auth: Auth;

try {
  app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApps()[0];
  auth = getAuth(app);
} catch (error) {
  console.warn("Firebase initialized in local mock fallback mode:", error);
  auth = {} as unknown as Auth;
}

export { app, auth };
