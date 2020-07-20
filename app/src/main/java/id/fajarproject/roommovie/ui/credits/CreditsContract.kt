package id.fajarproject.roommovie.ui.credits

import android.app.Activity
import androidx.viewpager.widget.ViewPager
import id.fajarproject.roommovie.models.Credits
import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.ui.base.BaseContract
import io.reactivex.Observable


/**
 * Create by Fajar Adi Prasetyo on 20/07/2020.
 */
class CreditsContract {

    interface View : BaseContract.View{
        fun setViewPager(viewPager: ViewPager)
        fun showDataSuccess(data: Credits?)
        fun showDataFailed(message : String)
    }

    interface Presenter : BaseContract.Presenter<View>{
        fun checkData(isMovie : Boolean,id : Int) : Observable<Credits>
        fun loadData(isMovie : Boolean,id : Int)
    }

    interface Fragment{
        fun injectDependency()
        fun setRecycleView()
        fun showData(list: MutableList<CreditsItem?>)
    }
}