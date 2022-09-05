package bokarev.st.stretchceilingcalculator


import bokarev.st.stretchceilingcalculator.entities.relations.ClientAndEstimate
import bokarev.st.stretchceilingcalculator.models.ClientAndEstimateMidifation
import java.io.Serializable


data class Receipt(
    var receiptId: Long,
    var dataList: MutableList<ClientAndEstimate>,
    val address: String,
    val tel: String,
    var FilePath: String,

) : Serializable {

    constructor(
        dataList: MutableList<ClientAndEstimate>,
        address: String,
        tel: String,
        filePath: String,
    ) : this(0, dataList, address, tel, filePath)

}
