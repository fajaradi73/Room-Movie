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
import id.fajarproject.roommovie.databinding.ActivityPictureBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.PicturesItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.previewPicture.PreviewPictureActivity
import id.fajarproject.roommovie.ui.widget.GridSpacingItemDecoration
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import org.parceler.Parcels
import javax.inject.Inject

class PictureActivity : BaseActivity(), PictureContract.View {

    @Inject
    lateinit var presenter: PictureContract.Presenter
    lateinit var adapter: PictureAdapter
    var list: MutableList<PicturesItem?> = arrayListOf()
    private var reenterState: Bundle? = null
    private lateinit var pictureBinding: ActivityPictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pictureBinding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(pictureBinding.root)
        injectDependency()
        presenter.attach(this, this)
        setToolbar()
        setUI()
        title = intent.getStringExtra(Constant.title)
        list = Parcels.unwrap(intent.getParcelableExtra(Constant.dataPicture))
        showLoading()
        if (list.isNotEmpty()) {
            showDataSuccess(list)
        } else {
            checkData(false)
        }
        ActivityCompat.setExitSharedElementCallback(
            this,
            presenter.exitElementCallback(reenterState, pictureBinding.rvVideo)
        )

    }

    override fun showDataSuccess(list: MutableList<PicturesItem?>) {
        checkData(true)
        pictureBinding.rvVideo.layoutManager = GridLayoutManager(this, 2)
        pictureBinding.rvVideo.addItemDecoration(
            GridSpacingItemDecoration(
                this,
                2,
                1,
                true
            )
        )
        pictureBinding.rvVideo.itemAnimator = DefaultItemAnimator()
        adapter = PictureAdapter(this, list)
        pictureBinding.rvVideo.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                view?.let {
                    showPreviewImage(it, position, list)
                }
            }
        })
        setViewBackToTop()
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorPicture", message)
        checkData(false)
    }

    override fun checkData(isShow: Boolean) {
        hideLoading()
        if (isShow) {
            pictureBinding.refreshLayout.visibility = View.VISIBLE
            pictureBinding.noData.visibility = View.GONE
        } else {
            pictureBinding.refreshLayout.visibility = View.GONE
            pictureBinding.noData.visibility = View.VISIBLE
        }
    }

    override fun setViewBackToTop() {
        pictureBinding.rvVideo.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem =
                    (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                val lastVisibleItem =
                    (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItem < adapter.itemCount) {
                    pictureBinding.btnBackToTop.show()
                } else {
                    pictureBinding.btnBackToTop.hide()
                }
                if (firstVisibleItem == 0) {
                    pictureBinding.btnBackToTop.hide()
                }
            }
        })
        pictureBinding.btnBackToTop.setOnClickListener {
            pictureBinding.rvVideo.smoothScrollToPosition(0)
        }
    }

    override fun showPreviewImage(view: View, position: Int, data: MutableList<PicturesItem?>) {
        val intent = Intent(this, PreviewPictureActivity::class.java)
        intent.putExtra(Constant.position, position)
        intent.putExtra(Constant.dataPicture, Parcels.wrap(data))
        val p1 = Pair.create(view, ViewCompat.getTransitionName(view))
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1).toBundle()

        startActivity(intent, bundle)
    }

    override fun clearReenterState() {
        reenterState = null
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        reenterState = Bundle(data?.extras)
        reenterState?.let {
            val startingPosition = it.getInt(Constant.EXTRA_STARTING_ALBUM_POSITION)
            val currentPosition = it.getInt(Constant.EXTRA_CURRENT_ALBUM_POSITION)
            if (startingPosition != currentPosition) pictureBinding.rvVideo.scrollToPosition(
                currentPosition
            )
            ActivityCompat.postponeEnterTransition(this)

            pictureBinding.rvVideo.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    pictureBinding.rvVideo.viewTreeObserver.removeOnPreDrawListener(this)
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
        setSupportActionBar(pictureBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            pictureBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )
    }

    override fun setUI() {
        pictureBinding.btnBackToTop.hide()
        pictureBinding.btnBackToTop.setOnClickListener {
            pictureBinding.rvVideo.smoothScrollToPosition(
                0
            )
        }
        pictureBinding.refreshLayout.setOnRefreshListener {
            pictureBinding.refreshLayout.isRefreshing = false
        }
    }

    override fun showLoading() {
        pictureBinding.shimmerView.visibility = View.VISIBLE
        pictureBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        pictureBinding.shimmerView.startShimmer()
        pictureBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        pictureBinding.shimmerView.stopShimmer()
        pictureBinding.shimmerView.visibility = View.GONE
        pictureBinding.refreshLayout.visibility = View.VISIBLE
    }
}