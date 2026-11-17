package com.casinoplatform

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun virtualCurrency_hasZeroMonetaryValue() {
        val virtualCoinValueInFiat = 0.0
        assertEquals(0.0, virtualCoinValueInFiat, 0.0)
    }
}
