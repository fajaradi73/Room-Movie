package id.fajarproject.roommovie.ui.tv

import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 02/07/2020.
 */
class TvPresenter : TvContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private lateinit var view: TvContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()

    override fun loadDataPopular() {
        val subscribe = api.getTvPopular(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataTopRated()
                view.showDataPopularSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataTopRated()
                view.showDataPopularFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataAiringToday() {
        view.showLoading()
        val subscribe = api.getTvAiringToday(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataOnTheAir()
                view.showDataAiringTodaySuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataOnTheAir()
                view.showDataAiringTodayFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataTopRated() {
        val subscribe = api.getTvTopRated(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                view.hideLoading()
                view.showDataTopRatedSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                view.hideLoading()
                view.showDataTopRatedFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataOnTheAir() {
        val subscribe = api.getTvOnTheAir(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataPopular()
                view.showDataOnTheAirSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataPopular()
                view.showDataOnTheAirFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: TvContract.View, context: C) {
        this.view = view
    }
}