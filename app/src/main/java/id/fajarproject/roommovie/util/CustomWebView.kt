package id.fajarproject.roommovie.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView


/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */

class CustomWebView : WebView {
    inner class JavascriptInterface {
        fun notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
        {
            Log.d("___", "GOT IT")
            // This code is not executed in the UI thread, so we must force that to happen
            Handler(Looper.getMainLooper()).post {
                if (videoEnabledWebChromeClient != null) {
                    videoEnabledWebChromeClient!!.onHideCustomView()
                }
            }
        }
    }

    private var videoEnabledWebChromeClient: VideoWebChromeClient? = null
    private var addedJavascriptInterface: Boolean

    constructor(context: Context) : super(context) {
        addedJavascriptInterface = false
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        addedJavascriptInterface = false
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        addedJavascriptInterface = false
    }

    /**
     * Indicates if the video is being displayed using a custom view (typically full-screen)
     *
     * @return true it the video is being displayed using a custom view (typically full-screen)
     */
    fun isVideoFullscreen(): Boolean {
        return videoEnabledWebChromeClient != null && videoEnabledWebChromeClient?.isVideoFullscreen == true
    }

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun setWebChromeClient(client: WebChromeClient?) {
        settings.javaScriptEnabled = true
        if (client is VideoWebChromeClient) {
            videoEnabledWebChromeClient = client
        }
        super.setWebChromeClient(client)
    }

    override fun loadData(
        data: String,
        mimeType: String?,
        encoding: String?
    ) {
        addJavascriptInterface()
        super.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        addJavascriptInterface()
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadUrl(url: String) {
        addJavascriptInterface()
        super.loadUrl(url)
    }

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String?, String?>
    ) {
        addJavascriptInterface()
        super.loadUrl(url, additionalHttpHeaders)
    }

    @SuppressLint("JavascriptInterface")
    private fun addJavascriptInterface() {
        if (!addedJavascriptInterface) {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(
                JavascriptInterface(),
                "_VideoEnabledWebView"
            ) // Must match Javascript interface name of VideoEnabledWebChromeClient
            addedJavascriptInterface = true
        }
    }
}
