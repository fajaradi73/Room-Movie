package id.fajarproject.roommovie.ui.base


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class BaseContract {

    interface Presenter<in T> {
        fun subscribe()
        fun unsubscribe()
        fun<C> attach(view: T, context : C)
    }

    interface View {
        fun injectDependency()
        fun setToolbar()
        fun setUI()
        fun showLoading()
        fun hideLoading()
    }
}