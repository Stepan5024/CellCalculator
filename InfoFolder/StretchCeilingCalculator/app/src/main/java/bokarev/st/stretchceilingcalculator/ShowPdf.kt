package bokarev.st.stretchceilingcalculator

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.IOException

class ShowPdf {

    fun findFilePath(file: String): File? {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            file
        )
        if (!file.exists()) {
            return null
        }
        return file
    }

}