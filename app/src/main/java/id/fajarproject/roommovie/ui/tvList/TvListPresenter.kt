package id.fajarproject.roommovie.ui.tvList

import android.app.Activity
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.util.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Create by Fajar Adi Prasetyo on 06/07/2020.
 */
class TvListPresenter : TvListContract.Presenter{

    private val subscriptions = CompositeDisposable()
    private lateinit var view: TvListContract.View
    private lateinit var activity: Activity
    private val api : ApiServiceInterface = ApiServiceInterface.create()

    override fun loadData(page: Int, status: String) {
        if (page == 1){
            view.showLoading()
        }
        val subscribe = checkData(page,status).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                view.hideLoading()
                view.showDataSuccess(movie?.movieList ?: arrayListOf())
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

    override fun checkData(page: Int, status: String): Observable<Movie> {
        return when (status) {
            activity.getString(R.string.tv_airing_today) -> {
                api.getTvAiringToday(Constant.API_KEY,page)
            }
            activity.getString(R.string.tv_on_the_air) -> {
                api.getTvOnTheAir(Constant.API_KEY,page)
            }
            activity.getString(R.string.top_rated) -> {
                api.getTvTopRated(Constant.API_KEY,page)
            }
            else -> {
                api.getTvPopular(Constant.API_KEY,page)
            }
        }
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: TvListContract.View, context: C) {
        this.view = view
        this.activity = context as Activity
    }

}