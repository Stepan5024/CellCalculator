package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.lifecycle.lifecycleScope
import bokarev.st.stretchceilingcalculator.entities.Client
import kotlinx.coroutines.launch

class Clients : AppCompatActivity(), ClientsRecyclerViewAdapter.RowClickListener {

    private lateinit var clientsRecyclerViewAdapter: ClientsRecyclerViewAdapter
    private lateinit var viewModel: ClientsViewModel
    private var flagOfEditing: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clients)
        try {
            val client = getClientFromPreviousActivity()
            val previousActivity = intent.getStringExtra("PreviousActivity")
            Log.d("mytag", "previousActivity = $previousActivity nameOfClient = ${client.ClientName}")

        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).also {
                it.putExtra("ClientEntity", setNullClient())
                it.putExtra("PreviousActivity", "ClientsActivity")

            }
            startActivity(intent)
        }

        val btnCorrectListOfClients: ImageView = findViewById(R.id.btnCorrectListOfClients)
        btnCorrectListOfClients.setOnClickListener {
            val toast = Toast.makeText(
                applicationContext,
                "Теперь нажмите на любую запись для редактирования",
                Toast.LENGTH_LONG
            )
            toast.show()
            flagOfEditing = !flagOfEditing
        }
        val addNewClient: RelativeLayout = findViewById(R.id.addNewClient)
        addNewClient.setOnClickListener {

            /*val toast = Toast.makeText(applicationContext, "Relative Layout pressed", Toast.LENGTH_SHORT)
            toast.show()*/

            val intent = Intent(this, ClientActivity::class.java).also {
                it.putExtra("ClientEntity", setNullClient())
                it.putExtra("PreviousActivity", "Clients")

            }
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.ClientsRecyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Clients)
            clientsRecyclerViewAdapter = ClientsRecyclerViewAdapter(this@Clients)
            adapter = clientsRecyclerViewAdapter
            val divider =
                DividerItemDecoration(applicationContext, StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(divider)
        }


        //Without ViewModelFactory
        lifecycleScope.launch {
            viewModel = ViewModelProvider(this@Clients)[ClientsViewModel::class.java]
            viewModel.getAllUsersObservers().observe(this@Clients) {

                clientsRecyclerViewAdapter.setListData(ArrayList(it))
                clientsRecyclerViewAdapter.notifyDataSetChanged()

            }
        }


    }

    // Kotlin
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).also {
            it.putExtra("ClientEntity", setNullClient())
            it.putExtra("PreviousActivity", "Clients")
        }
        startActivity(intent)
    }

    override fun onDeleteUserClickListener(user: Client) {
        viewModel.deleteUserInfo(user)
    }

    override fun onItemClickListener(user: Client) {
        //обработка нажатия в recyclerView


        if (flagOfEditing) {
            // открываем активность Клиента и заполняем поля
            val intent = Intent(this, ClientActivity::class.java).also {

                it.putExtra("PreviousActivity", "Clients")
                it.putExtra("ClientEntity", user)

            }
            startActivity(intent)
        } else {
            // открываем смету выбранного клиента и заполненные данные клиента

            val toast = Toast.makeText(applicationContext, "имя клиента ${user.ClientName}", Toast.LENGTH_SHORT)
            toast.show()

            val intent = Intent(this, Calculation::class.java).also {

                it.putExtra("PreviousActivity", "Clients")
                it.putExtra("ClientEntity", user)
            }
            startActivity(intent)
        }

        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }
    private fun setNullClient(): Client {

        return Client(
            0, "", "", "", IsNew = false, IsPurchase = false, IsArchive = false,
            DateOfCreation = "",
            DateOfEditing = ""
        )

    }

    private fun getClientFromPreviousActivity(): Client {

        return intent.getSerializableExtra("ClientEntity") as Client
    }
}