package id.fajarproject.roommovie.ui.peopleDetail

import android.app.Activity
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BasePresenter
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.Comparator


/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PeopleDetailPresenter : BasePresenter(),PeopleDetailContract.Presenter {
    lateinit var view: PeopleDetailContract.View
    override fun loadData(idPeople: Int) {
        view.showLoading()
        val subscribe = api.getPeopleDetail(idPeople,Constant.API_KEY,"combined_credits,external_ids")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ data : PeopleItem ->
                view.showDataSuccess(data)
            },{ error ->
                view.hideLoading()
                view.showDataFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun getGender(id: Int?): String {
        return when (id) {
            1 -> {
                activity.getString(R.string.female)
            }
            2 -> {
                activity.getString(R.string.male)
            }
            else -> {
                "Unspecified (not set)"
            }
        }
    }

    override fun getKnowAs(list: MutableList<String>?) : String{
        var knownAs = ""
        list?.let {
            for (i in it.indices){
                knownAs += if (i != it.lastIndex){
                    "${it[i]} \n"
                }else{
                    it[i]
                }
            }
        }
        return knownAs
    }

    override fun subscribe() {
    }

    override fun getKnownCredits(list: MutableList<CreditsItem?>?) : Int {
        var total = 0
        list?.let {
            for (item in list){
                item?.voteCount?.let {
                    if (item.voteCount > 0){
                        total += 1
                    }
                }
            }
        }
        return total
    }

    override fun getKnownFor(knownFor : String ,data : MutableList<CreditsItem?>) : MutableList<CreditsItem?>{
        val list = arrayListOf<CreditsItem?>()
        for (item in data){
            if (item?.department == knownFor){
                list.add(item)
            }
        }
        return list
    }

    override fun sortKnown() = Comparator<CreditsItem?> { o1, o2 ->
        return@Comparator o1?.voteCount?.let { o2?.voteCount?.compareTo(it) } ?: 0
    }

    override fun sortYear() : Comparator<CreditsItem?> {
        return Comparator { o1, o2 ->
            val long1: Long =
                Util.dateTimeToMillis(o1?.releaseDate ?: o1?.firstAirDate ?: "", "yyyy-MM-dd")
            val long2: Long =
                Util.dateTimeToMillis(o2?.releaseDate ?: o2?.firstAirDate ?: "", "yyyy-MM-dd")
            long2.compareTo(long1)
        }
    }

    override fun getListCredits(
        isCredits: Boolean,
        list: MutableList<CreditsItem?>
    ): MutableList<CreditsItem?> {
        val data : MutableList<CreditsItem?> = list
        if (isCredits){
            Collections.sort(data,sortYear())
        }else{
            Collections.sort(data,sortKnown())
        }
        return data
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: PeopleDetailContract.View, context: C) {
        this.view = view
        this.activity   = context as Activity
    }
}