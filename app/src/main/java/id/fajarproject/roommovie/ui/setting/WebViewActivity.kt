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
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    private var intentStatus : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        injectDependency()
        intentStatus = intent.getStringExtra(Constant.INTENT_STATUS)
        setToolbar()
        setWebView()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this, R.color.iconColorPrimary))
        title = intentStatus
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(){
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
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
                injectCSS(AppPreference.getBooleanPreferenceByName(this@WebViewActivity,Constant.isNightMode))
                super.onPageFinished(view, url)
            }
        }
        when (intentStatus) {
            getString(R.string.privacy_policy) -> {
                webView.loadUrl("file:///android_asset/privacy_policy.html")
            }
            getString(R.string.terms_of_use) -> {
                webView.loadUrl("file:///android_asset/term_of_use.html")
            }
            getString(R.string.contribution) -> {
                webView.loadUrl("file:///android_asset/contribution.html")
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
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }

    private fun injectCSS(isNightMode: Boolean) {
        if (isNightMode) {
            webView.loadUrl(
                "javascript:document.body.style.setProperty(\"color\", \"white\");"
            )
            webView.loadUrl(
                "javascript:document.body.style.setProperty(\"background-color\", \"black\");"
            )
        }else{
            webView.loadUrl(
                "javascript:document.body.style.setProperty(\"color\", \"black\");"
            )
            webView.loadUrl(
                "javascript:document.body.style.setProperty(\"background-color\", \"white\");"
            )
        }
    }
}