from pydantic import BaseModel
from typing import Optional, Dict

class FCMTokenRegistration(BaseModel):
    device_token: str

class PushNotificationPayload(BaseModel):
    title: str
    body: str
    topic: Optional[str] = None
    data: Optional[Dict[str, str]] = None
