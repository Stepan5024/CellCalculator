package bokarev.st.stretchceilingcalculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.core.content.FileProvider.getUriForFile
import java.io.File


class SharePdf {

    @SuppressLint("QueryPermissionsNeeded")
    fun sharePdf(context: Context, file: File) {

        val contentUri = getUriForFile(
            context,
            "bokarev.st.stretchceilingcalculator.provider", file
        )
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "application/pdf"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri)


        val chooser = Intent.createChooser(sharingIntent, "Отправить смету")
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