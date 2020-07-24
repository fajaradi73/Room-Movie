package id.fajarproject.roommovie

import androidx.appcompat.app.AppCompatDelegate
import id.fajarproject.roommovie.di.component.ApplicationComponent
import id.fajarproject.roommovie.di.component.DaggerApplicationComponent
import id.fajarproject.roommovie.di.module.ApplicationModule
import id.fajarproject.roommovie.ui.widget.localeHelper.LocaleAwareApplication
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class BaseApp: LocaleAwareApplication() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        if (AppPreference.getBooleanPreferenceByName(this, Constant.isNightMode)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        instance = this
        setup()

    }

    private fun setup() {
        component = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this)).build()
        component.inject(this)
    }

    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    companion object {
        lateinit var instance: BaseApp private set
    }
}