package id.fajarproject.roommovie.ui.peopleDetail

import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.models.ExternalIds
import id.fajarproject.roommovie.models.people.KnownForItem
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseContract
import java.util.Comparator


/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PeopleDetailContract {

    interface View : BaseContract.View{
        fun showDataSuccess(data: PeopleItem)
        fun showDataFailed(message : String)
        fun setViewKnownFor(list: MutableList<CreditsItem?>)
        fun setViewExternalIDs(data: ExternalIds)
        fun setViewCredits(list: MutableList<CreditsItem?>)
        fun setOpenURL(url : String,status : String)
        fun showDialogNoData()
        fun setOnSetChange(name : String)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadData(idPeople : Int)
        fun getGender(id : Int?) : String
        fun getKnowAs(list: MutableList<String>?) : String
        fun getKnownCredits(list: MutableList<CreditsItem?>?) : Int
        fun sortKnown(): Comparator<CreditsItem?>
        fun sortYear(): Comparator<CreditsItem?>
        fun getListCredits(isCredits : Boolean,list: MutableList<CreditsItem?>) : MutableList<CreditsItem?>
        fun getKnownFor(knownFor : String ,data : MutableList<CreditsItem?>) : MutableList<CreditsItem?>
    }
}