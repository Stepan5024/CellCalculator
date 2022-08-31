package bokarev.st.myapplication


import java.io.Serializable


data class Receipt(
    var receiptId: Long,
    var createdDate: String,
    var priceP1: Double,
    var quantityP1: Int,
    var priceP2: Double,
    var quantityP2: Int,
    var priceP3: Double,
    var quantityP3: Int,
    var filePath: String,
) : Serializable {

    constructor(
        createdDate: String,
        priceP1: Double,
        quantityP1: Int,
        priceP2: Double,
        quantityP2: Int,
        priceP3: Double,
        quantityP3: Int,
        filePath: String
    ) : this(
        0,
        createdDate,
        priceP1,
        quantityP1,
        priceP2,
        quantityP2,
        priceP3,
        quantityP3,
        filePath
    )

}
