package bokarev.st.dbcalculationtest

import androidx.room.*
import bokarev.st.dbcalculationtest.entities.TypeCategory
import bokarev.st.dbcalculationtest.entities.TypeOfWork
import bokarev.st.dbcalculationtest.entities.relations.TypeOfWorkWithTypeCategory

@Dao
interface TypeCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeCategory(typeCategory: TypeCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeOfWork(typeOfWork: TypeOfWork)

    @Transaction
    @Query("SELECT * FROM typeofwork WHERE _id = :typeOfWorkId")
    suspend fun getTypeOfWorkWithTypeCategory(typeOfWorkId: Int): List<TypeOfWorkWithTypeCategory>
}