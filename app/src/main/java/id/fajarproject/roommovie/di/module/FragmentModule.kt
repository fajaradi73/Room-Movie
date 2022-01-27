package id.fajarproject.roommovie.di.module

import dagger.Module
import dagger.Provides
import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.ui.movie.MovieContract
import id.fajarproject.roommovie.ui.movie.MoviePresenter
import id.fajarproject.roommovie.ui.people.PeopleContract
import id.fajarproject.roommovie.ui.people.PeoplePresenter
import id.fajarproject.roommovie.ui.tv.TvContract
import id.fajarproject.roommovie.ui.tv.TvPresenter


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
@Module
class FragmentModule {

    @Provides
    fun provideMoviePresenter() : MovieContract.Presenter{
        return MoviePresenter()
    }

    @Provides
    fun provideTvPresenter() : TvContract.Presenter{
        return TvPresenter()
    }

    @Provides
    fun providePeoplePresenter() : PeopleContract.Presenter{
        return PeoplePresenter()
    }

    @Provides
    fun provideApiService(): ApiServiceInterface {
        return ApiServiceInterface.create()
    }
}