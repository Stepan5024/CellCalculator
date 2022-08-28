package bokarev.st.stretchceilingcalculator.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.stretchceilingcalculator.entities.TypeOfWork

data class TypeOfWorkWithUnion(
    //что объединяем
    @Embedded val typeOfWork: TypeOfWork,

    // с чем объединяем
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idTypeOfWork"
    )
    val сlientAndEstimate: List<ClientAndEstimate>
)
