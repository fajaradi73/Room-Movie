package id.fajarproject.roommovie.ui.widget.localeHelper


import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * Create by Fajar Adi Prasetyo on 22/07/2020.
 */
@Suppress("DEPRECATION")
val Configuration.currentLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)
    } else {
        locale
    }

@Suppress("DEPRECATION")
fun Configuration.setCurrentLocale(locale: Locale) {
    this.setLocale(locale)
}