package bokarev.st.myapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Environment
import androidx.core.content.FileProvider.getUriForFile
import java.io.File


class SharePdf {

    fun sharePdf(context: Context, file: File) {

        val contentUri = getUriForFile(
            context,
            "bokarev.st.myapplication.provider", file
        )
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "application/pdf"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri)


        val chooser = Intent.createChooser(sharingIntent, "Share File 123")
        val resInfoList: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                contentUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        context.startActivity(chooser)
    }
}