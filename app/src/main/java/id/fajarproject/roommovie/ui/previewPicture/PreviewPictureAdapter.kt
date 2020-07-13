package id.fajarproject.roommovie.ui.previewPicture

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import id.fajarproject.roommovie.models.PicturesItem
import android.util.SparseArray
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PreviewPictureAdapter(
    private val activity: Activity,
    private val items: MutableList<PicturesItem?>,
    private val currentPos: Int,
    private val isDetail : Boolean = false) : PagerAdapter() {

    private val views = SparseArray<View?>(if (isDetail) {
        if (items.size > 5){
            5
        }else{
            items.size
        }
    }else{
        items.size
    })

    @SuppressLint("ClickableViewAccessibility")
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val item = items[position]
        val imageView = ImageView(collection.context)
        val transitionName = activity.getString(R.string.transition_title,position)
        ViewCompat.setTransitionName(imageView, transitionName)
        views.put(position, imageView)
        Glide.with(collection.context)
            .load(Constant.BASE_IMAGE + item?.filePath)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.1f)
            .placeholder(Util.circleLoading(activity))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    ActivityCompat.startPostponedEnterTransition(activity)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (position == currentPos) {
                        imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                                ActivityCompat.startPostponedEnterTransition(activity)
                                return true
                            }
                        })
                    }
                    return false
                }

            })
            .into(imageView)
        imageView.setOnTouchListener(ImageMatrixTouchHandler(activity))

        collection.addView(imageView)
        return imageView
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
//        if (position == views.size()){
//            views.removeAt(position - 1)
//        }else{
//            views.removeAt(position)
//        }
        collection.removeView(view as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount() =  if (isDetail) {
        if (items.size > 5){
            5
        }else{
            items.size
        }
    }else{
        items.size
    }

    fun getView(position: Int): View? = views.get(position)

}