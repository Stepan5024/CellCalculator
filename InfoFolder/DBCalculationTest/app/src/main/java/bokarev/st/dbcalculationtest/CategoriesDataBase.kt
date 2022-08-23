package bokarev.st.dbcalculationtest

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bokarev.st.dbcalculationtest.entities.TypeCategory
import bokarev.st.dbcalculationtest.entities.TypeOfWork
import javax.security.auth.Subject

@Database(
    entities = [
        TypeOfWork::class,
        TypeCategory::class,

    ],
    version = 1
)
abstract class CategoriesDataBase : RoomDatabase() {

    abstract val categoriesDao: TypeCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: CategoriesDataBase? = null

        fun getInstance(context: Context): CategoriesDataBase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CategoriesDataBase::class.java,
                    "Calculator.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}