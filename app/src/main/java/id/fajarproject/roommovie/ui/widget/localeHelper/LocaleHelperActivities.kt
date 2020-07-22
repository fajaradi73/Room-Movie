package id.fajarproject.roommovie.ui.widget.localeHelper


import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * Create by Fajar Adi Prasetyo on 22/07/2020.
 */
open class LocaleAwareCompatActivity : AppCompatActivity() {
    private val localeDelegate = LocaleHelperActivityDelegateImpl()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeDelegate.onCreate(this)
    }

    override fun onResume() {
        super.onResume()
        localeDelegate.onResumed(this)
    }

    override fun onPause() {
        super.onPause()
        localeDelegate.onPaused()
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            localeDelegate.applyOverrideConfiguration(baseContext, overrideConfiguration)
        )
    }

    override fun getResources(): Resources = localeDelegate.getResources(super.getResources())

    open fun updateLocale(locale: Locale) {
        localeDelegate.setLocale(this, locale)
    }
}

open class LocaleAwareApplication : Application() {
    private val localeAppDelegate = LocaleHelperApplicationDelegate()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
    }
}