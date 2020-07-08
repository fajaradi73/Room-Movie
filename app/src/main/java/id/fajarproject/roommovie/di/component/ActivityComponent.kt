package id.fajarproject.roommovie.di.component

import dagger.Component
import id.fajarproject.roommovie.ui.movieList.MovieListActivity
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.ui.home.HomeActivity
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.search.SearchActivity
import id.fajarproject.roommovie.ui.setting.WebViewActivity
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.tvList.TvListActivity


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
@Component(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(homeActivity: HomeActivity)
    fun inject(movieListActivity: MovieListActivity)
    fun inject(searchActivity: SearchActivity)
    fun inject(webViewActivity: WebViewActivity)
    fun inject(tvListActivity: TvListActivity)
    fun inject(movieDetailActivity: MovieDetailActivity)
    fun inject(tvDetailActivity: TvDetailActivity)
}