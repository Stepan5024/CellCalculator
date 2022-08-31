package bokarev.st.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkPermission()) {
            Toast.makeText(this, "Storage permission not there", Toast.LENGTH_SHORT).show()
            requestPermission()
        }

        val receipt = Receipt(
            "formatterDate.format(LocalDateTime.now())",
            5.toDouble(),
            76,
            45.toDouble(),
            78.toInt(),
            72.toDouble(),
            9.toInt(),
            ""
        )
        //gotShowPdfPage(receipt)
        if (receipt.filePath.isEmpty() || receipt.filePath.isBlank()) {
            generatePdf(receipt)
            displayPdf(receipt)
        } else {
            displayPdf(receipt)
        }

    }
    private val STORAGE_REQUEST_CODE = 99;

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            //bellow 11
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), STORAGE_REQUEST_CODE
            )
        }
    }

    private val storageActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //Todo call Save
                    Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }
            } else {
                //bellow 11
            }
        }
    private fun generatePdf(receipt: Receipt) {
        val bitmapFactory = BitmapFactory.decodeResource(
            this.resources, R.drawable.pizzahead
        )
        var filePath = GeneratePdf.generate(
            bitmapFactory, receipt
        )
        receipt.filePath = filePath


    }

    private fun displayPdf(receipt: Receipt) {

        var filePath = ShowPdf().findFilePath(receipt.filePath)
        if (filePath != null) {

                SharePdf().sharePdf(this, filePath)

        } else {
            generatePdf(receipt)
        }
    }


    private fun gotShowPdfPage(receipt: Receipt) {
        val fragment = ShowPdfFragment.newInstance()
        val args = Bundle()
        args.putSerializable("RECEIPT_EXTRA", receipt)
        fragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}