package bokarev.st.recyclerviewapptest.db

import androidx.room.*
import bokarev.st.recyclerviewapptest.UserEntity2

@Dao
interface UserDao {


    @Query("SELECT * FROM userinfo ORDER BY id DESC")
    fun getAllUserInfo(): List<UserEntity2>?


    @Insert
    fun insertUser(user: UserEntity2?)

    @Delete
    fun deleteUser(user: UserEntity2?)

    @Update
    fun updateUser(user: UserEntity2?)

}