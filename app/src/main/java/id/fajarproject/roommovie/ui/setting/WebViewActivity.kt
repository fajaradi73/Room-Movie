package id.fajarproject.roommovie.ui.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityWebViewBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


class WebViewActivity : AppCompatActivity() {

    private var intentStatus: String? = ""
    private lateinit var webViewBinding: ActivityWebViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webViewBinding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(webViewBinding.root)
        injectDependency()
        intentStatus = intent.getStringExtra(Constant.INTENT_STATUS)
        setToolbar()
        setWebView()
    }

    private fun setToolbar() {
        setSupportActionBar(webViewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            webViewBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )
        title = intentStatus
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        webViewBinding.webView.settings.javaScriptEnabled = true
        webViewBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val host = Uri.parse(url).host
                host?.let {
                    if (host.contains("android_asset")) {
                        // This is my web site, so do not override; let my WebView load the page
                        return false
                    }
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Inject CSS on PageFinished
                injectCSS(
                    AppPreference.getBooleanPreferenceByName(
                        this@WebViewActivity,
                        Constant.isNightMode
                    )
                )
                super.onPageFinished(view, url)
            }
        }
        when (intentStatus) {
            getString(R.string.privacy_policy) -> {
                webViewBinding.webView.loadUrl("file:///android_asset/privacy_policy.html")
            }
            getString(R.string.terms_of_use) -> {
                webViewBinding.webView.loadUrl("file:///android_asset/term_of_use.html")
            }
            getString(R.string.contribution) -> {
                webViewBinding.webView.loadUrl("file:///android_asset/contribution.html")
            }
        }
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onBackPressed() {
        if (webViewBinding.webView.canGoBack()) {
            webViewBinding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun injectCSS(isNightMode: Boolean) {
        if (isNightMode) {
            webViewBinding.webView.loadUrl(
                "javascript:document.body.style.setProperty(\"color\", \"white\");"
            )
            webViewBinding.webView.loadUrl(
                "javascript:document.body.style.setProperty(\"background-color\", \"black\");"
            )
        } else {
            webViewBinding.webView.loadUrl(
                "javascript:document.body.style.setProperty(\"color\", \"black\");"
            )
            webViewBinding.webView.loadUrl(
                "javascript:document.body.style.setProperty(\"background-color\", \"white\");"
            )
        }
    }
}