package bokarev.st.stretchceilingcalculator.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.stretchceilingcalculator.entities.Estimate
import bokarev.st.stretchceilingcalculator.entities.TypeCategory


data class TypeCategoryInEstimate(
    @Embedded val typeCategory: TypeCategory,
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idTypeCategory"
    )
    val estimate: List<Estimate>
)
