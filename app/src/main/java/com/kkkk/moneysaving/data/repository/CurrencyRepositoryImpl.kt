package com.kkkk.moneysaving.data.repository

import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Currency
import com.kkkk.moneysaving.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {
    override fun getSupportedCurrencies(): List<Currency> {
        return listOf(
            Currency(code = "USD", displayName = "United State Dollar", "$", R.drawable.ic_flag_gb),
            Currency(code = "VND", displayName = "Vietnamese Dong", "đ", R.drawable.ic_flag_vn),
        )
    }
}

