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
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.VideosItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.widget.GridSpacingItemDecoration
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_video_list.*
import javax.inject.Inject

class VideoListActivity : BaseActivity(),VideoListContract.View {

    @Inject lateinit var presenter: VideoListContract.Presenter
    lateinit var adapter: VideoListAdapter
    var idMovie : Int = -1
    var isMovie = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)
        injectDependency()
        presenter.attach(this,this)
        idMovie = intent.getIntExtra(Constant.idMovie,-1)
        isMovie = intent.getBooleanExtra(Constant.isMovie,true)
        setToolbar()
        setUI()

        title    = intent.getStringExtra(Constant.title)

        if (idMovie != -1){
            presenter.loadData(idMovie,isMovie)
        }else{
            checkData(false)
        }
    }

    override fun showDataSuccess(list: MutableList<VideosItem?>) {
        checkData(true)
        rvVideo.layoutManager = GridLayoutManager(this,2)
        rvVideo.addItemDecoration(
            GridSpacingItemDecoration(
                activity!!,
                2,
                1,
                true
            )
        )
        rvVideo.itemAnimator    = DefaultItemAnimator()
        adapter                 = VideoListAdapter(this,list)
        rvVideo.adapter         = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.key?.let {
                    val intent = Intent(this@VideoListActivity, VideoPlayerActivity::class.java)
                    intent.putExtra(Constant.keyVideo,it)
                    startActivity(intent)
                }
            }
        })
        setViewBackToTop()
    }


    override fun showDataFailed(message: String) {
        Log.e("ErrorVideo",message)
        checkData(false)
    }

    override fun checkData(isShow: Boolean) {
        if (isShow){
            refreshLayout.visibility    = View.VISIBLE
            noData.visibility           = View.GONE
        }else{
            refreshLayout.visibility    = View.GONE
            noData.visibility           = View.VISIBLE
        }
    }

    override fun setViewBackToTop() {
        rvVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem    = (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                val lastVisibleItem     = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItem < adapter.itemCount){
                    btnBackToTop.show()
                }else{
                    btnBackToTop.hide()
                }
                if (firstVisibleItem == 0){
                    btnBackToTop.hide()
                }
            }
        })
        btnBackToTop.setOnClickListener {
            rvVideo.smoothScrollToPosition(0)
        }
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this, R.color.iconColorPrimary))
    }

    override fun setUI() {
        btnBackToTop.hide()
        btnBackToTop.setOnClickListener { rvVideo.smoothScrollToPosition(0) }
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.loadData(idMovie,isMovie)
        }
    }

    override fun showLoading() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        shimmerView.startShimmer()
        refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        refreshLayout.visibility    = View.VISIBLE
    }

}