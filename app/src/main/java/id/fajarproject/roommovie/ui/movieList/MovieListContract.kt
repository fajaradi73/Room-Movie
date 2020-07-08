package id.fajarproject.roommovie.ui.movieList

import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseContract
import io.reactivex.Observable


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MovieListContract {

    interface View: BaseContract.View {
        fun setRecycleView()
        fun setScrollRecycleView()
        fun showDataSuccess(list: MutableList<MovieItem?>)
        fun showDataFailed(message : String)
        fun checkLastData()
        fun checkData()
        fun showData(isShow : Boolean)
        fun getTitle(title : String) : String?
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun loadData(page : Int,status : String)
        fun checkData(page : Int,status : String): Observable<Movie>
    }
}