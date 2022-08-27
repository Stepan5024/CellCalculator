package bokarev.st.stretchceilingcalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import bokarev.st.stretchceilingcalculator.entities.Client
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ClientActivity : AppCompatActivity() {

    lateinit var viewModel: ClientsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client)
        try {
            val name = intent.getStringExtra("KEY1")
            // val school6 = intent.getSerializableExtra("School 6") as School
            //val age = intent.getIntExtra("KEY2", 0)
            //Log.d("mytag", "school 6 = ${school6.title}")
        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, Clients::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }
        val tvState: TextView = findViewById(R.id.tvState)

        val addNewClient: RelativeLayout = findViewById(R.id.addNewClient)
        addNewClient.setOnClickListener {

            val editTextNameClient: EditText = findViewById(R.id.editTextNameClient)
            val editTextAddress: EditText = findViewById(R.id.editTextAddress)
            val editTextPhone: EditText = findViewById(R.id.editTextPhone)


            val name = editTextNameClient.text.toString()
            val address = editTextAddress.text.toString()
            val phone = editTextPhone.text.toString()

            val sdf = SimpleDateFormat("dd.MM.yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            Log.d("mytag", "currentDate = ${currentDate}}")

            if (tvState.text.equals("Продолжить")) {


                val user = Client(0, name, address, phone,
                    IsNew = true,
                    IsPurchase = false,
                    IsArchive = false,
                    DateOfCreation = currentDate,
                    DateOfEditing = currentDate
                )
                //Without ViewModelFactory
                lifecycleScope.launch {
                    viewModel = ViewModelProvider(this@ClientActivity)[ClientsViewModel::class.java]
                    /*viewModel.getAllUsersObservers().observe(this@ClientActivity) {


                    }*/
                }
                viewModel.insertUserInfo(user)

                //переход на активность смета
                val intent = Intent(this, Clients::class.java).also {
                    it.putExtra("KEY1", "value1")
                    it.putExtra("KEY2", "value1")
                    it.putExtra("KEY3", "value1")
                    //it.putExtra("School 6", School("School 6", false))
                }
                startActivity(intent)
            } else {
                // Обновление данных пока заглушка!!!
                //Надо добавить поиск сущеествующего пользователя и чтение предыдущих значений с его полей
                // и вместо булевых пеерменных записывать то что было

                val user = Client(
                    editTextNameClient.getTag(editTextNameClient.id).toString().toInt(),
                    name,
                    address,
                    phone,
                    IsNew = true,
                    IsPurchase = false,
                    IsArchive = false,
                    DateOfCreation = currentDate,
                    DateOfEditing = currentDate
                )
                viewModel.updateUserInfo(user)
                tvState.setText("Продолжить")

                val intent = Intent(this, Clients::class.java).also {
                    it.putExtra("KEY1", "value1")
                    it.putExtra("KEY2", "value1")
                    it.putExtra("KEY3", "value1")
                    //it.putExtra("School 6", School("School 6", false))
                }
                startActivity(intent)
            }
            //nameInput.setText("")
            //emailInput.setText("")
        }

    }

    // Kotlin
    override fun onBackPressed() {
        val intent = Intent(this, Clients::class.java).also {
            it.putExtra("KEY1", "value1")
            it.putExtra("KEY2", "value1")
            it.putExtra("KEY3", "value1")
            //it.putExtra("School 6", School("School 6", false))
        }
        startActivity(intent)
    }

}