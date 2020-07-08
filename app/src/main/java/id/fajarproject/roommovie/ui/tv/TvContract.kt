package id.fajarproject.roommovie.ui.tv

import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseContract

/**
 * Create by Fajar Adi Prasetyo on 02/07/2020.
 */
class TvContract {

    interface View : BaseContract.View{
        fun moveToActivity(status: String)
        fun showDataPopularSuccess(list: MutableList<MovieItem?>)
        fun showDataPopularFailed(message : String)
        fun showDataAiringTodaySuccess(list: MutableList<MovieItem?>)
        fun showDataAiringTodayFailed(message: String)
        fun showDataTopRatedSuccess(list: MutableList<MovieItem?>)
        fun showDataTopRatedFailed(message: String)
        fun showDataOnTheAirSuccess(list: MutableList<MovieItem?>)
        fun showDataOnTheAirFailed(message: String)
        fun setItemClickListener(adapter: TvAdapter)
        fun moveToDetail(id : Int)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadDataPopular()
        fun loadDataAiringToday()
        fun loadDataTopRated()
        fun loadDataOnTheAir()
    }
}