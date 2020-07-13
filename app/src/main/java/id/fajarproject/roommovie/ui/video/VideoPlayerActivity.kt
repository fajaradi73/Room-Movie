package id.fajarproject.roommovie.ui.video

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.LoadingWebViewClient
import id.fajarproject.roommovie.util.Util
import id.fajarproject.roommovie.util.VideoWebChromeClient
import kotlinx.android.synthetic.main.activity_video.*

class VideoPlayerActivity : BaseActivity() {

    private var keyVideo : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        injectDependency()
        setToolbar()
        title    = intent.getStringExtra(Constant.title)
        keyVideo = intent.getStringExtra(Constant.keyVideo)
        if (keyVideo?.isNotEmpty() == true){
            setViewVideo()
        }
    }

    fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this, R.color.iconColorPrimary))

    }

    private fun setViewVideo(){
        val nonVideoLayout: View =
            findViewById(R.id.videoLayout)

        val videoLayout: ViewGroup =
            findViewById(R.id.videoFullScreenOverLay)

        ivThumbnail.visibility  = View.GONE
        videoPlayer.visibility  = View.VISIBLE
        progressBar.visibility  = View.GONE

        //noinspection all
        val loadingView = layoutInflater.inflate(
            R.layout.custom_loading_video,
            null)

        val webChromeClient                 = VideoWebChromeClient(nonVideoLayout,videoLayout,loadingView,videoPlayer)

        webChromeClient.setOnToggledFullscreen(object : VideoWebChromeClient.ToggledFullscreenCallback{
            override fun toggledFullscreen(fullscreen: Boolean) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    val attrs = window.attributes
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    window.attributes = attrs
                } else {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    val attrs = window.attributes
                    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                    attrs.flags =
                        attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                    window.attributes = attrs
                }
            }
        })
        videoPlayer.webChromeClient = webChromeClient
        videoPlayer.webViewClient   = LoadingWebViewClient(this)
        videoPlayer.settings.mediaPlaybackRequiresUserGesture = false

        videoPlayer.loadDataWithBaseURL("http://www.youtube.com", Util.getScriptAutoPlayYoutube(keyVideo), "text/html", "utf-8", null)
//        videoPlayer.loadUrl(Constant.BASE_VIDEO + keyVideo + "?autoplay=1")
    }

}