package id.fajarproject.roommovie.ui.movie

import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MoviePresenter : MovieContract.Presenter{

    private val subscriptions = CompositeDisposable()
    private lateinit var view: MovieContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()

    override fun loadDataPopular() {
        view.showLoading()
        val subscribe = api.getMoviePopular(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataNowPlaying()
                view.showDataPopularSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataNowPlaying()
                view.showDataPopularFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataNowPlaying() {
        val subscribe = api.getMovieTheater(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataTopRated()
                view.showDataNowPlayingSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataTopRated()
                view.showDataNowPlayingFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataTopRated() {
        val subscribe = api.getMovieTopRated(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataUpcoming()
                view.showDataTopRatedSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataUpcoming()
                view.showDataTopRatedFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataUpcoming() {
        val subscribe = api.getMovieUpcoming(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                loadDataTrending()
                view.showDataUpcomingSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                loadDataTrending()
                view.showDataUpcomingFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataTrending() {
        val subscribe = api.getMovieTrending(Constant.API_KEY,1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                view.hideLoading()
                view.showDataTrendingSuccess(movie?.movieList ?: arrayListOf())
            },{ error ->
                view.hideLoading()
                view.showDataTrendingFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: MovieContract.View, context: C) {
        this.view = view
    }

}