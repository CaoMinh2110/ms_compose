package com.kkkk.moneysaving.di

import com.kkkk.moneysaving.data.repository.AppStartRepositoryImpl
import com.kkkk.moneysaving.data.repository.AuthRepositoryImpl
import com.kkkk.moneysaving.data.repository.BudgetRepositoryImpl
import com.kkkk.moneysaving.data.repository.CategoryRepositoryImpl
import com.kkkk.moneysaving.data.repository.CurrencyRepositoryImpl
import com.kkkk.moneysaving.data.repository.ImageRepositoryImpl
import com.kkkk.moneysaving.data.repository.LanguageRepositoryImpl
import com.kkkk.moneysaving.data.repository.SettingsRepositoryImpl
import com.kkkk.moneysaving.data.repository.TransactionRepositoryImpl
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.repository.CurrencyRepository
import com.kkkk.moneysaving.domain.repository.ImageRepository
import com.kkkk.moneysaving.domain.repository.LanguageRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAppStartRepository(impl: AppStartRepositoryImpl): AppStartRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(impl: LanguageRepositoryImpl): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(impl: ImageRepositoryImpl): ImageRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
}
