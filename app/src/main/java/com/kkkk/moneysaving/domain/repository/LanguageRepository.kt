package com.kkkk.moneysaving.domain.repository

import com.kkkk.moneysaving.domain.model.Language

interface LanguageRepository {
    fun getSupportedLanguages(): List<Language>
}

