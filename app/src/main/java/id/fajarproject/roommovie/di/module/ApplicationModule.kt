package id.fajarproject.roommovie.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import id.fajarproject.roommovie.BaseApp
import id.fajarproject.roommovie.di.scope.PerApplication
import javax.inject.Singleton


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
@Module
class ApplicationModule(private val baseApp: BaseApp) {

    @Provides
    @Singleton
    @PerApplication
    fun provideApplication(): Application {
        return baseApp
    }
}