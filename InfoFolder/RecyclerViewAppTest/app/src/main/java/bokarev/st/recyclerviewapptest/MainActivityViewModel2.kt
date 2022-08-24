package bokarev.st.recyclerviewapptest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import bokarev.st.recyclerviewapptest.db.RoomAppDb



class MainActivityViewModel2(app: Application): AndroidViewModel(app) {
    var allUsers : MutableLiveData<List<UserEntity2>> = MutableLiveData()

    /*init{
        getAllUsers()
    }*/

    fun getAllUsersObservers(): MutableLiveData<List<UserEntity2>> {
        return allUsers
    }

    /*fun getAllUsers() {
        val userDao = RoomAppDb.getAppDatabase((getApplication()))?.userDao()
        val list = userDao?.getAllUserInfo()

        allUsers.postValue(list)
    }

    fun insertUserInfo(entity: UserEntity2){
        val userDao = RoomAppDb.getAppDatabase(getApplication())?.userDao()
        userDao?.insertUser(entity)
        getAllUsers()
    }

    fun updateUserInfo(entity: UserEntity2){
        val userDao = RoomAppDb.getAppDatabase(getApplication())?.userDao()
        userDao?.updateUser(entity)
        getAllUsers()
    }

    fun deleteUserInfo(entity: UserEntity2){
        val userDao = RoomAppDb.getAppDatabase(getApplication())?.userDao()
        userDao?.deleteUser(entity)
        getAllUsers()
    }*/
}