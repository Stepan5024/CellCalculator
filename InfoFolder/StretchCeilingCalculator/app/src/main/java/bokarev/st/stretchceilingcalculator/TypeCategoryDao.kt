package bokarev.st.stretchceilingcalculator

import androidx.room.*
import bokarev.st.stretchceilingcalculator.entities.*
import bokarev.st.stretchceilingcalculator.entities.relations.*

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

    @Transaction
    @Query("SELECT * FROM Client WHERE ClientName = :clientName AND DateOfCreation = :dateOfCreation")
    suspend fun getClient(clientName: String, dateOfCreation: String): Client

    @Transaction
    @Query("SELECT * FROM Client ORDER BY DateOfEditing DESC")
    fun getAllUserInfo(): List<Client>?

    @Transaction
    @Query("SELECT  Client.ClientName, Estimate.Count, Estimate._idTypeCategory, TypeCategory._idTypeOfWork, TypeCategory.Price, TypeCategory.CategoryName FROM Client, Estimate, TypeCategory where Estimate._idClient = :clientId")
    suspend fun getClientAndEstimate(clientId: Int): List<ClientAndEstimate>


    @Transaction
    @Query("SELECT Client.ClientName, Estimate.Count, Estimate._idTypeCategory, TypeCategory._idTypeOfWork, TypeCategory.Price, TypeCategory.CategoryName FROM Estimate INNER JOIN Client ON Estimate._idClient = Client._id INNER JOIN  TypeCategory ON Estimate._idTypeCategory =  TypeCategory._id where Estimate._idClient = :clientId  AND TypeCategory._idTypeOfWork = :typeCategoryId")
    fun getUnionClientAndEstimateAndTypeCategory2(
        clientId: Int,
        typeCategoryId: Int
    ): List<ClientAndEstimate>


    @Insert
    fun insertUser(user: Client?)

    @Delete
    fun deleteUser(user: Client?)

    @Transaction
    @Query("SELECT Client.ClientName, Estimate.Count, Estimate._idTypeCategory, TypeCategory._idTypeOfWork, TypeCategory.Price, TypeCategory.CategoryName FROM Estimate INNER JOIN Client ON Estimate._idClient = Client._id INNER JOIN  TypeCategory ON Estimate._idTypeCategory =  TypeCategory._id where Estimate._idClient = :clientId")
    fun selectStrokesEstimateByClient(clientId: Int): List<ClientAndEstimate>

    @Transaction
    @Query("DELETE FROM Estimate where _idClient = :clientId")
    fun deleteStrokesEstimateByClient(clientId: Int)

    @Transaction
    @Query("UPDATE Estimate SET Count = :newCount  where _idClient = :clientId AND _idTypeCategory = :idTypeCategory")
    suspend fun updateCountStrokesEstimateByClient(
        clientId: Int,
        idTypeCategory: Int,
        newCount: Int
    )

    @Update
    fun updateUser(user: Client?)


    @Delete()
    fun deleteEstimate(estimate: Estimate)

    @Transaction
    @Query("DELETE FROM Estimate where _id = :Id")
    fun deleteEstimateById(Id: Int)

    @Transaction
    @Query("DELETE FROM Estimate where _idClient = :Id")
    fun deleteEstimateByClientId(Id: Int)

    @Transaction
    @Query("DELETE FROM Client where _id = :Id")
    fun deleteClientById(Id: Int)
}