from fastapi import FastAPI, Request, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager
from app.core.config import settings
from app.core.database import engine, Base
from app.core.firebase import init_firebase
from app.routers import auth, instruments, paper, algo, stream, notifications, admin
import logging

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger("bold.main")

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Initializing BOLD Trading Platform Backend...")
    # Initialize database tables if running in SQLite/Development mode
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    # Initialize Firebase Cloud Messaging
    init_firebase()
    logger.info("BOLD Backend API ready for live multi-user connections.")
    yield
    logger.info("Shutting down BOLD Trading Platform Backend...")
    await engine.dispose()

app = FastAPI(
    title=settings.APP_NAME,
    version="1.0.0",
    description="High-throughput asynchronous REST & WebSocket API for BOLD Mobile & Admin Console",
    lifespan=lifespan
)

# CORS configuration allowing mobile clients & admin console
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # In production, restrict to admin domain & mobile schemas
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Standard Unified Error Envelope Handler (Adhering to API.md Section 1.2)
@app.exception_handler(Exception)
async def unified_exception_handler(request: Request, exc: Exception):
    logger.error(f"Unhandled exception on {request.method} {request.url.path}: {exc}", exc_info=True)
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={
            "error": {
                "code": "INTERNAL_SERVER_ERROR",
                "message": str(exc) if settings.APP_ENV != "production" else "An unexpected institutional engine error occurred.",
                "status_code": 500,
                "path": request.url.path
            }
        }
    )

# Mount Routers
app.include_router(auth.router)
app.include_router(instruments.router)
app.include_router(paper.router)
app.include_router(algo.router)
app.include_router(stream.router)
app.include_router(notifications.router)
app.include_router(admin.router)

@app.get("/")
async def root():
    return {
        "platform": "BOLD Institutional Trading Engine",
        "version": "1.0.0",
        "status": "ONLINE",
        "docs_url": "/docs",
        "free_launch_mandate": "100% Free at Launch"
    }

@app.get("/v1/market/status")
async def market_status():
    return {
        "status": "ONLINE",
        "exchange": "NSE/BSE",
        "session": "REGULAR",
        "kill_switch_active": False,
        "latency_target_ms": "<50ms"
    }
