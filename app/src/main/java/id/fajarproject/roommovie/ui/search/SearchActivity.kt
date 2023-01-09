package id.fajarproject.roommovie.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivitySearchBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.people.PeopleAdapter
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailActivity
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.PaginationScrollListener
import id.fajarproject.roommovie.util.Util
import java.util.*
import javax.inject.Inject

class SearchActivity : BaseActivity(), SearchContract.View {

    @Inject
    lateinit var presenter: SearchContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    private var adapterMovie: SearchAdapter? = null
    private var adapterPeople: PeopleAdapter? = null
    var isLoading = false
    var isLastPage = false
    var isPeople = false

    private var countData = 0
    var currentPage = 1
    var limit = 20

    private var status: String = ""
    private var query: String = ""
    private lateinit var searchBinding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)
        injectDependency()
        presenter.attach(this, this)

        status = intent.getStringExtra(Constant.INTENT_STATUS) ?: ""
        query = intent.getStringExtra(Constant.voiceSearch) ?: ""

        if (status.contains(getString(R.string.people))) {
            isPeople = true
        }
        setToolbar()
        setRecycleView()
        setUI()
        searchBinding.searchBar.setSearchHint(
            "${getString(R.string.search_hint)} ${
                status.toLowerCase(
                    Locale.getDefault()
                )
            }"
        )

        if (isConnection)
            if (query.isNotEmpty()) {
                presenter.checkData(currentPage, query, status)
                searchBinding.searchBar.setSearchText(query)
            } else {
                searchBinding.searchBar.setSearchFocused(true)
            }
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(this)
        layoutManager = GridLayoutManager(this, mNoOfColumns)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    checkAdapter(isPeople).getItemViewType(position) == Constant.VIEW_TYPE_LOADING -> {
                        mNoOfColumns
                    }
                    checkAdapter(isPeople).getItemViewType(position) == Constant.VIEW_TYPE_ITEM -> {
                        1
                    }
                    else -> {
                        mNoOfColumns
                    }
                }
            }
        }
        searchBinding.rvSearch.layoutManager = layoutManager
    }

    override fun checkAdapter(isPeople: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return if (isPeople) {
            adapterPeople as RecyclerView.Adapter<RecyclerView.ViewHolder>
        } else {
            adapterMovie as RecyclerView.Adapter<RecyclerView.ViewHolder>
        }
    }

    override fun setScrollRecycleView() {
        searchBinding.rvSearch.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter.checkData(currentPage, query, status)
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
                    searchBinding.btnBackToTop.show()
                else
                    searchBinding.btnBackToTop.hide()
            }

        })
    }

    override fun showDataMovieSuccess(list: MutableList<MovieItem?>, isMovie: Boolean) {
        countData = list.size
        if (currentPage == 1) {
            adapterMovie = SearchAdapter(
                this, list, isMovie
            )
            searchBinding.rvSearch.adapter = adapterMovie
            setScrollRecycleView()
        } else {
            adapterMovie?.removeLoadingFooter()
            isLoading = false
            adapterMovie?.addData(list)
            adapterMovie?.notifyDataSetChanged()
        }

        adapterMovie?.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapterMovie?.getMovie(position)
                item?.id?.let {
                    moveToDetail(it, isMovie)
                }
            }
        })
        checkLastData()
        checkData()
    }

    override fun moveToDetail(id: Int, isMovie: Boolean) {
        val intent = if (isMovie)
            Intent(this, MovieDetailActivity::class.java)
        else
            Intent(this, TvDetailActivity::class.java)

        intent.putExtra(Constant.idMovie, id)
        startActivity(intent)
    }

    override fun showDataMovieFailed(message: String) {
        Log.d("ErrorSearch", message)
        showData(false)
    }

    override fun showDataPeopleSuccess(list: MutableList<PeopleItem?>) {
        countData = list.size
        if (currentPage == 1) {
            adapterPeople = PeopleAdapter(
                this, list
            )
            searchBinding.rvSearch.adapter = adapterPeople
            setScrollRecycleView()
        } else {
            adapterPeople?.removeLoadingFooter()
            isLoading = false
            adapterPeople?.addData(list)
            adapterPeople?.notifyDataSetChanged()
        }

        adapterPeople?.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapterPeople?.getItem(position)
                item?.id.let {
                    val intent = Intent(this@SearchActivity, PeopleDetailActivity::class.java)
                    intent.putExtra(Constant.idPeople, it)
                    startActivity(intent)
                }
            }
        })
        checkLastData()
        checkData()
    }

    override fun showDataPeopleFailed(message: String) {
        Log.d("ErrorFavorite", message)
        showData(false)
    }

    override fun checkLastData() {
        if (countData == limit) {
            if (isPeople) {
                adapterPeople?.addLoadingFooter()
            } else {
                adapterMovie?.addLoadingFooter()
            }
        } else {
            isLastPage = true
        }
    }

    override fun checkData() {
        if (checkAdapter(isPeople).itemCount == 0) {
            showData(false)
        } else {
            showData(true)
        }
    }

    override fun showData(isShow: Boolean) {
        if (isShow) {
            searchBinding.rvSearch.visibility = View.VISIBLE
            searchBinding.noData.visibility = View.GONE
        } else {
            searchBinding.rvSearch.visibility = View.GONE
            searchBinding.noData.visibility = View.VISIBLE
        }
    }

    override fun search(search: String) {
        query = search
        currentPage = 1
        presenter.checkData(currentPage, search, status)
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
    }

    override fun setUI() {
        searchBinding.btnBackToTop.hide()
        searchBinding.btnBackToTop.setOnClickListener {
            searchBinding.rvSearch.smoothScrollToPosition(
                0
            )
        }
        searchBinding.refreshLayout.setOnRefreshListener {
            searchBinding.refreshLayout.isRefreshing = false
            search(query)
        }
        searchBinding.searchBar.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSearchAction(currentQuery: String?) {
                search(currentQuery ?: "")
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
            }

        })
        searchBinding.searchBar.setOnHomeActionClickListener { onBackPressed() }
        searchBinding.searchBar.setOnMenuItemClickListener { item ->
            if (item?.itemId == R.id.action_voice) {
                Util.onVoiceClicked(this)
            }
        }
    }

    override fun showLoading() {
        if (isPeople) {
            searchBinding.shimmerViewPeople.visibility = View.VISIBLE
            searchBinding.shimmerViewPeople.setShimmer(
                Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
            )
            searchBinding.shimmerViewPeople.startShimmer()
        } else {
            searchBinding.shimmerView.visibility = View.VISIBLE
            searchBinding.shimmerView.setShimmer(
                Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
            )
            searchBinding.shimmerView.startShimmer()
        }
        searchBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        if (isPeople) {
            searchBinding.shimmerViewPeople.stopShimmer()
            searchBinding.shimmerViewPeople.visibility = View.GONE
        } else {
            searchBinding.shimmerView.stopShimmer()
            searchBinding.shimmerView.visibility = View.GONE
        }
        searchBinding.refreshLayout.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches: ArrayList<String> =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) ?: arrayListOf()
            if (matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchBinding.searchBar.setSearchText(searchWrd)
                    search(searchWrd)
                }
            }
            return
        }
    }
}