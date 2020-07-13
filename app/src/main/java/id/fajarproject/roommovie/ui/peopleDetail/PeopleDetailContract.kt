package id.fajarproject.roommovie.ui.peopleDetail

import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.models.ExternalIds
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PeopleDetailContract {
    interface View : BaseContract.View{
        fun showDataSuccess(data: PeopleItem)
        fun showDataFailed(message : String)
        fun setViewExternalIDs(data: ExternalIds)
        fun setOpenURL(url : String,status : String)
        fun showDialogNoData()
        fun setOnSetChange(name : String)

    }
    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(idPeople : Int)
        fun getGender(id : Int?) : String
        fun getKnowAs(list: MutableList<String>?) : String
        fun getKnownCredits(list: MutableList<CreditsItem?>?) : Int
    }
}