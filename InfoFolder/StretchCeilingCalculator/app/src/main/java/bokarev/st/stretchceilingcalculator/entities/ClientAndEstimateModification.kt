package bokarev.st.stretchceilingcalculator.entities



data class ClientAndEstimateModification(

    val ClientName: String,
    var Count: Float,
    val _idTypeCategory: Int,
    val _idTypeOfWork: Int,
    val Price: Int,
    val CategoryName: String,
    val NameTypeOfWork: String,
    val TypeLayout: Int,
    val UnitsOfMeasurement: String,


)