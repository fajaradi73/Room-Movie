package id.fajarproject.roommovie.ui.movieList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityMovieListBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.PaginationScrollListener
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MovieListActivity : BaseActivity(), MovieListContract.View {

    @Inject
    lateinit var presenter: MovieListContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    var adapter: MovieListAdapter? = null
    var isLoading = false
    var isLastPage = false

    private var countData = 0
    var currentPage = 1
    var limit = 20

    private var status: String = ""
    private lateinit var movieListBinding: ActivityMovieListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieListBinding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(movieListBinding.root)
        injectDependency()
        presenter.attach<Activity>(this, this)

        status = intent.getStringExtra(Constant.INTENT_STATUS) ?: ""

        setToolbar()
        setRecycleView()
        setUI()
        if (isConnection) {
            presenter.loadData(currentPage, status)
        }
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(this)
        layoutManager = GridLayoutManager(this, mNoOfColumns)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    adapter?.getItemViewType(position) == Constant.VIEW_TYPE_LOADING -> {
                        mNoOfColumns
                    }
                    adapter?.getItemViewType(position) == Constant.VIEW_TYPE_ITEM -> {
                        1
                    }
                    else -> {
                        mNoOfColumns
                    }
                }
            }
        }
        movieListBinding.rvMovie.layoutManager = layoutManager
    }

    override fun setScrollRecycleView() {
        movieListBinding.rvMovie.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
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
                    movieListBinding.btnBackToTop.show()
                else
                    movieListBinding.btnBackToTop.hide()
            }

        })
    }

    override fun showDataSuccess(list: MutableList<MovieItem?>) {
        countData = list.size
        if (currentPage == 1) {
            adapter = MovieListAdapter(
                this, list
            )
            movieListBinding.rvMovie.adapter = adapter
            setScrollRecycleView()
        } else {
            adapter?.removeLoadingFooter()
            isLoading = false
            adapter?.addData(list)
            adapter?.notifyDataSetChanged()
        }

        adapter?.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter?.getMovie(position)
                item?.id?.let {
                    moveToDetail(it)
                }
            }
        })
        checkLastData()
        checkData()
    }

    fun moveToDetail(id: Int) {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra(Constant.idMovie, id)
        startActivity(intent)
    }

    override fun showDataFailed(message: String) {
        Log.d("ErrorFavorite", message)
        showData(false)
    }

    override fun checkLastData() {
        if (countData == limit) {
            adapter?.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    override fun checkData() {
        if (adapter?.itemCount == 0) {
            showData(false)
        } else {
            showData(true)
        }
    }

    override fun showData(isShow: Boolean) {
        if (isShow) {
            movieListBinding.rvMovie.visibility = View.VISIBLE
            movieListBinding.noData.visibility = View.GONE
        } else {
            movieListBinding.rvMovie.visibility = View.GONE
            movieListBinding.noData.visibility = View.VISIBLE
        }
    }

    override fun getTitle(title: String): String {
        return when (title) {
            getString(R.string.now_playing) -> {
                getString(R.string.now_playing)
            }
            getString(R.string.top_rated) -> {
                getString(R.string.top_rated)
            }
            getString(R.string.upcoming) -> {
                getString(R.string.upcoming)
            }
            getString(R.string.trending) -> {
                getString(R.string.trending)
            }
            else -> {
                getString(R.string.what_s_popular)
            }
        }
    }

    override fun setToolbar() {
        setSupportActionBar(movieListBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            movieListBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )
        title = getTitle(status)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun setUI() {
        movieListBinding.btnBackToTop.hide()
        movieListBinding.btnBackToTop.setOnClickListener {
            movieListBinding.rvMovie.smoothScrollToPosition(
                0
            )
        }
        movieListBinding.refreshLayout.setOnRefreshListener {
            movieListBinding.refreshLayout.isRefreshing = false
            currentPage = 1
            presenter.loadData(currentPage, status)
        }
    }

    override fun showLoading() {
        movieListBinding.shimmerView.visibility = View.VISIBLE
        movieListBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        movieListBinding.shimmerView.startShimmer()
        movieListBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        movieListBinding.shimmerView.stopShimmer()
        movieListBinding.shimmerView.visibility = View.GONE
        movieListBinding.refreshLayout.visibility = View.VISIBLE
    }

}