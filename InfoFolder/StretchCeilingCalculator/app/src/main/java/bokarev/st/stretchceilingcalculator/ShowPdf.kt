package bokarev.st.stretchceilingcalculator

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.IOException

class ShowPdf {
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var currentPage: PdfRenderer.Page
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private val pageIndex = 0

    @Throws(IOException::class)
     fun showPage(file: File): Bitmap? {
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        // This is the PdfRenderer we use to render the PDF.
        pdfRenderer =
            PdfRenderer(parcelFileDescriptor)
        if (pdfRenderer.pageCount <= pageIndex) {
            return null
        }
        currentPage = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(
            currentPage.width, currentPage.height,
            Bitmap.Config.ARGB_8888
        )
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        closeRenderer()

        // We are ready to show the Bitmap
        return bitmap
    }

    @Throws(IOException::class)
    private fun closeRenderer() {
        currentPage.close()
        pdfRenderer.close()
        parcelFileDescriptor.close()
    }


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