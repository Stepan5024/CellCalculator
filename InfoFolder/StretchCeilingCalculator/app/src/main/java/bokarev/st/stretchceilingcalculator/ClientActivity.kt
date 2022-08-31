package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.Estimate
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*

class ClientActivity : AppCompatActivity() {

    private lateinit var viewModel: ClientsViewModel


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client)


        val editTextNameClient: EditText = findViewById(R.id.editTextNameClient)
        val editTextAddress: EditText = findViewById(R.id.editTextAddress)
        val editTextPhone: EditText = findViewById(R.id.editTextPhone)
        val tvState: TextView = findViewById(R.id.tvState)

        try {

            val previousActivity = intent.getStringExtra("PreviousActivity")
            val client = getClientFromPreviousActivity()

            editTextNameClient.setText(client.ClientName)
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


            val name = editTextNameClient.text.toString()
            val address = editTextAddress.text.toString()
            val phone = editTextPhone.text.toString()

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
            val currentDate = sdf.format(Date())
            Log.d("mytag", "currentDate = ${currentDate}")
            var flagGo: Boolean = false
            var idUser = setNullClient()

            if (tvState.text.equals("Продолжить")) {


                Log.d("mytag", "flagGo = ${flagGo}")
                val user = Client(
                    0, name, address, phone,
                    IsNew = true,
                    IsPurchase = false,
                    IsArchive = false,
                    DateOfCreation = currentDate,
                    DateOfEditing = currentDate
                )
                flagGo = true
                Log.d("mytag", "flagGo = ${flagGo}}")
                // костыль
                //Without ViewModelFactory
                lifecycleScope.launch {
                    viewModel =
                        ViewModelProvider(this@ClientActivity)[ClientsViewModel::class.java]
                }
                val dao = CategoriesDataBase.getInstance(this).categoriesDao
                viewModel.insertUserInfo(user) // вставка нового клиента


                var num = 0
                val job = GlobalScope.launch(Dispatchers.Default) {
                    //кастыль чтобы найти Id только что вставленного клиента
                    idUser = viewModel.getAllUsersForStepan(name, currentDate)
                    num = idUser._id

                    val estimate = listOf(

                        // здесь добавить чтение БД таблицы ategoryName и отталкиваясь от кол-ва делать цикл,
                        // куда записывать все нулевые значения
                        Estimate(0, num, 1, 0, currentDate, currentDate),
                        Estimate(0, idUser._id, 2, 0, currentDate, currentDate),
                        Estimate(0, idUser._id, 3, 0, currentDate, currentDate),
                        Estimate(0, idUser._id, 4, 0, currentDate, currentDate),
                        Estimate(0, idUser._id, 5, 0, currentDate, currentDate),
                        Estimate(0, idUser._id, 6, 0, currentDate, currentDate),

                        )

                    estimate.forEach { dao.insertEstimate(it) }


                    Log.d("mytag", "new client id = ${idUser._id}")

                   /* repeat(5)
                    {
                        Log.d("mytag", "Coroutines is still working")
                        // delay the coroutine by 1sec
                        delay(1000)
                    }*/
                    // update views
                    // добавить прогресс бар
                    // https://material.io/components/progress-indicators/android#using-progress-indicators

                }

             /*  val job = lifecycleScope.launch {
                    flagGo = true


                }
                flagGo = true*/


                runBlocking {
                    // waiting for the coroutine to finish it"s work
                    job.join()
                    gettransition(idUser)
                    Log.d("mytag", "Main Thread is Running")
                }


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
                tvState.text = "Продолжить"
                val toast = Toast.makeText(
                    applicationContext,
                    "Кнопка перезаписать не работает",
                    Toast.LENGTH_SHORT
                )
                toast.show()
                /*val intent = Intent(this, Clients::class.java).also {

                    it.putExtra("PreviousActivity", "ClientActivity")
                    it.putExtra("ClientEntity", user)
                }
                startActivity(intent)*/
            }
            //nameInput.setText("")
            //emailInput.setText("")
        }

    }
    fun gettransition(client: Client){
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