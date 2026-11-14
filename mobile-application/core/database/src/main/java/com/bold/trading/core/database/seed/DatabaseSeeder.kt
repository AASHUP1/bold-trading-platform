package com.bold.trading.core.database.seed

import com.bold.trading.core.database.entity.CachedCandleEntity
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.CachedWatchlistEntity
import com.bold.trading.core.database.entity.CachedWatchlistItemEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import java.math.BigDecimal

object DatabaseSeeder {

    val INITIAL_INSTRUMENTS = listOf(
        CachedInstrumentEntity(
            id = "inst-nifty50",
            exchange = "NSE",
            symbol = "NIFTY 50",
            name = "NIFTY 50 Benchmark Index",
            instrumentType = "index",
            tickSize = BigDecimal("0.05"),
            lotSize = 25,
            lastTradedPrice = BigDecimal("24360.50"),
            change = BigDecimal("156.80"),
            changePercent = BigDecimal("0.65"),
            high24h = BigDecimal("24410.00"),
            low24h = BigDecimal("24205.20"),
            volume = 342000000L
        ),
        CachedInstrumentEntity(
            id = "inst-banknifty",
            exchange = "NSE",
            symbol = "BANKNIFTY",
            name = "NIFTY Bank Index",
            instrumentType = "index",
            tickSize = BigDecimal("0.05"),
            lotSize = 15,
            lastTradedPrice = BigDecimal("51820.00"),
            change = BigDecimal("218.40"),
            changePercent = BigDecimal("0.42"),
            high24h = BigDecimal("52010.00"),
            low24h = BigDecimal("51600.00"),
            volume = 185000000L
        ),
        CachedInstrumentEntity(
            id = "inst-reliance",
            exchange = "NSE",
            symbol = "RELIANCE",
            name = "Reliance Industries Ltd",
            instrumentType = "equity",
            tickSize = BigDecimal("0.05"),
            lotSize = 1,
            lastTradedPrice = BigDecimal("2980.20"),
            change = BigDecimal("33.80"),
            changePercent = BigDecimal("1.15"),
            high24h = BigDecimal("2995.00"),
            low24h = BigDecimal("2942.10"),
            volume = 8400000L
        ),
        CachedInstrumentEntity(
            id = "inst-hdfcbank",
            exchange = "NSE",
            symbol = "HDFCBANK",
            name = "HDFC Bank Ltd",
            instrumentType = "equity",
            tickSize = BigDecimal("0.05"),
            lotSize = 1,
            lastTradedPrice = BigDecimal("1640.75"),
            change = BigDecimal("-4.95"),
            changePercent = BigDecimal("-0.30"),
            high24h = BigDecimal("1655.00"),
            low24h = BigDecimal("1632.50"),
            volume = 12100000L
        ),
        CachedInstrumentEntity(
            id = "inst-infy",
            exchange = "NSE",
            symbol = "INFY",
            name = "Infosys Ltd",
            instrumentType = "equity",
            tickSize = BigDecimal("0.05"),
            lotSize = 1,
            lastTradedPrice = BigDecimal("1865.40"),
            change = BigDecimal("15.70"),
            changePercent = BigDecimal("0.85"),
            high24h = BigDecimal("1875.00"),
            low24h = BigDecimal("1842.00"),
            volume = 6500000L
        ),
        CachedInstrumentEntity(
            id = "inst-tcs",
            exchange = "NSE",
            symbol = "TCS",
            name = "Tata Consultancy Services Ltd",
            instrumentType = "equity",
            tickSize = BigDecimal("0.05"),
            lotSize = 1,
            lastTradedPrice = BigDecimal("4120.00"),
            change = BigDecimal("20.50"),
            changePercent = BigDecimal("0.50"),
            high24h = BigDecimal("4145.00"),
            low24h = BigDecimal("4090.00"),
            volume = 4200000L
        ),
        CachedInstrumentEntity(
            id = "inst-tatamotors",
            exchange = "NSE",
            symbol = "TATAMOTORS",
            name = "Tata Motors Ltd",
            instrumentType = "equity",
            tickSize = BigDecimal("0.05"),
            lotSize = 1,
            lastTradedPrice = BigDecimal("940.10"),
            change = BigDecimal("19.30"),
            changePercent = BigDecimal("2.10"),
            high24h = BigDecimal("948.00"),
            low24h = BigDecimal("918.50"),
            volume = 15600000L
        )
    )

    val INITIAL_PAPER_ACCOUNT = PaperAccountEntity(
        id = "account-default",
        userId = "user-default",
        virtualCapital = BigDecimal("1000000.00"),
        availableCash = BigDecimal("1000000.00"),
        marginUsed = BigDecimal("0.00"),
        realizedPnl = BigDecimal("0.00"),
        unrealizedPnl = BigDecimal("0.00")
    )

    val INITIAL_LEDGER_ENTRY = LedgerEntryEntity(
        id = "ledger-init-001",
        paperAccountId = "account-default",
        entryType = "INITIAL_DEPOSIT",
        amount = BigDecimal("1000000.00"),
        balanceAfter = BigDecimal("1000000.00"),
        description = "Initial virtual capital allocation (100% Free Launch)"
    )

    val INITIAL_WATCHLIST = CachedWatchlistEntity(
        id = "wl-nifty-core",
        name = "NIFTY Core",
        sortOrder = 0,
        isDefault = true
    )

    val INITIAL_WATCHLIST_ITEMS = INITIAL_INSTRUMENTS.mapIndexed { idx, inst ->
        CachedWatchlistItemEntity(
            watchlistId = "wl-nifty-core",
            instrumentId = inst.id,
            symbol = inst.symbol,
            sortOrder = idx
        )
    }

    val INITIAL_CANDLES: List<CachedCandleEntity> = run {
        val list = mutableListOf<CachedCandleEntity>()
        val now = 1731580800000L // Deterministic timestamp base
        var currentPrice = BigDecimal("24250.00")
        for (i in 40 downTo 1) {
            val ts = now - (i * 15 * 60 * 1000L)
            val open = currentPrice
            val high = open.add(BigDecimal("25.00"))
            val low = open.subtract(BigDecimal("18.00"))
            val close = open.add(BigDecimal("12.00"))
            currentPrice = close
            list.add(
                CachedCandleEntity(
                    symbol = "NIFTY 50",
                    timeframe = "15m",
                    timestamp = ts,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = 450000L,
                    vwap = open.add(BigDecimal("5.00"))
                )
            )
        }
        list
    }
}
