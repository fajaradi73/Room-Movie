package id.fajarproject.roommovie.ui.people

import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.people.People
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 03/07/2020.
 */

class PeoplePresenter : PeopleContract.Presenter {
    private val subscriptions = CompositeDisposable()
    private lateinit var view: PeopleContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()

    override fun loadData(page : Int) {
        if (page == 1){
            view.showLoading()
        }
        val subscribe = api.getPeoplePopular(Constant.API_KEY,page).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ data : People? ->
                view.hideLoading()
                view.showDataSuccess(data?.results ?: arrayListOf())
            },{ error ->
                view.hideLoading()
                if (page == 1){
                    view.showDataFailed(error.message ?: "")
                }else{
                    view.showDataSuccess(arrayListOf())
                }
            })
        subscriptions.add(subscribe)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: PeopleContract.View, context: C) {
        this.view = view
    }

}