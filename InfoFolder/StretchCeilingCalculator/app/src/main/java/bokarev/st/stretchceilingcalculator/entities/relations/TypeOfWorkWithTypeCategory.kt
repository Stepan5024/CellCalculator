package bokarev.st.stretchceilingcalculator.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.stretchceilingcalculator.entities.TypeCategory
import bokarev.st.stretchceilingcalculator.entities.TypeOfWork

data class TypeOfWorkWithTypeCategory (
    @Embedded val typeOfWork: TypeOfWork,
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idTypeOfWork"
    )
    val typesCategories: List<TypeCategory>
)