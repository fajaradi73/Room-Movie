package id.fajarproject.roommovie.ui.picture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.PicturesItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.previewPicture.PreviewPictureActivity
import id.fajarproject.roommovie.ui.widget.GridSpacingItemDecoration
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_picture.*
import org.parceler.Parcels
import javax.inject.Inject

class PictureActivity : BaseActivity(),PictureContract.View {

    @Inject lateinit var presenter : PictureContract.Presenter
    lateinit var adapter : PictureAdapter
    var list : MutableList<PicturesItem?> = arrayListOf()
    private var reenterState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        injectDependency()
        presenter.attach(this,this)
        setToolbar()
        setUI()
        title       = intent.getStringExtra(Constant.title)
        list        = Parcels.unwrap(intent.getParcelableExtra(Constant.dataPicture))
        showLoading()
        if (list.isNotEmpty()){
            showDataSuccess(list)
        }else{
            checkData(false)
        }
        ActivityCompat.setExitSharedElementCallback(this, presenter.exitElementCallback(reenterState,rvVideo))

    }

    override fun showDataSuccess(list: MutableList<PicturesItem?>) {
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
        adapter                 = PictureAdapter(this,list)
        rvVideo.adapter         = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                view?.let {
                    showPreviewImage(it,position,list)
                }
            }
        })
        setViewBackToTop()
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorPicture",message)
        checkData(false)
    }

    override fun checkData(isShow: Boolean) {
        hideLoading()
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

    override fun showPreviewImage(view: View, position: Int, data: MutableList<PicturesItem?>) {
        val intent = Intent(this, PreviewPictureActivity::class.java)
        intent.putExtra(Constant.position,position)
        intent.putExtra(Constant.dataPicture, Parcels.wrap(data))
        val p1      = Pair.create(view, ViewCompat.getTransitionName(view))
        val bundle  = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1).toBundle()

        startActivity(intent,bundle)
    }

    override fun clearReenterState() {
        reenterState = null
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        reenterState = Bundle(data?.extras)
        reenterState?.let {
            val startingPosition    = it.getInt(Constant.EXTRA_STARTING_ALBUM_POSITION)
            val currentPosition     = it.getInt(Constant.EXTRA_CURRENT_ALBUM_POSITION)
            if (startingPosition != currentPosition) rvVideo.scrollToPosition(currentPosition)
            ActivityCompat.postponeEnterTransition(this)

            rvVideo.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    rvVideo.viewTreeObserver.removeOnPreDrawListener(this)
                    ActivityCompat.startPostponedEnterTransition(this@PictureActivity)
                    return true
                }
            })
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