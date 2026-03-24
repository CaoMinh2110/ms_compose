package com.kkkk.moneysaving.domain.repository

import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType

interface CategoryRepository {
    fun getAll(): List<Category>
    fun getById(id: String): Category?
    fun getType(id: String): CategoryType?
}

