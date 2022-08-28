package bokarev.st.stretchceilingcalculator.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Client(

    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val ClientName: String,
    val Address: String,
    val Tel: String,
    val IsNew: Boolean,
    val IsPurchase: Boolean,
    val IsArchive: Boolean,
    val DateOfCreation: String,
    val DateOfEditing: String,
    ) : Serializable
