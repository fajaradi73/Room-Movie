package id.fajarproject.roommovie.ui.discover

import android.app.Activity
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.ui.base.BasePresenter
import id.fajarproject.roommovie.util.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 16/07/2020.
 */
class DiscoverPresenter : BasePresenter(),DiscoverContract.Presenter {

    lateinit var view: DiscoverContract.View

    override fun loadData(isMovie: Boolean, sortBy: String, genre: String, keywords: String,page : Int) {
        if (page == 1){
            view.showLoading()
        }
        val subscribe = checkData(isMovie, sortBy, genre, keywords, page).subscribeOn(Schedulers.io())
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

    override fun checkData(
        isMovie: Boolean,
        sortBy: String,
        genre: String,
        keywords: String,
        page : Int
    ): Observable<Movie> {
        return if (isMovie){
            api.getMovieDiscover(Constant.API_KEY,sortBy,genre,keywords,page)
        }else{
            api.getTvDiscover(Constant.API_KEY,sortBy,genre,keywords,page)
        }
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
    }

    override fun <C> attach(view: DiscoverContract.View, context: C) {
        this.view       = view
        this.activity   = context as Activity
    }

}