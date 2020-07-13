package id.fajarproject.roommovie.ui.movieDetail

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import id.fajarproject.roommovie.models.*
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 07/07/2020.
 */

class MovieDetailContract {
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
        fun setViewBackdrops(list: MutableList<PicturesItem?>)
        fun setViewPosters(list: MutableList<PicturesItem?>)
        fun setViewRecommendation(list: MutableList<MovieItem?>)
        fun showPreviewImage(view : android.view.View, position: Int, data: MutableList<PicturesItem?>,isBackdrops : Boolean)
        fun moveToPicture(title : String,list: MutableList<PicturesItem?>)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(id : Int)
        fun getLanguage(string: String?,list: MutableList<SpokenLanguagesItem?>?) : String
        fun getGenre(list : MutableList<GenresItem?>?) : String
    }
}