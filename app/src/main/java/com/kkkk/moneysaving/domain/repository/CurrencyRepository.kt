package com.kkkk.moneysaving.domain.repository

import com.kkkk.moneysaving.domain.model.Currency

interface CurrencyRepository {
    fun getSupportedCurrencies(): List<Currency>
}

