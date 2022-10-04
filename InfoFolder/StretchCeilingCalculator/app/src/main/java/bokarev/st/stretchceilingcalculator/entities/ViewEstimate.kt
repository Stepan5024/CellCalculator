package bokarev.st.stretchceilingcalculator.entities

data class ViewEstimate(
    // надо отказаться от этого класса данных, потому что для него есть более полный класс ClientAndEstimateModification
    val _id: Int, // typeCategoryId
    val _idTypeOfWork: Int,
    var Price: Int,
    val CategoryName: String,
    val UnitsOfMeasurement: String,

)
