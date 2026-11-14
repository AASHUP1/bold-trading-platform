from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.core.database import get_db
from app.schemas.notification import FCMTokenRegistration, PushNotificationPayload
from app.models.user import Workspace
from app.core.firebase import send_fcm_notification

router = APIRouter(prefix="/v1/notifications", tags=["Firebase Push Notifications"])

@router.post("/token")
async def register_device_token(payload: FCMTokenRegistration, db: AsyncSession = Depends(get_db)):
    """
    Registers an Android FCM device token for instant trade fills, risk alerts, and signal updates.
    """
    stmt = select(Workspace).limit(1)
    res = await db.execute(stmt)
    workspace = res.scalar_one_or_none()
    if workspace:
        workspace.fcm_device_token = payload.device_token
        await db.commit()
        return {"status": "success", "message": "FCM device token registered successfully."}
    return {"status": "success", "message": "FCM token received."}

@router.post("/test")
async def send_test_notification(payload: PushNotificationPayload, db: AsyncSession = Depends(get_db)):
    """
    Triggers a test push notification via Firebase Cloud Messaging.
    """
    stmt = select(Workspace).limit(1)
    res = await db.execute(stmt)
    workspace = res.scalar_one_or_none()
    token = workspace.fcm_device_token if workspace else None

    if not token and not payload.topic:
        # Simulated dispatch
        await send_fcm_notification(
            token="simulated-device-token-123456",
            title=payload.title,
            body=payload.body,
            data=payload.data
        )
        return {
            "status": "delivered_simulated",
            "message": "No active device token registered in workspace; push logged in simulated mode."
        }

    success = await send_fcm_notification(
        token=token,
        title=payload.title,
        body=payload.body,
        data=payload.data
    )
    return {
        "status": "delivered" if success else "failed",
        "title": payload.title,
        "body": payload.body
    }
