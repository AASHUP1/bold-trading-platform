from pydantic import BaseModel, Field
from typing import Optional, Dict

class OTPRequest(BaseModel):
    phone: str = Field(..., example="+919876543210")

class OTPRequestResponse(BaseModel):
    message: str = "OTP dispatched successfully."
    expires_in_seconds: int = 120
    retry_after_seconds: int = 30

class OTPVerify(BaseModel):
    phone: str = Field(..., example="+919876543210")
    otp: str = Field(..., example="123456")

class UserProfile(BaseModel):
    id: str
    phone: str
    email: Optional[str] = None
    full_name: Optional[str] = None
    status: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "Bearer"
    expires_in: int
    is_new_user: bool = False
    user: UserProfile

class EntitlementsResponse(BaseModel):
    tier: str = "FREE_PRO_UNLOCKED"
    is_free_launch_active: bool = True
    features: Dict[str, bool] = {
        "scanner_unlimited": True,
        "fno_option_chain": True,
        "strategy_builder": True,
        "ai_assistant_monthly_queries": True
    }
