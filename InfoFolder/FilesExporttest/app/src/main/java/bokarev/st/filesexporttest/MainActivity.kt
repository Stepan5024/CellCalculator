package bokarev.st.filesexporttest

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.itextpdf.*
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // on below line we are creating
    // a variable for our image view.
    lateinit var generatePDFBtn: Button

    // declaring width and height
    // for our PDF file.
    var pageHeight = 1120
    var pageWidth = 792

    // creating a bitmap variable
    // for storing our images
    lateinit var bmp: Bitmap
    lateinit var scaledbmp: Bitmap

    // on below line we are creating a
    // constant code for runtime permissions.
    var PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*
        // on below line we are initializing our button with its id.
        generatePDFBtn = findViewById(R.id.idBtnGeneratePdf)

        // on below line we are initializing our bitmap and scaled bitmap.
        bmp = BitmapFactory.decodeResource(resources, R.drawable.mypic)
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)

        // on below line we are checking permission
        if (checkPermissions()) {
            // if permission is granted we are displaying a toast message.
            Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
        } else {
            // if the permission is not granted
            // we are calling request permission method.
            requestPermission()
        }

        // on below line we are adding on click listener for our generate button.
        generatePDFBtn.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                // on below line we are calling generate
                // PDF method to generate our PDF file.
                generatePDF()
            }
        }


 */

      /*  val mDoc = Document()
        val mFileName = "testPDFFile"
            /*SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())*/

        val mFilePath =
            Environment.getExternalStorageDirectory().toString() + "/" + mFileName + ".pdf"
        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()
            mDoc.addAuthor("Stepan Bokarev")
            mDoc.add(Paragraph("string my text"))
            mDoc.close()

        } catch (e: Exception) {

        }

        val sendIntent: Intent = Intent().apply {


            action = Intent.ACTION_SEND
            /*putExtra(Intent.EXTRA_TEXT, "This is my text to send.\n" +
                "This is my text to send.\n" +
                "This is my text to send.\n" +
                "This is my text to send.\n")*/
            putExtra(Intent.EXTRA_STREAM, mFilePath)
            type = "application/pdf"
            // after storing our pdf to that


            /*  action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "This is my text to send.\n" +
                "This is my text to send.\n" +
                "This is my text to send.\n" +
                "This is my text to send.\n")
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "text/plain"

       */
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
*/


/*

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "fileName.txt")
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, "")
        }

        resultLauncher.launch(intent)

 */


}

var resultLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val uri = data!!.data
            try {
                val outPutStream = this.contentResolver.openOutputStream(uri!!)
                outPutStream?.write("CodeLIb file save Demo".toByteArray())
                outPutStream?.close()
                Toast.makeText(this, "File saved", Toast.LENGTH_LONG).show()

                // creating an object variable
                // for our PDF document.
                var pdfDocument: PdfDocument = PdfDocument()

                // two variables for paint "paint" is used
                // for drawing shapes and we will use "title"
                // for adding text in our PDF file.
                var paint: Paint = Paint()
                var title: Paint = Paint()

                // we are adding page info to our PDF file
                // in which we will be passing our pageWidth,
                // pageHeight and number of pages and after that
                // we are calling it to create our PDF.
                var myPageInfo: PdfDocument.PageInfo? =
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

                // below line is used for setting
                // start page for our PDF file.
                var myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)

                // creating a variable for canvas
                // from our page of PDF.
                var canvas: Canvas = myPage.canvas

                // below line is used to draw our image on our PDF file.
                // the first parameter of our drawbitmap method is
                // our bitmap
                // second parameter is position from left
                // third parameter is position from top and last
                // one is our variable for paint.
                canvas.drawBitmap(scaledbmp, 56F, 40F, paint)

                // below line is used for adding typeface for
                // our text which we will be adding in our PDF file.
                title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                // below line is used for setting text size
                // which we will be displaying in our PDF file.
                title.textSize = 15F

                // below line is sued for setting color
                // of our text inside our PDF file.
                title.color = ContextCompat.getColor(this@MainActivity, R.color.purple_200)

                // below line is used to draw text in our PDF file.
                // the first parameter is our text, second parameter
                // is position from start, third parameter is position from top
                // and then we are passing our variable of paint which is title.
                canvas.drawText("A portal for IT professionals.", 209F, 100F, title)
                canvas.drawText("Geeks for Geeks", 209F, 80F, title)
                title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                title.color = ContextCompat.getColor(this@MainActivity, R.color.purple_200)
                title.textSize = 15F

                // below line is used for setting
                // our text to center of PDF.
                title.textAlign = Paint.Align.CENTER
                canvas.drawText(
                    "This is sample document which we have created.",
                    396F,
                    560F,
                    title
                )

                // after adding all attributes to our
                // PDF file we will be finishing our page.
                pdfDocument.finishPage(myPage)

                // below line is used to set the name of
                // our PDF file and its path.
                val file: File = File(Environment.getExternalStorageDirectory(), "GFG.pdf")

                val sendIntent: Intent = Intent().apply {


                    try {
                        // after creating a file name we will
                        // write our PDF file to that location.
                        pdfDocument.writeTo(FileOutputStream(file))

                        // on below line we are displaying a toast message as PDF file generated..
                        Toast.makeText(
                            applicationContext,
                            "PDF file generated..",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        // below line is used
                        // to handle error
                        e.printStackTrace()

                        // on below line we are displaying a toast message as fail to generate PDF
                        Toast.makeText(
                            applicationContext,
                            "Fail to generate PDF file..",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        action = Intent.ACTION_SEND
                        /*putExtra(Intent.EXTRA_TEXT, "This is my text to send.\n" +
                            "This is my text to send.\n" +
                            "This is my text to send.\n" +
                            "This is my text to send.\n")*/
                        putExtra(Intent.EXTRA_STREAM, file)
                        type = "application/pdf"
                        // after storing our pdf to that
                        // location we are closing our PDF file.
                        pdfDocument.close()

                        /*  action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "This is my text to send.\n" +
                            "This is my text to send.\n" +
                            "This is my text to send.\n" +
                            "This is my text to send.\n")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "text/plain"

                   */
                    }
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            } catch (e: Exception) {
                print(e.localizedMessage)
                Toast.makeText(this, "File not saved", Toast.LENGTH_LONG).show()
            }
        }
    }

// on below line we are creating a generate PDF method
// which is use to generate our PDF file.
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun generatePDF() {
    // creating an object variable
    // for our PDF document.
    var pdfDocument: PdfDocument = PdfDocument()

    // two variables for paint "paint" is used
    // for drawing shapes and we will use "title"
    // for adding text in our PDF file.
    var paint: Paint = Paint()
    var title: Paint = Paint()

    // we are adding page info to our PDF file
    // in which we will be passing our pageWidth,
    // pageHeight and number of pages and after that
    // we are calling it to create our PDF.
    var myPageInfo: PdfDocument.PageInfo? =
        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

    // below line is used for setting
    // start page for our PDF file.
    var myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)

    // creating a variable for canvas
    // from our page of PDF.
    var canvas: Canvas = myPage.canvas

    // below line is used to draw our image on our PDF file.
    // the first parameter of our drawbitmap method is
    // our bitmap
    // second parameter is position from left
    // third parameter is position from top and last
    // one is our variable for paint.
    canvas.drawBitmap(scaledbmp, 56F, 40F, paint)

    // below line is used for adding typeface for
    // our text which we will be adding in our PDF file.
    title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

    // below line is used for setting text size
    // which we will be displaying in our PDF file.
    title.textSize = 15F

    // below line is sued for setting color
    // of our text inside our PDF file.
    title.color = ContextCompat.getColor(this, R.color.purple_200)

    // below line is used to draw text in our PDF file.
    // the first parameter is our text, second parameter
    // is position from start, third parameter is position from top
    // and then we are passing our variable of paint which is title.
    canvas.drawText("A portal for IT professionals.", 209F, 100F, title)
    canvas.drawText("Geeks for Geeks", 209F, 80F, title)
    title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    title.color = ContextCompat.getColor(this, R.color.purple_200)
    title.textSize = 15F

    // below line is used for setting
    // our text to center of PDF.
    title.textAlign = Paint.Align.CENTER
    canvas.drawText("This is sample document which we have created.", 396F, 560F, title)

    // after adding all attributes to our
    // PDF file we will be finishing our page.
    pdfDocument.finishPage(myPage)

    // below line is used to set the name of
    // our PDF file and its path.
    val file: File = File(Environment.getExternalStorageDirectory(), "GFG.pdf")

    try {
        // after creating a file name we will
        // write our PDF file to that location.
        pdfDocument.writeTo(FileOutputStream(file))

        // on below line we are displaying a toast message as PDF file generated..
        Toast.makeText(applicationContext, "PDF file generated..", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        // below line is used
        // to handle error
        e.printStackTrace()

        // on below line we are displaying a toast message as fail to generate PDF
        Toast.makeText(applicationContext, "Fail to generate PDF file..", Toast.LENGTH_SHORT)
            .show()
    }
    // after storing our pdf to that
    // location we are closing our PDF file.
    pdfDocument.close()
}

fun checkPermissions(): Boolean {
    // on below line we are creating a variable for both of our permissions.

    // on below line we are creating a variable for
    // writing to external storage permission
    var writeStoragePermission = ContextCompat.checkSelfPermission(
        applicationContext,
        WRITE_EXTERNAL_STORAGE
    )

    // on below line we are creating a variable
    // for reading external storage permission
    var readStoragePermission = ContextCompat.checkSelfPermission(
        applicationContext,
        READ_EXTERNAL_STORAGE
    )

    // on below line we are returning true if both the
    // permissions are granted and returning false
    // if permissions are not granted.
    return writeStoragePermission == PackageManager.PERMISSION_GRANTED
            && readStoragePermission == PackageManager.PERMISSION_GRANTED
}

// on below line we are creating a function to request permission.
fun requestPermission() {

    // on below line we are requesting read and write to
    // storage permission for our application.
    ActivityCompat.requestPermissions(
        this,
        arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSION_CODE
    )
}

// on below line we are calling
// on request permission result.
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    // on below line we are checking if the
    // request code is equal to permission code.
    if (requestCode == PERMISSION_CODE) {

        // on below line we are checking if result size is > 0
        if (grantResults.size > 0) {

            // on below line we are checking
            // if both the permissions are granted.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                == PackageManager.PERMISSION_GRANTED
            ) {

                // if permissions are granted we are displaying a toast message.
                Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()

            } else {

                // if permissions are not granted we are
                // displaying a toast message as permission denied.
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
}