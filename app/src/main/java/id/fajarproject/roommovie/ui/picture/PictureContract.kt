package id.fajarproject.roommovie.ui.picture

import android.os.Bundle
import android.view.View
import androidx.core.app.SharedElementCallback
import id.fajarproject.roommovie.models.PicturesItem
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PictureContract {
    interface View : BaseContract.View{
        fun showDataSuccess(list : MutableList<PicturesItem?>)
        fun showDataFailed(message : String)
        fun checkData(isShow : Boolean)
        fun setViewBackToTop()
        fun showPreviewImage(view : android.view.View, position: Int, data: MutableList<PicturesItem?>)
        fun clearReenterState()
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun exitElementCallback(reenterState : Bundle?, view : android.view.View): SharedElementCallback
    }
}