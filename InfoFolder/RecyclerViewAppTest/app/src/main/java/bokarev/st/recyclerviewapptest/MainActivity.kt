package bokarev.st.recyclerviewapptest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller


class MainActivity : AppCompatActivity(), SchoolAdapter.RowClickListener, RecyclerViewAdapter2.RowClickListener2 {

    var userList = arrayListOf<UserEntity2>(
        UserEntity2(0, "Test 1", "email 1", "phone 1"),
        UserEntity2(0, "Test 2", "email 1", "phone 1"),
        UserEntity2(0, "Test 3", "email 1", "phone 1"),
        UserEntity2(0, "Test 4", "email 1", "phone 1"),
        UserEntity2(0, "Test 5", "email 1", "phone 1"),
        UserEntity2(0, "Test 6", "email 1", "phone 1"),
        UserEntity2(0, "Test 7", "email 1", "phone 1"),
        UserEntity2(0, "Test 8", "email 1", "phone 1"),
        UserEntity2(0, "Test 9", "email 1", "phone 1"),


        )
    private val userAdapter = RecyclerViewAdapter2(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val name = intent.getStringExtra("KEY1")
            val school6 = intent.getSerializableExtra("School 6") as School
            //val age = intent.getIntExtra("KEY2", 0)
            Log.d("mytag", "school 6 = ${school6.title}")
        }catch (exp: RuntimeException) {
            // костыль пока не определились как можно распознавать с какой активности вернулся пользователь
        }

        var todoList = mutableListOf(
            Todo("Task 1", true),
            Todo("Task 2", false),
            Todo("Task 3", false),
            Todo("Task 4", false),
            Todo("Task 5", false),
            Todo("Task 6", false),
            Todo("Task 7", false),

            )

        var schoolList = mutableListOf(
            School("School 1", true),
            School("School 2", false),
            School("School 3", false),
            School("School 4", false),
            School("School 5", false),
            School("School 6", false),
            School("School 7", false),

            )


       // val addapterTodo = TodoAdapter(todoList)

        //val rvTodos: RecyclerView = findViewById(R.id.rvTodos)

       // rvTodos.adapter = addapterTodo
       // rvTodos.layoutManager = LinearLayoutManager(this)

        //val btnAdd: Button = findViewById(R.id.btnAddTodo)
        //val etTodo: EditText = findViewById(R.id.etTodo)

        //btnAdd.setOnClickListener {
           // val title = etTodo.text.toString()
          //  val todo = Todo(title, false)
          //  todoList.add(todo)
          //  addapterTodo.notifyItemInserted(todoList.size - 1)
       // }



        userAdapter.setListData(userList)
        val rvUsers: RecyclerView = findViewById(R.id.recyclerView)
        rvUsers.adapter = userAdapter
        rvUsers.layoutManager = LinearLayoutManager(this)

        //отвечает за медленную прокрутку
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        (rvUsers.layoutManager as LinearLayoutManager).startSmoothScroll(smoothScroller)

       // val addapterSchool = SchoolAdapter(schoolList, this@MainActivity)

        //val rvSchools: RecyclerView = findViewById(R.id.rvSchools)
        val saveButton = findViewById<Button>(R.id.saveButton)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)


        saveButton.setOnClickListener {
            val name  = nameInput.text.toString()
            val email  = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            if(saveButton.text.equals("Save")) {
                val user = UserEntity2(0, name, email, phone)
                /*userList.add(user)
                //viewModel.insertUserInfo(user)
                userAdapter.notifyItemInserted(userList.size - 1)*/

                //отвечает за медленную прокрутку
                userList.add(0, user);
                userAdapter.notifyItemInserted(0)
                rvUsers.smoothScrollToPosition(0);

            } else {
                 //= UserEntity2(0, name, email, phone)
                //userList.remove(user)
                var user = UserEntity2(nameInput.getTag(nameInput.id).toString().toInt(), name, email, phone)
                //viewModel.updateUserInfo(user)
                userList.add(user)
                userAdapter.notifyItemInserted(userList.size - 1)
                saveButton.setText("Save")

            }
            nameInput.setText("")
            emailInput.setText("")
        }

       /* rvSchools.adapter = addapterSchool
        rvSchools.layoutManager = LinearLayoutManager(this)

        val btnAddSchool: Button = findViewById(R.id.btnAddSchool)
        val etSchool: EditText = findViewById(R.id.etSchool)

        btnAddSchool.setOnClickListener {
            val title = etSchool.text.toString()
            val school = School(title, false)
            schoolList.add(school)
            addapterSchool.notifyItemInserted(schoolList.size - 1)
        }
*/
    }


    override fun onDeleteUserClickListener(school: School) {
        //viewModel.deleteUserInfo(user)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onItemClickListener(school: School) {
       /* Toast.makeText(applicationContext, "${school.title}", Toast.LENGTH_SHORT).show()

        val edNameSchool: TextView = findViewById(R.id.etSchool)
        edNameSchool.setText(school.title)
        val swBox: Switch = findViewById(R.id.swSchool)
        swBox.isChecked = school.isChecked*/
    }

    override fun onDeleteUserClickListener2(user: UserEntity2) {
       // viewModel.deleteUserInfo(user)
    }

    override fun onItemClickListener2(user: UserEntity2) {
        val toast = Toast.makeText(applicationContext, "${user.name} + ${userList.indexOf(user)}", Toast.LENGTH_SHORT)
        toast.show()
        val num = userList.indexOf(user)
        userAdapter.removeItem(num)
        //userAdapter.notifyItemRemoved(userList.indexOf(user))
        //userList.removeAt(num)

       /* val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val saveButton = findViewById<Button>(R.id.saveButton)

        nameInput.setText(user.name)
        emailInput.setText(user.email)
        phoneInput.setText(user.phone)
        nameInput.setTag(nameInput.id, user.id)
        saveButton.setText("Update")*/
        
        /*val intent = Intent(this, NewActivity::class.java).also {
            it.putExtra("KEY1", "value1")
            it.putExtra("KEY2", "value1")
            it.putExtra("KEY3", "value1")
            it.putExtra("School 6", School("School 6", false))
        }
        startActivity(intent)*/

    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart() called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("MainActivity", "onRestart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity", "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "onDestroy() called")
    }

}