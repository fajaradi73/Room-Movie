package id.fajarproject.roommovie.ui.video

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityVideoListBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.VideosItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.widget.GridSpacingItemDecoration
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject

class VideoListActivity : BaseActivity(), VideoListContract.View {

    @Inject
    lateinit var presenter: VideoListContract.Presenter
    lateinit var adapter: VideoListAdapter
    var idMovie: Int = -1
    var isMovie = true
    private lateinit var videoListBinding: ActivityVideoListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoListBinding = ActivityVideoListBinding.inflate(layoutInflater)
        setContentView(videoListBinding.root)
        injectDependency()
        presenter.attach(this, this)
        idMovie = intent.getIntExtra(Constant.idMovie, -1)
        isMovie = intent.getBooleanExtra(Constant.isMovie, true)
        setToolbar()
        setUI()

        title = intent.getStringExtra(Constant.title)

        if (idMovie != -1) {
            presenter.loadData(idMovie, isMovie)
        } else {
            checkData(false)
        }
    }

    override fun showDataSuccess(list: MutableList<VideosItem?>) {
        checkData(true)
        videoListBinding.rvVideo.layoutManager = GridLayoutManager(this, 2)
        videoListBinding.rvVideo.addItemDecoration(
            GridSpacingItemDecoration(
                this,
                2,
                1,
                true
            )
        )
        videoListBinding.rvVideo.itemAnimator = DefaultItemAnimator()
        adapter = VideoListAdapter(this, list)
        videoListBinding.rvVideo.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.key?.let {
                    val intent = Intent(this@VideoListActivity, VideoPlayerActivity::class.java)
                    intent.putExtra(Constant.keyVideo, it)
                    startActivity(intent)
                }
            }
        })
        setViewBackToTop()
    }


    override fun showDataFailed(message: String) {
        Log.e("ErrorVideo", message)
        checkData(false)
    }

    override fun checkData(isShow: Boolean) {
        if (isShow) {
            videoListBinding.refreshLayout.visibility = View.VISIBLE
            videoListBinding.noData.visibility = View.GONE
        } else {
            videoListBinding.refreshLayout.visibility = View.GONE
            videoListBinding.noData.visibility = View.VISIBLE
        }
    }

    override fun setViewBackToTop() {
        videoListBinding.rvVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem =
                    (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                val lastVisibleItem =
                    (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItem < adapter.itemCount) {
                    videoListBinding.btnBackToTop.show()
                } else {
                    videoListBinding.btnBackToTop.hide()
                }
                if (firstVisibleItem == 0) {
                    videoListBinding.btnBackToTop.hide()
                }
            }
        })
        videoListBinding.btnBackToTop.setOnClickListener {
            videoListBinding.rvVideo.smoothScrollToPosition(0)
        }
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(videoListBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            videoListBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )
    }

    override fun setUI() {
        videoListBinding.btnBackToTop.hide()
        videoListBinding.btnBackToTop.setOnClickListener {
            videoListBinding.rvVideo.smoothScrollToPosition(
                0
            )
        }
        videoListBinding.refreshLayout.setOnRefreshListener {
            videoListBinding.refreshLayout.isRefreshing = false
            presenter.loadData(idMovie, isMovie)
        }
    }

    override fun showLoading() {
        videoListBinding.shimmerView.visibility = View.VISIBLE
        videoListBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        videoListBinding.shimmerView.startShimmer()
        videoListBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        videoListBinding.shimmerView.stopShimmer()
        videoListBinding.shimmerView.visibility = View.GONE
        videoListBinding.refreshLayout.visibility = View.VISIBLE
    }

}