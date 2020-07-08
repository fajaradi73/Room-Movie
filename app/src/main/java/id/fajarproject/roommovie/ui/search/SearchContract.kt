package id.fajarproject.roommovie.ui.search

import androidx.recyclerview.widget.RecyclerView
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseContract
import io.reactivex.Observable


/**
 * Create by Fajar Adi Prasetyo on 03/07/2020.
 */
class SearchContract {

    interface View: BaseContract.View {
        fun setRecycleView()
        fun setScrollRecycleView()
        fun showDataMovieSuccess(list: MutableList<MovieItem?>,isMovie : Boolean)
        fun showDataMovieFailed(message : String)
        fun showDataPeopleSuccess(list: MutableList<PeopleItem?>)
        fun showDataPeopleFailed(message : String)
        fun checkLastData()
        fun checkData()
        fun showData(isShow : Boolean)
        fun search(search: String)
        fun checkAdapter(isPeople: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>
        fun moveToDetail(id : Int,isMovie: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun checkData(page : Int,search : String,status : String)
        fun loadDataMovie(page : Int,search : String)
        fun loadDataTv(page : Int,search : String)
        fun loadDataPeople(page : Int,search : String)
    }
}