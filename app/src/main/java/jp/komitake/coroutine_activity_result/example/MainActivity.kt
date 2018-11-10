package jp.komitake.coroutine_activity_result.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import jp.komitake.coroutine_activity_result.ActivityResult
import jp.komitake.coroutine_activity_result.R
import jp.komitake.coroutine_activity_result.activityResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val getContentIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
                val result = activityResult(getContentIntent)
                if (result is ActivityResult.Ok) {
                    val ext = when (val type = contentResolver.getType(result.data.data)) {
                        null -> "png"
                        else -> MimeTypeMap.getSingleton().getExtensionFromMimeType(type) ?: "png"
                    }
                    val filename = "image.$ext"
                    contentResolver.openInputStream(result.data.data).use { input ->
                        openFileOutput(filename, Context.MODE_PRIVATE).use { output ->
                            input.copyTo(output)
                        }
                    }
                    image.setImageURI(Uri.fromFile(getFileStreamPath(filename)))
                }
            }
        }
    }
}
