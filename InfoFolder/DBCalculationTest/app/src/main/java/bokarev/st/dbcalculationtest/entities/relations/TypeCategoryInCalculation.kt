package bokarev.st.dbcalculationtest.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.dbcalculationtest.entities.Calculation
import bokarev.st.dbcalculationtest.entities.TypeCategory


data class TypeCategoryInCalculation(
    @Embedded val typeCategory: TypeCategory,
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idTypeCategory"
    )
    val calculation: List<Calculation>
)
