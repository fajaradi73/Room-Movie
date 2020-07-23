package id.fajarproject.roommovie.ui.seasons

import id.fajarproject.roommovie.models.SeasonsItem
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 23/07/2020.
 */

class SeasonContract {
    interface View : BaseContract.View{
        fun showDataSuccess(list : MutableList<SeasonsItem?>)
        fun showDataFailed(message : String)
        fun checkData(isShow : Boolean)
        fun setViewBackToTop()
    }
}