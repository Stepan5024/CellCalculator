package bokarev.st.stretchceilingcalculator

import androidx.room.*
import bokarev.st.stretchceilingcalculator.entities.Estimate
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.TypeCategory
import bokarev.st.stretchceilingcalculator.entities.TypeOfWork
import bokarev.st.stretchceilingcalculator.entities.relations.ClientWithEstimate
import bokarev.st.stretchceilingcalculator.entities.relations.TypeCategoryInEstimate
import bokarev.st.stretchceilingcalculator.entities.relations.TypeOfWorkWithTypeCategory

@Dao
interface TypeCategoryDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeCategory(typeCategory: TypeCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeOfWork(typeOfWork: TypeOfWork)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: Estimate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client)

    @Transaction
    @Query("SELECT * FROM TypeOfWork WHERE _id = :typeOfWorkId")
    suspend fun getTypeOfWorkWithTypeCategory(typeOfWorkId: Int): List<TypeOfWorkWithTypeCategory>

    @Transaction
    @Query("SELECT * FROM TypeCategory WHERE _id = :typeCategoryId")
    suspend fun getTypeCategoryInEstimate(typeCategoryId: Int): List<TypeCategoryInEstimate>

    @Transaction
    @Query("SELECT * FROM Client WHERE _id = :clientId")
    suspend fun getClientWithEstimate(clientId: Int): List<ClientWithEstimate>

    @Query("SELECT * FROM Client ORDER BY DateOfEditing DESC")
    fun getAllUserInfo(): List<Client>?


    @Insert
    fun insertUser(user: Client?)

    @Delete
    fun deleteUser(user: Client?)

    @Update
    fun updateUser(user: Client?)
}