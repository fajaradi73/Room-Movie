package id.fajarproject.roommovie.ui.people

import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 03/07/2020.
 */
class PeopleContract {
    interface View : BaseContract.View{
        fun setRecycleView()
        fun setScrollRecycleView()
        fun showDataSuccess(list: MutableList<PeopleItem?>)
        fun showDataFailed(message : String)
        fun checkLastData()
        fun checkData()
        fun showData(isShow : Boolean)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(page : Int)
    }
}