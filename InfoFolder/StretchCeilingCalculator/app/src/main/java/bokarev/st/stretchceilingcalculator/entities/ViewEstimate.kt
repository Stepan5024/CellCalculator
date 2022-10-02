package bokarev.st.stretchceilingcalculator.entities

data class ViewEstimate(
    val _id: Int, // typeCategoryId
    val _idTypeOfWork: Int,
    var Price: Int,
    val CategoryName: String,
    val UnitsOfMeasurement: String,

)
