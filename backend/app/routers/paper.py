from fastapi import APIRouter, Depends, HTTPException, Header, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.core.database import get_db
from app.schemas.paper import CreatePaperOrder, PaperOrderResponse, PaperPositionResponse, LedgerResponse, PaperAccountResponse
from app.models.paper import PaperAccount, PaperOrder, PaperPosition, LedgerEntry
from app.models.user import Workspace
from app.services.risk_engine import RiskEngineService
from app.services.paper_engine import PaperExecutionService
from app.services.fcm_service import FCMNotificationService
from decimal import Decimal
from datetime import datetime, timezone
import uuid

router = APIRouter(prefix="/v1/paper", tags=["Paper Trading & Virtual Ledger"])

@router.post("/orders", response_model=PaperOrderResponse)
async def create_paper_order(
    payload: CreatePaperOrder,
    x_idempotency_key: str = Header(..., description="Mandatory idempotency key from mobile client"),
    db: AsyncSession = Depends(get_db)
):
    # Check for existing idempotency key
    stmt = select(PaperOrder).where(PaperOrder.client_order_id == x_idempotency_key)
    res = await db.execute(stmt)
    existing_order = res.scalar_one_or_none()
    if existing_order:
        return PaperOrderResponse(
            id=existing_order.id,
            client_order_id=existing_order.client_order_id,
            symbol=payload.symbol,
            direction=existing_order.direction,
            order_type=existing_order.order_type,
            quantity=existing_order.quantity,
            filled_quantity=existing_order.filled_quantity,
            average_fill_price=existing_order.average_fill_price,
            status=existing_order.status,
            rejection_reason=existing_order.rejection_reason,
            placed_at=existing_order.placed_at.isoformat(),
            filled_at=existing_order.filled_at.isoformat() if existing_order.filled_at else None
        )

    # Fetch default or first paper account
    account_stmt = select(PaperAccount).limit(1)
    acc_res = await db.execute(account_stmt)
    account = acc_res.scalar_one_or_none()
    if not account:
        account = PaperAccount(
            id=str(uuid.uuid4()),
            user_id="c0000000-0000-0000-0000-000000000001",
            virtual_capital=Decimal("1000000.00"),
            available_cash=Decimal("1000000.00")
        )
        db.add(account)
        await db.flush()

    fill_price = payload.requested_price or Decimal("24360.50")
    if payload.symbol == "RELIANCE":
        fill_price = Decimal("2980.20")
    elif payload.symbol == "INFY":
        fill_price = Decimal("1865.40")

    # Server-Side Risk Validation (1.0% trade budget, 2.0% daily circuit breaker)
    passed, reason = RiskEngineService.validate_order(
        virtual_capital=account.virtual_capital,
        available_cash=account.available_cash,
        daily_realized_loss=account.realized_pnl,
        price=fill_price,
        quantity=payload.quantity,
        stop_loss=payload.stop_loss
    )

    if not passed:
        # Record rejected order
        rejected_order = PaperOrder(
            id=str(uuid.uuid4()),
            paper_account_id=account.id,
            instrument_id=payload.instrument_id,
            client_order_id=x_idempotency_key,
            direction=payload.direction,
            order_type=payload.order_type,
            quantity=payload.quantity,
            requested_price=payload.requested_price,
            status="REJECTED",
            rejection_reason=reason
        )
        db.add(rejected_order)
        await db.commit()

        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail={"code": "RISK_BUDGET_EXCEEDED", "message": reason}
        )

    # Execute paper order & compute charges
    new_cash, charges, receipt_id = PaperExecutionService.execute_market_order(
        direction=payload.direction,
        price=fill_price,
        quantity=payload.quantity,
        current_cash=account.available_cash
    )

    account.available_cash = new_cash
    account.margin_used += (fill_price * Decimal(payload.quantity)) if payload.direction == "BUY" else Decimal("0.00")

    order = PaperOrder(
        id=str(uuid.uuid4()),
        paper_account_id=account.id,
        instrument_id=payload.instrument_id,
        client_order_id=x_idempotency_key,
        direction=payload.direction,
        order_type=payload.order_type,
        quantity=payload.quantity,
        requested_price=payload.requested_price,
        stop_loss=payload.stop_loss,
        target=payload.target,
        status="FILLED",
        filled_quantity=payload.quantity,
        average_fill_price=fill_price,
        filled_at=datetime.now(timezone.utc)
    )
    db.add(order)

    # Double-Entry Ledger Entry
    ledger = LedgerEntry(
        id=str(uuid.uuid4()),
        paper_account_id=account.id,
        entry_type=f"{payload.direction}_FILL",
        amount=-(fill_price * Decimal(payload.quantity) + charges) if payload.direction == "BUY" else (fill_price * Decimal(payload.quantity) - charges),
        balance_after=new_cash,
        reference_id=receipt_id,
        description=f"Simulated {payload.direction} {payload.quantity}x {payload.symbol} @ ₹{fill_price:,.2f} (Charges: ₹{charges:,.2f})"
    )
    db.add(ledger)
    await db.commit()

    # Trigger Firebase Push Notification
    # Fetch device token from workspace if registered
    ws_stmt = select(Workspace).where(Workspace.user_id == account.user_id)
    ws_res = await db.execute(ws_stmt)
    workspace = ws_res.scalar_one_or_none()
    if workspace and workspace.fcm_device_token:
        await FCMNotificationService.notify_order_filled(
            fcm_token=workspace.fcm_device_token,
            symbol=payload.symbol,
            direction=payload.direction,
            quantity=payload.quantity,
            fill_price=fill_price,
            receipt_id=receipt_id
        )

    return PaperOrderResponse(
        id=order.id,
        client_order_id=order.client_order_id,
        symbol=payload.symbol,
        direction=order.direction,
        order_type=order.order_type,
        quantity=order.quantity,
        filled_quantity=order.filled_quantity,
        average_fill_price=order.average_fill_price,
        status=order.status,
        placed_at=order.placed_at.isoformat(),
        filled_at=order.filled_at.isoformat()
    )

@router.get("/account", response_model=PaperAccountResponse)
async def get_paper_account(db: AsyncSession = Depends(get_db)):
    stmt = select(PaperAccount).limit(1)
    res = await db.execute(stmt)
    account = res.scalar_one_or_none()
    if not account:
        return PaperAccountResponse(
            id="account-default",
            virtual_capital=Decimal("1000000.00"),
            available_cash=Decimal("1000000.00"),
            margin_used=Decimal("0.00"),
            realized_pnl=Decimal("0.00"),
            unrealized_pnl=Decimal("0.00"),
            reset_count=0
        )
    return PaperAccountResponse(
        id=account.id,
        virtual_capital=account.virtual_capital,
        available_cash=account.available_cash,
        margin_used=account.margin_used,
        realized_pnl=account.realized_pnl,
        unrealized_pnl=account.unrealized_pnl,
        reset_count=account.reset_count
    )

@router.get("/ledger", response_model=list[LedgerResponse])
async def get_paper_ledger(db: AsyncSession = Depends(get_db)):
    stmt = select(LedgerEntry).order_by(LedgerEntry.created_at.desc()).limit(50)
    res = await db.execute(stmt)
    entries = res.scalars().all()
    return [
        LedgerResponse(
            id=e.id,
            entry_type=e.entry_type,
            amount=e.amount,
            balance_after=e.balance_after,
            reference_id=e.reference_id,
            description=e.description,
            created_at=e.created_at.isoformat()
        )
        for e in entries
    ]

@router.post("/account/reset", response_model=PaperAccountResponse)
async def reset_paper_account(db: AsyncSession = Depends(get_db)):
    stmt = select(PaperAccount).limit(1)
    res = await db.execute(stmt)
    account = res.scalar_one_or_none()
    if account:
        account.available_cash = Decimal("1000000.00")
        account.virtual_capital = Decimal("1000000.00")
        account.margin_used = Decimal("0.00")
        account.realized_pnl = Decimal("0.00")
        account.unrealized_pnl = Decimal("0.00")
        account.reset_count += 1
        account.last_reset_at = datetime.now(timezone.utc)

        reset_ledger = LedgerEntry(
            id=str(uuid.uuid4()),
            paper_account_id=account.id,
            entry_type="ACCOUNT_RESET",
            amount=Decimal("1000000.00"),
            balance_after=Decimal("1000000.00"),
            description="Paper trading portfolio reset to initial ₹10,00,000 virtual balance"
        )
        db.add(reset_ledger)
        await db.commit()
        await db.refresh(account)

        return PaperAccountResponse(
            id=account.id,
            virtual_capital=account.virtual_capital,
            available_cash=account.available_cash,
            margin_used=account.margin_used,
            realized_pnl=account.realized_pnl,
            unrealized_pnl=account.unrealized_pnl,
            reset_count=account.reset_count
        )
    raise HTTPException(status_code=404, detail="Paper account not found")
