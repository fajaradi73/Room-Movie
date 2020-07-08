package id.fajarproject.roommovie.ui.search

import android.app.Activity
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.Movie
import id.fajarproject.roommovie.models.people.People
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 03/07/2020.
 */
class SearchPresenter : SearchContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private lateinit var view: SearchContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var activity : Activity

    override fun checkData(page: Int, search: String, status: String) {
        when {
            status.contains(activity.getString(R.string.tv)) -> {
                loadDataTv(page,search)
            }
            status.contains(activity.getString(R.string.people)) -> {
                loadDataPeople(page,search)
            }
            else -> {
                loadDataMovie(page,search)
            }
        }
    }

    override fun loadDataMovie(page: Int, search: String) {
        if (page == 1){
            view.showLoading()
        }
        val subscribe = api.getMovieSearch(Constant.API_KEY,page,search).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                view.hideLoading()
                view.showDataMovieSuccess(movie?.movieList ?: arrayListOf(),true)
            },{ error ->
                view.hideLoading()
                if (page == 1){
                    view.showDataMovieFailed(error.message ?: "")
                }else{
                    view.showDataMovieSuccess(arrayListOf(),true)
                }
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataTv(page: Int, search: String) {
        if (page == 1){
            view.showLoading()
        }
        val subscribe = api.getTvSearch(Constant.API_KEY,page,search).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Movie? ->
                view.hideLoading()
                view.showDataMovieSuccess(movie?.movieList ?: arrayListOf(),false)
            },{ error ->
                view.hideLoading()
                if (page == 1){
                    view.showDataMovieFailed(error.message ?: "")
                }else{
                    view.showDataMovieSuccess(arrayListOf(),false)
                }
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataPeople(page: Int, search: String) {
        if (page == 1){
            view.showLoading()
        }
        val subscribe = api.getPeopleSearch(Constant.API_KEY,page,search).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ data : People? ->
                view.hideLoading()
                view.showDataPeopleSuccess(data?.results ?: arrayListOf())
            },{ error ->
                view.hideLoading()
                if (page == 1){
                    view.showDataPeopleFailed(error.message ?: "")
                }else{
                    view.showDataPeopleSuccess(arrayListOf())
                }
            })
        subscriptions.add(subscribe)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: SearchContract.View, context: C) {
        this.view = view
        this.activity = context as Activity
    }

}