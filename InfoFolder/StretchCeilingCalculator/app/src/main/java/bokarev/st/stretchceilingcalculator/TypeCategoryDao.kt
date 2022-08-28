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
    @Query("SELECT * FROM Client ORDER BY DateOfEditing DESC")
    fun getAllUserInfo(): List<Client>?

    @Transaction
    @Query("SELECT  Client.ClientName, Estimate.Count, Estimate._idTypeCategory, TypeCategory._idTypeOfWork, TypeCategory.Price, TypeCategory.CategoryName FROM Client, Estimate, TypeCategory where Estimate._idClient = :clientId")
    suspend fun getClientAndEstimate(clientId:Int): List<ClientAndEstimate>

  /*  @Transaction
    @Query("SELECT Client.ClientName, Estimate.Count, Estimate._idTypeCategory, TypeCategory._idTypeOfWork, TypeCategory.Price, TypeCategory.CategoryName, TypeOfWork.TypeOfWorkName FROM Estimate INNER JOIN Client ON Estimate._idClient = Client._id INNER JOIN  TypeCategory ON Estimate._idTypeCategory =  TypeCategory._id INNER JOIN TypeOfWork ON TypeCategory._idTypeOfWork = TypeOfWork._id where Estimate._idClient = :clientId")
    suspend fun getUnionClientAndEstimateAndTypeCategory(clientId:Int): List<ClientAndEstimate>
*/
    @Transaction
    @Query("SELECT Client.ClientName, Estimate.Count, Estimate._idTypeCategory, TypeCategory._idTypeOfWork, TypeCategory.Price, TypeCategory.CategoryName FROM Estimate INNER JOIN Client ON Estimate._idClient = Client._id INNER JOIN  TypeCategory ON Estimate._idTypeCategory =  TypeCategory._id where Estimate._idClient = :clientId  AND TypeCategory._idTypeOfWork = :typeCategoryId")
    fun getUnionClientAndEstimateAndTypeCategory2(clientId:Int, typeCategoryId: Int): List<ClientAndEstimate>


    @Insert
    fun insertUser(user: Client?)

    @Delete
    fun deleteUser(user: Client?)

    @Update
    fun updateUser(user: Client?)



}