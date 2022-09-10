package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.Estimate
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ClientActivity : AppCompatActivity() {

    private lateinit var viewModel: ClientsViewModel


    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client)


        val editTextNameClientVal: EditText = findViewById(R.id.editTextNameClient)
        val editTextAddress: EditText = findViewById(R.id.editTextAddress)
        val editTextPhone: EditText = findViewById(R.id.editTextPhone)
        val tvState: TextView = findViewById(R.id.tvState)

        try {

            val previousActivity = intent.getStringExtra("PreviousActivity")
            val client = getClientFromPreviousActivity()

            editTextNameClientVal.setText(client.ClientName)
            editTextAddress.setText(client.Address)
            editTextPhone.setText(client.Tel)

            if (client.ClientName == "") tvState.text = "Продолжить"
            else tvState.text = "Сохранить"

            Log.d(
                "mytag",
                "previousActivity = $previousActivity nameOfClient = ${client.ClientName}"
            )


        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, Clients::class.java).also {
                it.putExtra("ClientEntity", setNullClient())
                it.putExtra("PreviousActivity", "ClientActivity")

            }
            startActivity(intent)
        }


        val addNewClient: RelativeLayout = findViewById(R.id.addNewClient)
        addNewClient.setOnClickListener {


            val name = editTextNameClientVal.text.toString()
            val address = editTextAddress.text.toString()
            val phone = editTextPhone.text.toString()

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
            val currentDate = sdf.format(Date())
            Log.d("mytag", "currentDate = $currentDate")

            var idUser = setNullClient()

            if (tvState.text.equals("Продолжить")) {


                val user = Client(
                    0, name, address, phone,
                    IsNew = true,
                    IsPurchase = false,
                    IsArchive = false,
                    DateOfCreation = currentDate,
                    DateOfEditing = currentDate
                )

                // костыль
                //Without ViewModelFactory
                lifecycleScope.launch {
                    viewModel =
                        ViewModelProvider(this@ClientActivity)[ClientsViewModel::class.java]
                }

                viewModel.insertUserInfo(user) // вставка нового клиента


                val job = GlobalScope.launch(Dispatchers.Default) {
                    //кастыль чтобы найти Id только что вставленного клиента
                    idUser = viewModel.getAllUsersForStepan(name, currentDate)


                    val dao = CategoriesDataBase.getInstance(this@ClientActivity).categoriesDao
                    val typesCategory = dao.getTypeCategory()
                    val estimate: MutableList<Estimate> = arrayListOf()


                    var index = 1
                    for (element in typesCategory) {
                        Log.d("mytag", "element in typesCategory = ${element._id}")
                        estimate.add(
                            Estimate(
                                0,
                                idUser._id,
                                element._id,
                                0.0,
                                currentDate,
                                currentDate
                            )
                        )
                        index++
                    }

                    // здесь добавить чтение БД таблицы CategoryName и отталкиваясь от кол-ва делать цикл,
                    // куда записывать все нулевые значения


                    estimate.forEach { dao.insertEstimate(it) }

                    Log.d("mytag", "new client id = ${idUser._id}")

                }

                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    getTransition(idUser)
                    Log.d("mytag", "Main Thread is Running")
                }

            } else {
                // Обновление данных пока заглушка!!!
                //Надо добавить поиск сущеествующего пользователя и чтение предыдущих значений с его полей
                // и вместо булевых пеерменных записывать то что было
                val id = getClientFromPreviousActivity()._id
                val user = Client(
                    id, name, address, phone, IsNew = true,
                    IsPurchase = false,
                    IsArchive = false,
                    DateOfCreation = currentDate,
                    DateOfEditing = currentDate
                )

                val dao = CategoriesDataBase.getInstance(this@ClientActivity).categoriesDao
                dao.updateUser(user)

                tvState.text = "Продолжить"
                val intent = Intent(this, Clients::class.java).also {

                    it.putExtra("PreviousActivity", "ClientActivity")
                    it.putExtra("ClientEntity", user)
                }
                startActivity(intent)
            }
            editTextNameClientVal.setText("")
            editTextAddress.setText("")
            editTextPhone.setText("")
        }

    }

    private fun getTransition(client: Client) {
        // Сейчас надо нажать второй раз чтобы перейти на активность Сметы
        // переход на активность смета
        val intent = Intent(this, Calculation::class.java).also {

            it.putExtra("PreviousActivity", "ClientActivity")
            it.putExtra("ClientEntity", client)
        }
        startActivity(intent)
    }

    // Kotlin
    override fun onBackPressed() {
        val intent = Intent(this, Clients::class.java).also {
            it.putExtra("ClientEntity", setNullClient())
            it.putExtra("PreviousActivity", "ClientActivity")
        }
        startActivity(intent)
    }

    private fun setNullClient(): Client = Client(
        0, "", "", "", IsNew = false, IsPurchase = false, IsArchive = false,
        DateOfCreation = "",
        DateOfEditing = ""
    )


    private fun getClientFromPreviousActivity(): Client =
        intent.getSerializableExtra("ClientEntity") as Client


}