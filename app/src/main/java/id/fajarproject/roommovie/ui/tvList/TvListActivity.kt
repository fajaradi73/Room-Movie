package id.fajarproject.roommovie.ui.tvList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityTvListBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.PaginationScrollListener
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject

class TvListActivity : BaseActivity(), TvListContract.View {

    @Inject
    lateinit var presenter: TvListContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    lateinit var adapter: TvListAdapter
    private lateinit var tvListBinding: ActivityTvListBinding

    var isLoading = false
    var isLastPage = false

    private var countData = 0
    var currentPage = 1
    var limit = 20

    private var status: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvListBinding = ActivityTvListBinding.inflate(layoutInflater)
        setContentView(tvListBinding.root)
        injectDependency()
        presenter.attach<Activity>(this, this)

        status = intent.getStringExtra(Constant.INTENT_STATUS) ?: ""

        setToolbar()
        setRecycleView()
        setUI()
        if (isConnection)
            presenter.loadData(currentPage, status)

    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(this)
        layoutManager = GridLayoutManager(this, mNoOfColumns)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    adapter.getItemViewType(position) == Constant.VIEW_TYPE_LOADING -> {
                        mNoOfColumns
                    }
                    adapter.getItemViewType(position) == Constant.VIEW_TYPE_ITEM -> {
                        1
                    }
                    else -> {
                        mNoOfColumns
                    }
                }
            }
        }
        tvListBinding.rvTV.layoutManager = layoutManager
    }

    override fun setScrollRecycleView() {
        tvListBinding.rvTV.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter.loadData(currentPage, status)
            }

            override fun getTotalPageCount(): Int {
                return limit
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun backToTop(isShow: Boolean) {
                if (isShow)
                    tvListBinding.btnBackToTop.show()
                else
                    tvListBinding.btnBackToTop.hide()
            }

        })
    }

    override fun showDataSuccess(list: MutableList<MovieItem?>) {
        countData = list.size
        if (currentPage == 1) {
            adapter = TvListAdapter(
                this, list
            )
            tvListBinding.rvTV.adapter = adapter
            setScrollRecycleView()
        } else {
            adapter.removeLoadingFooter()
            isLoading = false
            adapter.addData(list)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getMovie(position)
                item?.id?.let {
                    moveToDetail(it)
                }
            }
        })
        checkLastData()
        checkData()
    }

    override fun moveToDetail(id: Int) {
        val intent = Intent(this, TvDetailActivity::class.java)
        intent.putExtra(Constant.idMovie, id)
        startActivity(intent)
    }

    override fun showDataFailed(message: String) {
        Log.d("ErrorTvList", message)
        showData(false)
    }

    override fun checkLastData() {
        if (countData == limit) {
            adapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    override fun checkData() {
        if (adapter.itemCount == 0) {
            showData(false)
        } else {
            showData(true)
        }
    }

    override fun showData(isShow: Boolean) {
        if (isShow) {
            tvListBinding.rvTV.visibility = View.VISIBLE
            tvListBinding.noData.visibility = View.GONE
        } else {
            tvListBinding.rvTV.visibility = View.GONE
            tvListBinding.noData.visibility = View.VISIBLE
        }
    }

    override fun getTitle(title: String): String {
        return when (title) {
            getString(R.string.tv_airing_today) -> {
                getString(R.string.tv_airing_today)
            }
            getString(R.string.tv_on_the_air) -> {
                getString(R.string.tv_on_the_air)
            }
            getString(R.string.top_rated) -> {
                getString(R.string.top_rated)
            }
            else -> {
                getString(R.string.what_s_popular)
            }
        }
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(tvListBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            tvListBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )
        title = getTitle(status)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }


    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun setUI() {
        tvListBinding.btnBackToTop.hide()
        tvListBinding.btnBackToTop.setOnClickListener { tvListBinding.rvTV.smoothScrollToPosition(0) }
        tvListBinding.refreshLayout.setOnRefreshListener {
            tvListBinding.refreshLayout.isRefreshing = false
            currentPage = 1
            presenter.loadData(currentPage, status)
        }
    }

    override fun showLoading() {
        tvListBinding.shimmerView.visibility = View.VISIBLE
        tvListBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        tvListBinding.shimmerView.startShimmer()
        tvListBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        tvListBinding.shimmerView.stopShimmer()
        tvListBinding.shimmerView.visibility = View.GONE
        tvListBinding.refreshLayout.visibility = View.VISIBLE
    }

}