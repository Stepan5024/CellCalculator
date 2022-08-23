package bokarev.st.dbcalculationtest.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.dbcalculationtest.entities.TypeCategory
import bokarev.st.dbcalculationtest.entities.TypeOfWork

data class TypeOfWorkWithTypeCategory (
    @Embedded val typeOfWork: TypeOfWork,
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idTypeOfWork"
    )
    val typesCategories: List<TypeCategory>
)