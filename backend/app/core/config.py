from pydantic_settings import BaseSettings
from typing import Optional
import os

class Settings(BaseSettings):
    APP_NAME: str = "BOLD Trading Platform API"
    APP_ENV: str = "production"
    API_V1_PREFIX: str = "/v1"
    
    # Database URL: Defaults to local PostgreSQL, falls back to SQLite if PG is unavailable
    DATABASE_URL: str = os.getenv("DATABASE_URL", "sqlite+aiosqlite:///./bold_backend.db")
    
    # Redis for Kill Switch & Tick Streaming Pub/Sub
    REDIS_URL: Optional[str] = os.getenv("REDIS_URL", None)
    
    # JWT Auth Configuration
    JWT_SECRET: str = os.getenv("JWT_SECRET", "bold_super_secret_jwt_key_2026_institutional_grade")
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 30 # 30 days session
    
    # Firebase Cloud Messaging (FCM) Settings
    FIREBASE_PROJECT_ID: Optional[str] = os.getenv("FIREBASE_PROJECT_ID", "trading-app-19c14")
    FIREBASE_CREDENTIALS_JSON: Optional[str] = os.getenv("FIREBASE_CREDENTIALS_JSON", "firebase-service-account.json")
    
    # Mandatory Free Launch Policy
    FREE_LAUNCH_ACTIVE: bool = True
    VIRTUAL_CAPITAL_DEFAULT: float = 1_000_000.00 # ₹10,00,000

    class Config:
        case_sensitive = True

settings = Settings()
