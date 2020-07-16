package id.fajarproject.roommovie.ui.discover

import android.widget.ArrayAdapter
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseContract
import io.reactivex.Observable


/**
 * Create by Fajar Adi Prasetyo on 14/07/2020.
 */
class DiscoverContract {
    interface Parsing {
        fun onPassData(data: String)
    }
    interface Fragment{
        fun setViewSpinner()
        fun setAction()
        fun getAdapter(list: Array<String>) : ArrayAdapter<String?>
    }
    interface View : BaseContract.View{
        fun setRecycleView()
        fun setScrollRecycleView()
        fun showDataSuccess(list: MutableList<MovieItem?>)
        fun showDataFailed(message : String)
        fun checkLastData()
        fun checkData()
        fun showData(isShow : Boolean)
        fun moveToDetail(id : Int)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(isMovie : Boolean,sortBy : String,genre : String,keywords : String,page : Int)
        fun checkData(isMovie: Boolean,sortBy : String,genre : String,keywords : String,page : Int) : Observable<Movie>
    }
}