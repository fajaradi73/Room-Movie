package id.fajarproject.roommovie.ui.base

import android.app.Activity
import id.fajarproject.roommovie.api.ApiServiceInterface
import io.reactivex.disposables.CompositeDisposable


/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */
open class BasePresenter {
    val subscriptions = CompositeDisposable()
    val api : ApiServiceInterface = ApiServiceInterface.create()
    lateinit var activity: Activity
}