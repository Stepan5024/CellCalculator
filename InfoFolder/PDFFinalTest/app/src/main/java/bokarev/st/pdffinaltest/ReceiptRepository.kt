package bokarev.st.pdffinaltest

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ReceiptRepository(private val receiptDao: ReceiptDao) {

    val allReceipts: Flow<MutableList<Receipt>> = receiptDao.getAllReceipts()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReceipt(receipt: Receipt) {
        receiptDao.insertReceipt(receipt)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(receipt)
    }


}