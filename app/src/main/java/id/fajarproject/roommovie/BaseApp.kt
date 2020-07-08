package id.fajarproject.roommovie

import android.app.Application
import id.fajarproject.roommovie.di.component.ApplicationComponent
import id.fajarproject.roommovie.di.component.DaggerApplicationComponent
import id.fajarproject.roommovie.di.module.ApplicationModule


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class BaseApp: Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()

//        if (BuildConfig.DEBUG) {
//            // Maybe TimberPlant etc.
//        }
    }

    fun setup() {
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