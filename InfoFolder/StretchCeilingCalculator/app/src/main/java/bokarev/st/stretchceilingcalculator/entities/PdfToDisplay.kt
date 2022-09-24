package bokarev.st.stretchceilingcalculator.entities


import java.io.Serializable


data class PdfToDisplay(
    var receiptId: Long,
    var dataList: MutableList<ClientAndEstimateModification>,
    val address: String,
    val tel: String,
    var FilePath: String,

    ) : Serializable
