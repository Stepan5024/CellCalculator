package bokarev.st.dbcalculationtest

import androidx.room.*
import bokarev.st.dbcalculationtest.entities.Calculation
import bokarev.st.dbcalculationtest.entities.Client
import bokarev.st.dbcalculationtest.entities.TypeCategory
import bokarev.st.dbcalculationtest.entities.TypeOfWork
import bokarev.st.dbcalculationtest.entities.relations.ClientWithCalculation
import bokarev.st.dbcalculationtest.entities.relations.TypeCategoryInCalculation
import bokarev.st.dbcalculationtest.entities.relations.TypeOfWorkWithTypeCategory

@Dao
interface TypeCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeCategory(typeCategory: TypeCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeOfWork(typeOfWork: TypeOfWork)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: Calculation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client)

    @Transaction
    @Query("SELECT * FROM TypeOfWork WHERE _id = :typeOfWorkId")
    suspend fun getTypeOfWorkWithTypeCategory(typeOfWorkId: Int): List<TypeOfWorkWithTypeCategory>

    @Transaction
    @Query("SELECT * FROM TypeCategory WHERE _id = :typeCategoryId")
    suspend fun getTypeCategoryInCalculation(typeCategoryId: Int): List<TypeCategoryInCalculation>

    @Transaction
    @Query("SELECT * FROM Client WHERE _id = :clientId")
    suspend fun getClientWithCalculation(clientId: Int): List<ClientWithCalculation>
}