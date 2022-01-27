package id.fajarproject.roommovie.util

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.swiperefreshlayout.widget.CircularProgressDrawable


/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */

class LoadingWebViewClient(var context: Context) : WebViewClient() {
    lateinit var circularProgressDrawable: CircularProgressDrawable

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        circularProgressDrawable                = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth    = 5f
        circularProgressDrawable.centerRadius   = 30f
        circularProgressDrawable.start()
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String
    ): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        circularProgressDrawable.stop()
    }
}