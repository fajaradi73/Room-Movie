package id.fajarproject.roommovie.ui.movieList

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
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MovieListPresenter : MovieListContract.Presenter{

    private val subscriptions = CompositeDisposable()
    private lateinit var view: MovieListContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var activity: Activity

    override fun loadData(page : Int,status: String) {
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

    override fun checkData(page: Int, status: String) : Observable<Movie> {
        return when (status) {
            activity.getString(R.string.now_playing) -> {
                api.getMovieTheater(Constant.API_KEY,page)
            }
            activity.getString(R.string.top_rated) -> {
                api.getMovieTopRated(Constant.API_KEY,page)
            }
            activity.getString(R.string.upcoming) -> {
                api.getMovieUpcoming(Constant.API_KEY,page)
            }
            activity.getString(R.string.trending) -> {
                api.getMovieTrending(Constant.API_KEY,page)
            }
            else -> {
                api.getMoviePopular(Constant.API_KEY,page)
            }
        }
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: MovieListContract.View, context: C) {
        this.view = view
        this.activity = context as Activity
    }

}