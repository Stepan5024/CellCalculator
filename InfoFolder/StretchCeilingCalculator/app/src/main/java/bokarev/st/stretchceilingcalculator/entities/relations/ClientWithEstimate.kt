package bokarev.st.stretchceilingcalculator.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.stretchceilingcalculator.entities.Estimate
import bokarev.st.stretchceilingcalculator.entities.Client


data class ClientWithEstimate(
    @Embedded val client: Client,
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idClient"
    )
    val estimate: List<Estimate>
)

