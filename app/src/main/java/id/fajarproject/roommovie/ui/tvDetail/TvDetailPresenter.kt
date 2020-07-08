package id.fajarproject.roommovie.ui.tvDetail

import android.app.Activity
import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 08/07/2020.
 */
class TvDetailPresenter : TvDetailContract.Presenter {
    private val subscriptions = CompositeDisposable()
    private lateinit var view: TvDetailContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var activity: Activity

    override fun loadData(id : Int) {
        view.showLoading()
        val subscribe = api.getTvDetail(id, Constant.API_KEY,"credits,videos,images,keywords,recommendations,external_ids,content_ratings")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ data : MovieItem ->
                view.hideLoading()
                view.showDataSuccess(data)
            },{ error ->
                view.hideLoading()
                view.showDataFailed(error.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: TvDetailContract.View, context: C) {
        this.view = view
        this.activity = context as Activity
    }

    override fun getGenre(list : MutableList<GenresItem?>?) : String{
        var genre = ""
        if (list != null) {
            for (i in list.indices){
                val name    = list[i]?.name ?: ""
                if (name.isNotEmpty()){
                    genre += if (i != list.lastIndex){
                        "$name, "
                    }else{
                        name
                    }
                }
            }
        }
        return genre
    }
}