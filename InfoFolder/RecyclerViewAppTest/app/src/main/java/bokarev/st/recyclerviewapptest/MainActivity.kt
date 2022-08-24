package bokarev.st.recyclerviewapptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val addapterTodo = TodoAdapter(todoList)

        val rvTodos: RecyclerView = findViewById(R.id.rvTodos)

        rvTodos.adapter = addapterTodo
        rvTodos.layoutManager = LinearLayoutManager(this)

        val btnAdd: Button = findViewById(R.id.btnAddTodo)
        val etTodo: EditText = findViewById(R.id.etTodo)

        btnAdd.setOnClickListener{
            val title = etTodo.text.toString()
            val todo = Todo(title, false)
            todoList.add(todo)
            addapterTodo.notifyItemInserted(todoList.size - 1)
        }


        val addapterSchool = SchoolAdapter(schoolList)
        val rvSchools: RecyclerView = findViewById(R.id.rvSchools)

        rvSchools.adapter = addapterSchool
        rvSchools.layoutManager = LinearLayoutManager(this)

        val btnAddSchool: Button = findViewById(R.id.btnAddSchool)
        val etSchool: EditText = findViewById(R.id.etSchool)

        btnAddSchool.setOnClickListener{
            val title = etSchool.text.toString()
            val school = School(title, false)
            schoolList.add(school)
            addapterTodo.notifyItemInserted(schoolList.size - 1)
        }

    }
}