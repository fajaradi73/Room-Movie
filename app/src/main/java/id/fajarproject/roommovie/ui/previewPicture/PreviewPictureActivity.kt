package id.fajarproject.roommovie.ui.previewPicture

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityPreviewPictureBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.PicturesItem
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import org.parceler.Parcels

class PreviewPictureActivity : AppCompatActivity(), PreviewPictureContract.View {

    private var data: MutableList<PicturesItem?> = arrayListOf()
    private var isDetail = false
    private var isBackdrops = true
    private var isReturning: Boolean = false
    private var startingPosition: Int = 0
    private var currentPosition: Int = 0
    private var imagePagerAdapterPreview: PreviewPictureAdapter? = null
    private lateinit var previewPictureBinding: ActivityPreviewPictureBinding

    private val enterElementCallback: SharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(
            names: MutableList<String>,
            sharedElements: MutableMap<String?, View?>
        ) {
            if (isReturning) {
                val sharedElement = imagePagerAdapterPreview?.getView(currentPosition)

                if (startingPosition != currentPosition) {
                    names.clear()
                    names.add(sharedElement?.let { ViewCompat.getTransitionName(it) }
                        ?: getString(R.string.transition_title, currentPosition))

                    sharedElements.clear()
                    sharedElements[sharedElement?.let { ViewCompat.getTransitionName(it) }] =
                        sharedElement
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewPictureBinding = ActivityPreviewPictureBinding.inflate(layoutInflater)
        setContentView(previewPictureBinding.root)
        injectDependency()
        getDataIntent()
        ActivityCompat.postponeEnterTransition(this)
        ActivityCompat.setEnterSharedElementCallback(this, enterElementCallback)

        setToolbar()

        val index = intent.getIntExtra(Constant.position, 0)
        startingPosition = if (index > 0) index else 0
        currentPosition =
            savedInstanceState?.getInt(Constant.SAVED_CURRENT_PAGE_POSITION) ?: startingPosition

        setViewPager()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(Constant.SAVED_CURRENT_PAGE_POSITION, currentPosition)
    }

    override fun finishAfterTransition() {
        isReturning = true
        val data = Intent()
        data.putExtra(Constant.EXTRA_STARTING_ALBUM_POSITION, startingPosition)
        data.putExtra(Constant.EXTRA_CURRENT_ALBUM_POSITION, currentPosition)
        data.putExtra(Constant.isBackdrops, isBackdrops)
        setResult(Activity.RESULT_OK, data)
        super.finishAfterTransition()
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(previewPictureBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            previewPictureBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.colorWhite)
        )
        val statusBarHeight = Util.getStatusBarHeight(this)
        val size = Util.convertDpToPixel(24F)
        Util.setMargins(previewPictureBinding.toolbar, 0, statusBarHeight, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.statusBarColor = ContextCompat.getColor(this, R.color.greyTransparent)

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (statusBarHeight > size) {
                val decor = window.decorView
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun getDataIntent() {
        data = Parcels.unwrap(intent.getParcelableExtra(Constant.dataPicture))
        isDetail = intent.getBooleanExtra(Constant.isDetail, false)
        isBackdrops = intent.getBooleanExtra(Constant.isBackdrops, true)
    }

    override fun setViewPager() {
        imagePagerAdapterPreview = PreviewPictureAdapter(this, data, currentPosition, isDetail)
        previewPictureBinding.viewPager.adapter = imagePagerAdapterPreview
        previewPictureBinding.viewPager.currentItem = currentPosition
        previewPictureBinding.viewPager.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.let {
            when (it.itemId) {
                android.R.id.home -> {
                    supportFinishAfterTransition()
                    return true
                }
                else -> {
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}