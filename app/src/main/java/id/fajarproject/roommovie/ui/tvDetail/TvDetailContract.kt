package id.fajarproject.roommovie.ui.tvDetail

import android.widget.RelativeLayout
import android.widget.TextView
import id.fajarproject.roommovie.models.*
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 08/07/2020.
 */
class TvDetailContract {
    interface View : BaseContract.View{
        fun showDataSuccess(data: MovieItem)
        fun showDataFailed(message : String)
        fun showDialogNoData()
        fun setOnSetChange(name : String)
        fun setClickableSpan(textView : TextView)
        fun setViewExternalIDs(data: ExternalIds)
        fun setOpenURL(url : String,status : String)
        fun setViewKeyword(list: MutableList<KeywordsItem?>)
        fun configureChip(item: KeywordsItem?, params : RelativeLayout.LayoutParams, i : Int)
        fun setViewCasts(list: MutableList<CastItem?>)
        fun setViewVideo(list: MutableList<VideosItem?>)
        fun setViewBackdrops(list: MutableList<BackdropsItem?>)
        fun setViewPosters(list: MutableList<PostersItem?>)
        fun setViewRecommendation(list: MutableList<MovieItem?>)
        fun setViewNetwork(list: MutableList<NetworksItem?>)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(id : Int)
        fun getGenre(list : MutableList<GenresItem?>?) : String
    }
}