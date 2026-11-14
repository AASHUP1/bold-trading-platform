package com.bold.trading

import android.app.Application
import com.bold.trading.core.database.BoldDatabase
import com.bold.trading.core.networking.ticker.MarketTickEngine

class BoldApplication : Application() {

    lateinit var database: BoldDatabase
        private set

    lateinit var tickEngine: MarketTickEngine
        private set

    override fun onCreate() {
        super.onCreate()
        // Initialize Room Database with pre-seeded data
        database = BoldDatabase.getInstance(this)

        // Start dynamic live tick simulation engine (every 1.5s)
        tickEngine = MarketTickEngine(database)
        tickEngine.startStreaming()
    }
}
