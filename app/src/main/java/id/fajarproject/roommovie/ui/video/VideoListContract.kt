package id.fajarproject.roommovie.ui.video

import id.fajarproject.roommovie.models.Videos
import id.fajarproject.roommovie.models.VideosItem
import id.fajarproject.roommovie.ui.base.BaseContract
import io.reactivex.Observable


/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */
class VideoListContract {

    interface View : BaseContract.View{
        fun showDataSuccess(list : MutableList<VideosItem?>)
        fun showDataFailed(message : String)
        fun checkData(isShow : Boolean)
        fun setViewBackToTop()
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(idMovie : Int,isMovie : Boolean)
        fun checkData(idMovie: Int,isMovie: Boolean) : Observable<Videos>
    }
}