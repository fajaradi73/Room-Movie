package id.fajarproject.roommovie.ui.movieDetail

import android.app.Activity
import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.models.SpokenLanguagesItem
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Create by Fajar Adi Prasetyo on 07/07/2020.
 */
class MovieDetailPresenter : MovieDetailContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private lateinit var view: MovieDetailContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var activity: Activity

    override fun loadData(id : Int) {
        view.showLoading()
        val subscribe = api.getMovieDetail(id,Constant.API_KEY,"images,videos,keywords,credits,recommendations,external_ids")
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

    override fun <C> attach(view: MovieDetailContract.View, context: C) {
        this.view = view
        this.activity = context as Activity
    }

    override fun getLanguage(string: String?,list: MutableList<SpokenLanguagesItem?>?) : String{
        var language = ""
        if (list != null) {
            for (data in list){
                if (data?.iso6391 == string){
                    language = data?.name ?: ""
                    break
                }
            }
        }
        return language
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