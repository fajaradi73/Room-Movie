package id.fajarproject.roommovie.ui.credits

import android.app.Activity
import id.fajarproject.roommovie.models.Credits
import id.fajarproject.roommovie.ui.base.BasePresenter
import id.fajarproject.roommovie.util.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 20/07/2020.
 */

class CreditsPresenter : BasePresenter(),CreditsContract.Presenter {
    lateinit var view: CreditsContract.View

    override fun checkData(isMovie: Boolean,id : Int): Observable<Credits> {
        return if (isMovie)
            api.getMovieCredits(id,Constant.API_KEY)
        else
            api.getTvCredits(id,Constant.API_KEY)
    }

    override fun loadData(isMovie: Boolean,id : Int) {
        view.showLoading()
        val subscribe = checkData(isMovie,id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({data : Credits? ->
                view.hideLoading()
                view.showDataSuccess(data)
            },{ error ->
                view.hideLoading()
                view.showDataFailed(error.message ?: "Error")
            })

        subscriptions.add(subscribe)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
    }

    override fun <C> attach(view: CreditsContract.View, context: C) {
        this.view       = view
        this.activity   = context as Activity
    }
}