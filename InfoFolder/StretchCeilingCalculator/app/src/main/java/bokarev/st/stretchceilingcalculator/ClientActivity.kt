package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import bokarev.st.stretchceilingcalculator.entities.Client
import bokarev.st.stretchceilingcalculator.entities.Estimate
import kotlinx.android.synthetic.main.client.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class ClientActivity : AppCompatActivity(),TextView.OnEditorActionListener {

    private lateinit var viewModel: ClientsViewModel

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

        } catch (_: RuntimeException) {

        }

        val btnReturnToHome: LinearLayout = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            returnToPreviousActivity()
        }


        val addNewClient: RelativeLayout = findViewById(R.id.addNewClient)
        addNewClient.setOnClickListener {

            val name = editTextNameClientVal.text.toString()
            val address = editTextAddress.text.toString()
            val phone = editTextPhone.text.toString()


            val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date())
            Log.d("mytag", "currentDate = $currentDate")

            if (name == "" || address == "" || phone == "") {
                val toast = Toast.makeText(
                    applicationContext,
                    "Заполните имя, адрес и телефон Клиента",
                    Toast.LENGTH_LONG
                )
                toast.show()

            } else {


                if (tvState.text.equals("Продолжить")) {

                    val user = Client(
                        0, name, address, phone,
                        IsNew = true,
                        IsPurchase = false,
                        IsArchive = false,
                        DateOfCreation = currentDate,
                        DateOfEditing = currentDate
                    )

                    insertNewClientInDataBase(user)

                } else {
                    // Обновление данных клиента
                    //Надо добавить поиск существующего пользователя и чтение предыдущих значений с его полей
                    // и вместо булевых переменных записывать, то что было

                    val id = getClientFromPreviousActivity()._id
                    val user = Client(
                        id, name, address, phone, IsNew = true,
                        IsPurchase = false,
                        IsArchive = false,
                        DateOfCreation = getClientFromPreviousActivity().DateOfCreation,
                        DateOfEditing = currentDate
                    )

                    updateInfoClient(user)

                }
                editTextNameClientVal.setText("")
                editTextAddress.setText("")
                editTextPhone.setText("")
            }
        }

    }

    private fun returnToPreviousActivity() {
        val intent = Intent(this, Clients::class.java).also {
            it.putExtra("ClientEntity", setNullClient())
            it.putExtra("PreviousActivity", "ClientActivity")

        }
        startActivity(intent)
    }

    private fun updateInfoClient(client: Client) {
        val dao = CategoriesDataBase.getInstance(this@ClientActivity).categoriesDao
        dao.updateUser(client)

        tvState.text = "Продолжить"
        val intent = Intent(this, Clients::class.java).also {

            it.putExtra("PreviousActivity", "ClientActivity")
            it.putExtra("ClientEntity", client)
        }
        startActivity(intent)
    }

    private fun insertNewClientInDataBase(client: Client) {

        viewModel =
            ViewModelProvider(this@ClientActivity)[ClientsViewModel::class.java]

        viewModel.insertUserInfo(client) // вставка нового клиента

        createBlankCalculationToNewClient(client)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun createBlankCalculationToNewClient(client: Client) {
        var idUser = setNullClient()

        val job = GlobalScope.launch(Dispatchers.Default) {
            //кастыль чтобы найти Id только что вставленного клиента
            idUser = viewModel.getAllUsersForStepan(client.ClientName, client.DateOfCreation)


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
                        client.DateOfCreation,
                        client.DateOfCreation
                    )
                )
                index++
            }

            estimate.forEach { dao.insertEstimate(it) }

            Log.d("mytag", "new client id = ${idUser._id}")

        }

        runBlocking {
            // waiting for the coroutine to finish it"s work
            job.join()
            getTransition(idUser)
            Log.d("mytag", "Main Thread is Running")
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


    override fun onBackPressed() =
        returnToPreviousActivity()


    private fun setNullClient(): Client = Client(
        0, "", "", "", IsNew = false, IsPurchase = false, IsArchive = false,
        DateOfCreation = "",
        DateOfEditing = ""
    )


    private fun getClientFromPreviousActivity(): Client =
        intent.getSerializableExtra("ClientEntity") as Client

    override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {

        if (actionId == EditorInfo.IME_ACTION_GO) {
            // обрабатываем нажатие кнопки GO
            val toast = Toast.makeText(
                applicationContext,
                "Enter",
                Toast.LENGTH_LONG
            )
            toast.show()
            return true
        }
        return false
    }


}