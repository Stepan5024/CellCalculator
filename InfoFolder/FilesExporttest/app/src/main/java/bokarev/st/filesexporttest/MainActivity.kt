package bokarev.st.filesexporttest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Toast
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    val CREATE_FILE  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "fileName.txt")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, "")
        }
        startActivityForResult(intent, CREATE_FILE )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK){
            val uri = data!!.data
            try {
                val outPutStream  = this.contentResolver.openOutputStream(uri!!)
                outPutStream?.write("CodeLIb file save Demo".toByteArray())
                outPutStream?.close()
                Toast.makeText(this, "File saved", Toast.LENGTH_LONG).show()
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "This is my text to send.\n" +
                            "This is my text to send.\n" +
                            "This is my text to send.\n" +
                            "This is my text to send.\n")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }catch (e:Exception){
                print(e.localizedMessage)
                Toast.makeText(this, "File not saved", Toast.LENGTH_LONG).show()
            }
        }
    }
}