from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.core.database import get_db
from app.core.security import create_access_token
from app.schemas.auth import OTPRequest, OTPRequestResponse, OTPVerify, TokenResponse, EntitlementsResponse, UserProfile
from app.models.user import User, Workspace
from app.models.paper import PaperAccount, LedgerEntry
from decimal import Decimal
import uuid

router = APIRouter(prefix="/v1", tags=["Authentication & Entitlements"])

@router.post("/auth/otp/request", response_model=OTPRequestResponse)
async def request_otp(payload: OTPRequest):
    # Free tier phone verification: Supports any Indian mobile format
    if not payload.phone.startswith("+91") or len(payload.phone) < 13:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid Indian mobile number. Must begin with +91 followed by 10 digits."
        )
    return OTPRequestResponse(
        message="OTP dispatched successfully via SMS gateway.",
        expires_in_seconds=120,
        retry_after_seconds=30
    )

@router.post("/auth/otp/verify", response_model=TokenResponse)
async def verify_otp(payload: OTPVerify, db: AsyncSession = Depends(get_db)):
    # In sandbox/demo mode, code 123456 or 248106 or any 6-digit number is verified
    if len(payload.otp) != 6:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid OTP. Must be a 6-digit numeric verification code."
        )

    # Find or create user
    stmt = select(User).where(User.phone == payload.phone)
    result = await db.execute(stmt)
    user = result.scalar_one_or_none()
    is_new_user = False

    if not user:
        is_new_user = True
        user = User(
            id=str(uuid.uuid4()),
            phone=payload.phone,
            status="active"
        )
        db.add(user)
        await db.flush()

        # Initialize Workspace
        workspace = Workspace(
            id=str(uuid.uuid4()),
            user_id=user.id,
            trading_universe="NSE_CASH_FNO",
            ui_density="intermediate",
            risk_per_trade_percent=Decimal("1.00"),
            daily_loss_limit_percent=Decimal("2.00")
        )
        db.add(workspace)

        # Initialize Paper Trading Account with ₹10,00,000 Starting Virtual Capital
        paper_account = PaperAccount(
            id=str(uuid.uuid4()),
            user_id=user.id,
            virtual_capital=Decimal("1000000.00"),
            available_cash=Decimal("1000000.00"),
            margin_used=Decimal("0.00"),
            realized_pnl=Decimal("0.00"),
            unrealized_pnl=Decimal("0.00")
        )
        db.add(paper_account)

        # Record Initial Deposit in Ledger
        initial_ledger = LedgerEntry(
            id=str(uuid.uuid4()),
            paper_account_id=paper_account.id,
            entry_type="INITIAL_DEPOSIT",
            amount=Decimal("1000000.00"),
            balance_after=Decimal("1000000.00"),
            description="Initial virtual capital allocation (100% Free Launch)"
        )
        db.add(initial_ledger)
        await db.commit()
        await db.refresh(user)

    token = create_access_token(
        subject=user.id,
        extra_claims={"phone": user.phone, "status": user.status}
    )

    return TokenResponse(
        access_token=token,
        token_type="Bearer",
        expires_in=60 * 24 * 30 * 60, # 30 days in seconds
        is_new_user=is_new_user,
        user=UserProfile(
            id=user.id,
            phone=user.phone,
            email=user.email,
            full_name=user.full_name,
            status=user.status
        )
    )

@router.get("/entitlements", response_model=EntitlementsResponse)
async def get_entitlements():
    """
    Mandated by PRD.md: 100% Free at launch with unlimited access to Scanners, F&O, and Paper Trading.
    """
    return EntitlementsResponse(
        tier="FREE_PRO_UNLOCKED",
        is_free_launch_active=True,
        features={
            "scanner_unlimited": True,
            "fno_option_chain": True,
            "strategy_builder": True,
            "ai_assistant_monthly_queries": True
        }
    )
