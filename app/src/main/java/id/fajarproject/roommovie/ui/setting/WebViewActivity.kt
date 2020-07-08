package id.fajarproject.roommovie.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_movie_list.*
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.activity_web_view.toolbar

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
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this, R.color.colorBlack))
        title = intentStatus
    }

    private fun setWebView(){
        if (intentStatus == getString(R.string.privacy_policy)){
            webView.loadUrl("file:///android_asset/privacy_policy.html")
        }else if (intentStatus == getString(R.string.terms_of_use)){
            webView.loadUrl("file:///android_asset/term_of_use.html")
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
}