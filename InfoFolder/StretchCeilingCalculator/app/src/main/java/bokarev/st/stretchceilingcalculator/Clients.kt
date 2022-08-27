package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

    lateinit var clientsRecyclerViewAdapter: ClientsRecyclerViewAdapter
    lateinit var viewModel: ClientsViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clients)
        try {
            val name = intent.getStringExtra("KEY1")
            //val school6 = intent.getSerializableExtra("School 6") as School
            //val age = intent.getIntExtra("KEY2", 0)
            //Log.d("mytag", "school 6 = ${school6.title}")
        } catch (exp: RuntimeException) {

        }

        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }

        val btnCorrectListOfClients: ImageView= findViewById(R.id.btnCorrectListOfClients)
        btnCorrectListOfClients.setOnClickListener{
            val toast = Toast.makeText(applicationContext, "Correct btn pressed", Toast.LENGTH_SHORT)
            toast.show()

        }
        val addNewClient: RelativeLayout = findViewById(R.id.addNewClient)
        addNewClient.setOnClickListener{

            /*val toast = Toast.makeText(applicationContext, "Relative Layout pressed", Toast.LENGTH_SHORT)
            toast.show()*/

            val intent = Intent(this, ClientActivity::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.ClientsRecyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Clients)
            clientsRecyclerViewAdapter = ClientsRecyclerViewAdapter(this@Clients)
            adapter = clientsRecyclerViewAdapter
            val divider = DividerItemDecoration(applicationContext, StaggeredGridLayoutManager.VERTICAL)
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
            it.putExtra("KEY1", "value1")
            it.putExtra("KEY2", "value1")
            it.putExtra("KEY3", "value1")
            //it.putExtra("School 6", School("School 6", false))
        }
        startActivity(intent)
    }
    override fun onDeleteUserClickListener(user: Client) {
        viewModel.deleteUserInfo(user)
    }

    override fun onItemClickListener(user: Client) {


        /*nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/

    }
}