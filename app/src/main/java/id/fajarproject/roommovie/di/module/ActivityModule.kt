package id.fajarproject.roommovie.di.module

import android.app.Activity
import dagger.Module
import dagger.Provides
import id.fajarproject.roommovie.ui.home.HomeContract
import id.fajarproject.roommovie.ui.home.HomePresenter
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailContract
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailPresenter
import id.fajarproject.roommovie.ui.movieList.MovieListContract
import id.fajarproject.roommovie.ui.movieList.MovieListPresenter
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailContract
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailPresenter
import id.fajarproject.roommovie.ui.picture.PictureContract
import id.fajarproject.roommovie.ui.picture.PicturePresenter
import id.fajarproject.roommovie.ui.search.SearchContract
import id.fajarproject.roommovie.ui.search.SearchPresenter
import id.fajarproject.roommovie.ui.tvDetail.TvDetailContract
import id.fajarproject.roommovie.ui.tvDetail.TvDetailPresenter
import id.fajarproject.roommovie.ui.tvList.TvListContract
import id.fajarproject.roommovie.ui.tvList.TvListPresenter
import id.fajarproject.roommovie.ui.video.VideoListContract
import id.fajarproject.roommovie.ui.video.VideoListPresenter


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
@Module
class ActivityModule(private var activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun providePresenter(): MovieListContract.Presenter {
        return MovieListPresenter()
    }

    @Provides
    fun provideHomePresenter() : HomeContract.Presenter{
        return HomePresenter()
    }
    @Provides
    fun provideSearchPresenter() : SearchContract.Presenter{
        return SearchPresenter()
    }
    @Provides
    fun provideTvPresenter() : TvListContract.Presenter{
        return TvListPresenter()
    }
    @Provides
    fun provideMovieDetailPresenter() : MovieDetailContract.Presenter{
        return MovieDetailPresenter()
    }
    @Provides
    fun provideTvDetailPresenter() : TvDetailContract.Presenter{
        return TvDetailPresenter()
    }
    @Provides
    fun provideVideoListPresenter() : VideoListContract.Presenter{
        return VideoListPresenter()
    }
    @Provides
    fun providePicturePresenter() : PictureContract.Presenter{
        return PicturePresenter()
    }
    @Provides
    fun providePeopleDetailPresenter() : PeopleDetailContract.Presenter{
        return PeopleDetailPresenter()
    }

}