package com.bold.trading.core.database

import com.bold.trading.core.database.seed.DatabaseSeeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class DatabaseSeederTest {

    @Test
    fun verifySeeder_containsLiquidCoreNSEInstruments() {
        val instruments = DatabaseSeeder.INITIAL_INSTRUMENTS
        assertTrue("Expected at least 5 core instruments", instruments.size >= 5)

        val nifty = instruments.find { it.symbol == "NIFTY 50" }
        assertNotNull("NIFTY 50 must exist in seed", nifty)
        assertEquals(25, nifty?.lotSize)

        val bankNifty = instruments.find { it.symbol == "BANKNIFTY" }
        assertNotNull("BANKNIFTY must exist in seed", bankNifty)
        assertEquals(15, bankNifty?.lotSize)

        val reliance = instruments.find { it.symbol == "RELIANCE" }
        assertNotNull("RELIANCE must exist in seed", reliance)
        assertEquals(BigDecimal("0.05"), reliance?.tickSize)
    }

    @Test
    fun verifySeeder_initializesTenLakhsVirtualCapital() {
        val account = DatabaseSeeder.INITIAL_PAPER_ACCOUNT
        assertEquals(BigDecimal("1000000.00"), account.virtualCapital)
        assertEquals(BigDecimal("1000000.00"), account.availableCash)
        assertEquals(BigDecimal("0.00"), account.marginUsed)

        val ledger = DatabaseSeeder.INITIAL_LEDGER_ENTRY
        assertEquals("INITIAL_DEPOSIT", ledger.entryType)
        assertEquals(BigDecimal("1000000.00"), ledger.amount)
        assertEquals(BigDecimal("1000000.00"), ledger.balanceAfter)
    }
}
