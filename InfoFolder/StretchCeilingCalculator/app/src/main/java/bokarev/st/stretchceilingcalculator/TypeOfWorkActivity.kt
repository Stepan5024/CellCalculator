package bokarev.st.stretchceilingcalculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TypeOfWorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.type_of_work_activity)
        try {
            val name = intent.getStringExtra("KEY1")
            //val school6 = intent.getSerializableExtra("School 6") as School
            //val age = intent.getIntExtra("KEY2", 0)
            //Log.d("mytag", "school 6 = ${school6.title}")
        } catch (exp: RuntimeException) {

        }
        val btnReturnToHome: ImageView = findViewById(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            val intent = Intent(this, Calculation::class.java).also {
                it.putExtra("KEY1", "value1")
                it.putExtra("KEY2", "value1")
                it.putExtra("KEY3", "value1")
                //it.putExtra("School 6", School("School 6", false))
            }
            startActivity(intent)
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
}