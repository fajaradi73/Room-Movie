package id.fajarproject.roommovie.di.component

import dagger.Component
import id.fajarproject.roommovie.BaseApp
import id.fajarproject.roommovie.di.module.ApplicationModule


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

@Component(modules = [ApplicationModule::class])
fun interface ApplicationComponent {

    fun inject(application: BaseApp)

}