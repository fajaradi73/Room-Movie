package id.fajarproject.roommovie.ui.home

import androidx.fragment.app.Fragment
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.models.LanguagesItem
import id.fajarproject.roommovie.ui.base.BaseContract


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class HomeContract {

    interface View : BaseContract.View{
        fun addFragment(fragments: Fragment, tag: String)
        fun saveGenres(list: MutableList<GenresItem?>?,prefName : String)
        fun checkDataPreferences(prefName: String) : Boolean
        fun setHintSearch(status : String)
        fun moveToSearch(voiceSearch : String)
        fun changeToolbar(isSearch : Boolean)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun loadDataLanguage()
        fun saveLanguage(list: MutableList<LanguagesItem?>?)
        fun loadData()
        fun loadDataTv()
    }
}