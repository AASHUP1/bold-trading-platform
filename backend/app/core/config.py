from pydantic_settings import BaseSettings
from typing import Optional, Any
import os

class Settings(BaseSettings):
    APP_NAME: str = "BOLD Trading Platform API"
    APP_ENV: str = "production"
    API_V1_PREFIX: str = "/v1"
    
    # Database URL: Defaults to local SQLite, or overrides with remote PostgreSQL
    DATABASE_URL: str = os.getenv("DATABASE_URL", "sqlite+aiosqlite:///./bold_backend.db")
    
    # Redis for Kill Switch & Tick Streaming Pub/Sub
    REDIS_URL: Optional[str] = os.getenv("REDIS_URL", None)
    
    # JWT Auth Configuration
    JWT_SECRET: str = os.getenv("JWT_SECRET", "bold_jwt_release_2026_quant_secret")
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 30 # 30 days session
    
    # Firebase Cloud Messaging (FCM) Settings
    FIREBASE_PROJECT_ID: Optional[str] = os.getenv("FIREBASE_PROJECT_ID", "trading-app-19c14")
    # Accepts string file path, raw JSON string, or dict parsed by pydantic
    FIREBASE_CREDENTIALS_JSON: Optional[Any] = None
    
    # Mandatory Free Launch Policy
    FREE_LAUNCH_ACTIVE: bool = True
    VIRTUAL_CAPITAL_DEFAULT: float = 1_000_000.00 # ₹10,00,000

    class Config:
        case_sensitive = True
        extra = "ignore" # Prevent crash on any extra environment variables

settings = Settings()
