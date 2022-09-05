package bokarev.st.stretchceilingcalculator.models


class TypeOfWorkForRecyclerView(private var typeOfWorkName: String, private var price: Int, private var count: Int) {

    fun getTypeOfWorkName(): String {
        return typeOfWorkName
    }

    fun setTypeOfWorkName(typeOfWorkName: String) {
        this.typeOfWorkName = typeOfWorkName
    }
    fun getTypeOfWorkPrice(): Int {
        return price
    }

    fun setTypeOfWorkPrice(price: Int) {
        this.price = price
    }

    fun getTypeOfWorkCount(): Int {
        return count
    }

    fun setTypeOfWorkCount(count: Int) {
        this.count = count
    }
}
