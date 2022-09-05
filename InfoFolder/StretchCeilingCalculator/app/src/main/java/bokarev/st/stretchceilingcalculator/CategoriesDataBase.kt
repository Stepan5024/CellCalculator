package bokarev.st.stretchceilingcalculator

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bokarev.st.stretchceilingcalculator.entities.Estimate
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.TypeCategory
import bokarev.st.stretchceilingcalculator.entities.TypeOfWork

@Database(
    entities = [
        TypeOfWork::class,
        TypeCategory::class,
        Estimate::class,
        Client::class,
    ],
    version = 5
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
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build().also {
                    INSTANCE = it
                }
            }
        }
    }
}