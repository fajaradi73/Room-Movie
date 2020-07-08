package id.fajarproject.roommovie.ui.home

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.fajarproject.roommovie.api.ApiServiceInterface
import id.fajarproject.roommovie.models.Genre
import id.fajarproject.roommovie.models.LanguagesItem
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class HomePresenter : HomeContract.Presenter{

    private val subscriptions = CompositeDisposable()
    private lateinit var view: HomeContract.View
    private val api : ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var activity: Activity

    override fun loadDataLanguage() {
        view.showLoading()
        val subscribe = api.getLanguages(Constant.API_KEY).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ data : MutableList<LanguagesItem?>? ->
                view.hideLoading()
                saveLanguage(data)
            },{ error ->
                view.hideLoading()
                Log.e("ErrorGenre",error?.message ?: "")
            })

        if (!view.checkDataPreferences(Constant.genreMovie)){
            loadData()
        }else{
            view.setUI()
        }

        subscriptions.add(subscribe)
    }

    override fun saveLanguage(list: MutableList<LanguagesItem?>?) {
        val type: Type? = object : TypeToken<MutableList<LanguagesItem?>?>() {}.type
        val json: String = Gson().toJson(list, type)
        AppPreference.writePreference(activity,Constant.language,json)
    }

    override fun loadData() {
        view.showLoading()
        val subscribe = api.getMovieGenres(Constant.API_KEY).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Genre? ->
                view.hideLoading()
                view.saveGenres(movie?.genreList,Constant.genreMovie)
            },{ error ->
                view.hideLoading()
                view.setUI()
                Log.e("ErrorGenre",error?.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun loadDataTv() {
        view.showLoading()
        val subscribe = api.getTvGenres(Constant.API_KEY).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ movie : Genre? ->
                view.hideLoading()
                view.saveGenres(movie?.genreList,Constant.genreTv)
            },{ error ->
                view.hideLoading()
                Log.e("ErrorGenre",error?.message ?: "")
            })
        subscriptions.add(subscribe)
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun <C> attach(view: HomeContract.View, context: C) {
        this.view = view
        this.activity   = context as Activity
    }

}