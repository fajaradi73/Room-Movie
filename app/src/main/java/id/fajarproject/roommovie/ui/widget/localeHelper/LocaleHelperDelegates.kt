package id.fajarproject.roommovie.ui.widget.localeHelper


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import id.fajarproject.roommovie.R
import java.util.*

/**
 * Create by Fajar Adi Prasetyo on 22/07/2020.
 */
interface LocaleHelperActivityDelegate {
    fun setLocale(activity: Activity, newLocale: Locale)
    fun attachBaseContext(newBase: Context): Context
    fun applyOverrideConfiguration(
        baseContext: Context,
        overrideConfiguration: Configuration?
    ): Configuration?

    fun onPaused()
    fun onResumed(activity: Activity)
    fun onCreate(activity: Activity)
    fun getResources(resources: Resources): Resources
}

class LocaleHelperActivityDelegateImpl : LocaleHelperActivityDelegate {
    override fun onCreate(activity: Activity) {
        activity.window.decorView.layoutDirection =
            if (LocaleHelper.isRTL(Locale.getDefault())) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    }

    private var locale: Locale = Locale.getDefault()

    override fun setLocale(activity: Activity, newLocale: Locale) {
        LocaleHelper.setLocale(activity, newLocale)
        locale = newLocale
        refreshActivity(activity)
    }

    override fun attachBaseContext(newBase: Context): Context = LocaleHelper.onAttach(newBase)

    override fun applyOverrideConfiguration(
        baseContext: Context, overrideConfiguration: Configuration?
    ): Configuration? {
        overrideConfiguration?.setTo(baseContext.resources.configuration)
        overrideConfiguration?.setCurrentLocale(Locale.getDefault())
        return overrideConfiguration
    }

    override fun getResources(resources: Resources): Resources {
        return if (resources.configuration.currentLocale == Locale.getDefault()) {
            resources
        } else {
            resources.configuration.setCurrentLocale(Locale.getDefault())
            resources
        }
    }

    override fun onPaused() {
        locale = Locale.getDefault()
    }

    override fun onResumed(activity: Activity) {
        if (locale == Locale.getDefault()) return
//        refreshActivity(activity)
    }

    private fun refreshActivity(activity: Activity){
        val intent = Intent(activity,activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        activity.startActivity(intent)
    }
}

class LocaleHelperApplicationDelegate {
    fun attachBaseContext(base: Context): Context = LocaleHelper.onAttach(base)

    fun onConfigurationChanged(context: Context) {
        LocaleHelper.onAttach(context)
    }
}