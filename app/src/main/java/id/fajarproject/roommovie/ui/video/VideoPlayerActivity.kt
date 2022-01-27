package id.fajarproject.roommovie.ui.video

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityVideoBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.LoadingWebViewClient
import id.fajarproject.roommovie.util.Util
import id.fajarproject.roommovie.util.VideoWebChromeClient

class VideoPlayerActivity : BaseActivity() {

    private var keyVideo: String? = null
    private lateinit var videoBinding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoBinding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(videoBinding.root)
        injectDependency()
        setToolbar()
        title = intent.getStringExtra(Constant.title)
        keyVideo = intent.getStringExtra(Constant.keyVideo)
        if (keyVideo?.isNotEmpty() == true) {
            setViewVideo()
        }
    }

    fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    fun setToolbar() {
        setSupportActionBar(videoBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            videoBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )

    }

    private fun setViewVideo() {
        val nonVideoLayout: View =
            findViewById(R.id.videoLayout)

        val videoLayout: ViewGroup =
            findViewById(R.id.videoFullScreenOverLay)

        videoBinding.ivThumbnail.visibility = View.GONE
        videoBinding.videoPlayer.visibility = View.VISIBLE
        videoBinding.progressBar.visibility = View.GONE

        //noinspection all
        val loadingView = layoutInflater.inflate(
            R.layout.custom_loading_video,
            null
        )

        val webChromeClient =
            VideoWebChromeClient(nonVideoLayout, videoLayout, loadingView, videoBinding.videoPlayer)

        webChromeClient.setOnToggledFullscreen(object :
            VideoWebChromeClient.ToggledFullscreenCallback {
            override fun toggledFullscreen(fullscreen: Boolean) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    val attrs = window.attributes
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    window.attributes = attrs
                } else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    val attrs = window.attributes
                    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                    attrs.flags =
                        attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                    window.attributes = attrs
                }
            }
        })
        videoBinding.videoPlayer.webChromeClient = webChromeClient
        videoBinding.videoPlayer.webViewClient = LoadingWebViewClient(this)
        videoBinding.videoPlayer.settings.mediaPlaybackRequiresUserGesture = false

        videoBinding.videoPlayer.loadDataWithBaseURL(
            "http://www.youtube.com",
            Util.getScriptAutoPlayYoutube(keyVideo),
            "text/html",
            "utf-8",
            null
        )
//        videoPlayer.loadUrl(Constant.BASE_VIDEO + keyVideo + "?autoplay=1")
    }

}