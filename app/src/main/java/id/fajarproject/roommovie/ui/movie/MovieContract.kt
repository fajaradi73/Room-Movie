package id.fajarproject.roommovie.ui.movie

import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MovieContract {

    interface View : BaseContract.View{
        fun moveToActivity(status: String)
        fun showDataPopularSuccess(list: MutableList<MovieItem?>)
        fun showDataPopularFailed(message : String)
        fun showDataNowPlayingSuccess(list: MutableList<MovieItem?>)
        fun showDataNowPlayingFailed(message: String)
        fun showDataTopRatedSuccess(list: MutableList<MovieItem?>)
        fun showDataTopRatedFailed(message: String)
        fun showDataUpcomingSuccess(list: MutableList<MovieItem?>)
        fun showDataUpcomingFailed(message: String)
        fun showDataTrendingSuccess(list: MutableList<MovieItem?>)
        fun showDataTrendingFailed(message: String)
        fun setItemClickListener(adapter: MovieAdapter)
        fun moveToDetail(id : Int)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadDataPopular()
        fun loadDataNowPlaying()
        fun loadDataTopRated()
        fun loadDataUpcoming()
        fun loadDataTrending()
    }
}