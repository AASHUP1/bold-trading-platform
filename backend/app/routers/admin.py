from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.core.database import get_db
from app.models.admin import FeatureFlag, ApiKeyVault, FeedHealthLog, AuditEvent
from pydantic import BaseModel
from typing import Optional
from datetime import datetime, timezone
import uuid

router = APIRouter(prefix="/v1/admin", tags=["Admin & Compliance Control Room"])

class KillSwitchToggle(BaseModel):
    is_enabled: bool
    reason: str

class VaultKeyUpdate(BaseModel):
    provider: str
    key_name: str
    secret_value: str

@router.get("/kill-switch")
async def get_kill_switch(db: AsyncSession = Depends(get_db)):
    stmt = select(FeatureFlag).where(FeatureFlag.flag_key == "GLOBAL_KILL_SWITCH")
    res = await db.execute(stmt)
    flag = res.scalar_one_or_none()
    return {"is_enabled": flag.is_enabled if flag else False}

@router.post("/kill-switch")
async def toggle_kill_switch(payload: KillSwitchToggle, db: AsyncSession = Depends(get_db)):
    stmt = select(FeatureFlag).where(FeatureFlag.flag_key == "GLOBAL_KILL_SWITCH")
    res = await db.execute(stmt)
    flag = res.scalar_one_or_none()
    if not flag:
        flag = FeatureFlag(
            id=str(uuid.uuid4()),
            flag_key="GLOBAL_KILL_SWITCH",
            is_enabled=payload.is_enabled,
            description="Global trading kill switch"
        )
        db.add(flag)
    else:
        flag.is_enabled = payload.is_enabled

    audit = AuditEvent(
        id=str(uuid.uuid4()),
        event_hash=uuid.uuid4().hex,
        actor_email="admin@boldtrading.internal",
        action_category="KILL_SWITCH_MUTATION",
        target_resource="GLOBAL_KILL_SWITCH",
        state_after={"is_enabled": payload.is_enabled, "reason": payload.reason}
    )
    db.add(audit)
    await db.commit()
    return {"status": "success", "is_enabled": payload.is_enabled}

@router.get("/telemetry/feeds")
async def get_feed_telemetry():
    return [
        {"id": "zerodha", "name": "Zerodha Kite Connect", "status": "HEALTHY", "latency_ms": 142, "tokens_remaining_hours": 14},
        {"id": "upstox", "name": "Upstox Developer API", "status": "HEALTHY", "latency_ms": 185, "tokens_remaining_hours": 16},
        {"id": "angelone", "name": "Angel One SmartAPI", "status": "HEALTHY", "latency_ms": 210, "tokens_remaining_hours": 18},
        {"id": "dhan", "name": "Dhan HQ API", "status": "HEALTHY", "latency_ms": 120, "tokens_remaining_hours": 22},
        {"id": "fyers", "name": "Fyers API v3", "status": "HEALTHY", "latency_ms": 155, "tokens_remaining_hours": 19}
    ]
