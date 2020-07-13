package id.fajarproject.roommovie.ui.picture

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.ui.base.BasePresenter
import id.fajarproject.roommovie.util.Constant

/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PicturePresenter : BasePresenter(),PictureContract.Presenter {
    lateinit var view: PictureContract.View

    override fun subscribe() {
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun exitElementCallback(reenterState : Bundle?,view : View) = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (reenterState != null) {
                val startingPosition   = reenterState.getInt(Constant.EXTRA_STARTING_ALBUM_POSITION)
                val currentPosition    = reenterState.getInt(Constant.EXTRA_CURRENT_ALBUM_POSITION)
                if (startingPosition != currentPosition) {
                    // Current element has changed, need to override previous exit transitions
                    val newTransitionName   = activity.getString(R.string.transition_title,currentPosition)
                    val newSharedElement    = view.findViewWithTag<ConstraintLayout>(newTransitionName)

                    if (newSharedElement != null) {
                        names.clear()
                        names.add(newTransitionName)

                        sharedElements.clear()
                        sharedElements[newTransitionName] = newSharedElement
                    }
                }
            }
        }
    }

    override fun <C> attach(view: PictureContract.View, context: C) {
        this.view   = view
        this.activity   = context as Activity
    }
}