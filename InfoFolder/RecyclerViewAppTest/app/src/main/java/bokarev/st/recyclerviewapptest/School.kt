package bokarev.st.recyclerviewapptest

import java.io.Serializable

data class School(
    val title: String,
    var isChecked: Boolean
) : Serializable