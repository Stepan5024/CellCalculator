package bokarev.st.pdffinaltest

import androidx.lifecycle.*

import kotlinx.coroutines.*

class ReceiptViewModel(private val receiptRepository: ReceiptRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val loadingStatus = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private var job: Job? = null


    fun updateReceipt(
        receipt: Receipt
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                if (receipt.filePath.isNotEmpty()) {
                    loadingStatus.postValue(true)
                    viewModelScope.launch(Dispatchers.IO) {
                        receiptRepository.updateReceipt(
                            receipt
                        )
                        onError("Receipt saved")
                    }
                } else {
                    onError("File Path not saved")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loadingStatus.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


    val allReceiptRealTime: LiveData<MutableList<Receipt>> =
        receiptRepository.allReceipts.asLiveData()

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insertReceipt(user: Receipt) = viewModelScope.launch {
        receiptRepository.insertReceipt(user)
    }


    class ReceiptViewModelFactory(private val receiptRepository: ReceiptRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReceiptViewModel(receiptRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }

    }
}
