package id.fajarproject.roommovie.ui.tvList

import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseContract
import io.reactivex.Observable


/**
 * Create by Fajar Adi Prasetyo on 06/07/2020.
 */
class TvListContract {
    interface View: BaseContract.View {
        fun setRecycleView()
        fun setScrollRecycleView()
        fun showDataSuccess(list: MutableList<MovieItem?>)
        fun showDataFailed(message : String)
        fun checkLastData()
        fun checkData()
        fun showData(isShow : Boolean)
        fun getTitle(title : String) : String?
        fun moveToDetail(id : Int)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun loadData(page : Int,status : String)
        fun checkData(page : Int,status : String): Observable<Movie>
    }
}