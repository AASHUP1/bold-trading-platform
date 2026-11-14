import logging
import json
import os
from typing import Optional
from app.core.config import settings

logger = logging.getLogger("bold.firebase")

_firebase_initialized = False

def init_firebase():
    global _firebase_initialized
    if _firebase_initialized:
        return
    
    try:
        import firebase_admin
        from firebase_admin import credentials
        
        creds_json = settings.FIREBASE_CREDENTIALS_JSON
        if not creds_json:
            logger.warning("FIREBASE_CREDENTIALS_JSON not provided. Running FCM in sandbox/simulated mode.")
            return

        if isinstance(creds_json, dict):
            cred = credentials.Certificate(creds_json)
            firebase_admin.initialize_app(cred)
            _firebase_initialized = True
            logger.info("Firebase Admin SDK initialized successfully via parsed dict.")
        elif isinstance(creds_json, str):
            creds_str = creds_json.strip()
            if creds_str.startswith("{"):
                cred_dict = json.loads(creds_str)
                cred = credentials.Certificate(cred_dict)
                firebase_admin.initialize_app(cred)
                _firebase_initialized = True
                logger.info("Firebase Admin SDK initialized successfully via inline JSON string.")
            elif os.path.exists(creds_str):
                cred = credentials.Certificate(creds_str)
                firebase_admin.initialize_app(cred)
                _firebase_initialized = True
                logger.info("Firebase Admin SDK initialized successfully via service account JSON file.")
            else:
                logger.warning("FIREBASE_CREDENTIALS_JSON path not found. Running FCM in sandbox mode.")
    except Exception as e:
        logger.warning(f"Firebase initialization skipped: {e}. Running FCM in simulated mode.")

async def send_fcm_notification(
    token: str,
    title: str,
    body: str,
    data: Optional[dict] = None
) -> bool:
    """
    Dispatches a push notification to an Android device via Firebase Cloud Messaging.
    """
    init_firebase()
    
    if not _firebase_initialized:
        logger.info(f"[SIMULATED FCM] Push to {token[:12]}... | Title: '{title}' | Body: '{body}' | Data: {data}")
        return True

    try:
        from firebase_admin import messaging
        message = messaging.Message(
            notification=messaging.Notification(title=title, body=body),
            data={k: str(v) for k, v in (data or {}).items()},
            token=token,
            android=messaging.AndroidConfig(
                priority="high",
                notification=messaging.AndroidNotification(
                    channel_id="bold_trade_alerts",
                    color="#38D996",
                    sound="default"
                )
            )
        )
        response = messaging.send(message)
        logger.info(f"FCM push delivered successfully: {response}")
        return True
    except Exception as e:
        logger.error(f"FCM push failed: {e}")
        return False
