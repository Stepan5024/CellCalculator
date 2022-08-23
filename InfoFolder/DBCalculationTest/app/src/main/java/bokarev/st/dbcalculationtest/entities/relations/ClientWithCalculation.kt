package bokarev.st.dbcalculationtest.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import bokarev.st.dbcalculationtest.entities.Calculation
import bokarev.st.dbcalculationtest.entities.Client


data class ClientWithCalculation(
    @Embedded val client: Client,
    @Relation(
        parentColumn = "_id",
        entityColumn = "_idClient"
    )
    val calculation: List<Calculation>
)

