package id.fajarproject.roommovie.ui.video

import android.app.Activity
import id.fajarproject.roommovie.models.Videos
import id.fajarproject.roommovie.ui.base.BasePresenter
import id.fajarproject.roommovie.util.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */

class VideoListPresenter : VideoListContract.Presenter,BasePresenter() {
    lateinit var view: VideoListContract.View

    override fun loadData(idMovie: Int, isMovie: Boolean) {
        view.showLoading()
        val subscribe = checkData(idMovie,isMovie).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ data : Videos ->
                view.hideLoading()
                data.results?.let {
                    view.showDataSuccess(it)
                } ?: kotlin.run {
                    view.showDataFailed("No Data Videos")
                }
            },{ error ->
                view.hideLoading()
                view.showDataFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun checkData(idMovie: Int, isMovie: Boolean): Observable<Videos> {
        return if (isMovie)
            api.getMovieVideo(idMovie,Constant.API_KEY)
        else
            api.getTvVideo(idMovie,Constant.API_KEY)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: VideoListContract.View, context: C) {
        this.view = view
        this.activity = context as Activity
    }
}