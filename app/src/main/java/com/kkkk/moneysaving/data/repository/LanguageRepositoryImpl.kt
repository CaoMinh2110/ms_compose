package com.kkkk.moneysaving.data.repository

import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Language
import com.kkkk.moneysaving.domain.repository.LanguageRepository
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor() : LanguageRepository {
    override fun getSupportedLanguages(): List<Language> {
        return listOf(
            Language(code = "en", displayName = "English", R.drawable.ic_flag_gb),
            Language(code = "vi", displayName = "Tiếng Việt", R.drawable.ic_flag_vn),
        )
    }
}

