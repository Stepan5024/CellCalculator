package bokarev.st.stretchceilingcalculator


import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import bokarev.st.stretchceilingcalculator.entities.Client

class ClientsViewModel(app: Application) : AndroidViewModel(app) {
    private var allUsers: MutableLiveData<List<Client>> = MutableLiveData()

    init {
        //lifecycleScope.launch {
        getAllUsers()
        //}
    }

    fun getAllUsersObservers(): MutableLiveData<List<Client>> {
        return allUsers
    }

     private fun getAllUsers() {
        val userDao = CategoriesDataBase.getInstance((getApplication())).categoriesDao
        val list = userDao.getAllUserInfo()

        allUsers.postValue(list!!)
    }

    suspend fun getAllUsersForStepan(name: String, date:String): Client {
        val userDao = CategoriesDataBase.getInstance((getApplication())).categoriesDao
        return userDao.getClient(name, date)


    }

    @SuppressLint("SimpleDateFormat")
    fun insertUserInfo(entity: Client) {
        val userDao = CategoriesDataBase.getInstance(getApplication()).categoriesDao
        userDao.insertUser(entity)
        getAllUsers()
    }


    fun deleteUserInfo(entity: Client) {
        val userDao = CategoriesDataBase.getInstance(getApplication()).categoriesDao
        userDao.deleteStrokesEstimateByClient(entity._id)
        userDao.deleteUser(entity)
        getAllUsers()
    }
}