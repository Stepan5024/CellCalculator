package bokarev.st.stretchceilingcalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import bokarev.st.stretchceilingcalculator.entities.Client

class ClientsViewModel(app: Application): AndroidViewModel(app) {
    var allUsers : MutableLiveData<List<Client>> = MutableLiveData()

    init{
        getAllUsers()
    }

    fun getAllUsersObservers(): MutableLiveData<List<Client>> {
        return allUsers
    }

    private fun getAllUsers() {
        val userDao = CategoriesDataBase.getInstance((getApplication())).categoriesDao
        val list = userDao.getAllUserInfo()

        allUsers.postValue(list)
    }

    fun insertUserInfo(entity: Client){
        val userDao = CategoriesDataBase.getInstance(getApplication()).categoriesDao
        userDao.insertUser(entity)
        getAllUsers()
    }

    fun updateUserInfo(entity: Client){
        val userDao = CategoriesDataBase.getInstance(getApplication()).categoriesDao
        userDao?.updateUser(entity)
        getAllUsers()
    }

    fun deleteUserInfo(entity: Client){
        val userDao = CategoriesDataBase.getInstance(getApplication()).categoriesDao
        userDao?.deleteUser(entity)
        getAllUsers()
    }
}