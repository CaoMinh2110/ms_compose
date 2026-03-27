package com.kkkk.moneysaving.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kkkk.moneysaving.data.local.dao.DeletedItemDao
import com.kkkk.moneysaving.data.local.entity.DeletedItemType
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DatabaseCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val deletedItemDao: DeletedItemDao,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val itemsToDelete = deletedItemDao.getDeletedItemsSync()
            
            itemsToDelete.forEach { item ->
                when (item.type) {
                    DeletedItemType.TRANSACTION -> {
                        transactionRepository.hardDelete(item.id)
                    }
                    DeletedItemType.BUDGET -> {
                        budgetRepository.hardDelete(item.id)
                    }
                }
                deletedItemDao.delete(item)
            }
            
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
