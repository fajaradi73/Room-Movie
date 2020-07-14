package id.fajarproject.roommovie.ui.search

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SearchActivity : BaseActivity(),SearchContract.View {

    @Inject lateinit var presenter : SearchContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    private var adapterMovie: SearchAdapter? = null
    private var adapterPeople : PeopleAdapter? = null
    var isLoading = false
    var isLastPage = false
    var isPeople = false

    private var countData = 0
    var currentPage = 1
    var limit = 20

    private var status : String = ""
    private var query : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        injectDependency()
        presenter.attach(this,this)

        status  = intent.getStringExtra(Constant.INTENT_STATUS) ?: ""
        query   = intent.getStringExtra(Constant.voiceSearch) ?: ""

        if (status.contains(getString(R.string.people))){
            isPeople = true
        }
        setToolbar()
        setRecycleView()
        setUI()
        searchBar.setSearchHint("${getString(R.string.search_hint)} ${status.toLowerCase(Locale.getDefault())}")

        if (isConnection)
            if (query.isNotEmpty()){
                presenter.checkData(currentPage,query,status)
                searchBar.setSearchText(query)
            }else{
                searchBar.setSearchFocused(true)
            }
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(this)
        layoutManager = GridLayoutManager(this,mNoOfColumns)
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
        rvSearch.layoutManager = layoutManager
    }

    override fun checkAdapter(isPeople: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>{
        return if (isPeople){
            adapterPeople as RecyclerView.Adapter<RecyclerView.ViewHolder>
        }else{
            adapterMovie as RecyclerView.Adapter<RecyclerView.ViewHolder>
        }
    }

    override fun setScrollRecycleView() {
        rvSearch.addOnScrollListener(object : PaginationScrollListener(layoutManager){
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter.checkData(currentPage,query,status)
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
                    btnBackToTop.show()
                else
                    btnBackToTop.hide()
            }

        })
    }

    override fun showDataMovieSuccess(list: MutableList<MovieItem?>,isMovie : Boolean) {
        countData = list.size
        if (currentPage == 1){
            adapterMovie = SearchAdapter(
                this,list,isMovie
            )
            rvSearch.adapter = adapterMovie
            setScrollRecycleView()
        }else{
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
                    moveToDetail(it,isMovie)
                }
            }
        })
        checkLastData()
        checkData()
    }

    override fun moveToDetail(id : Int,isMovie: Boolean){
        val intent = if (isMovie)
            Intent(this, MovieDetailActivity::class.java)
        else
            Intent(this, TvDetailActivity::class.java)

        intent.putExtra(Constant.idMovie,id)
        startActivity(intent)
    }

    override fun showDataMovieFailed(message: String) {
        Log.d("ErrorSearch",message)
        showData(false)
    }

    override fun showDataPeopleSuccess(list: MutableList<PeopleItem?>) {
        countData = list.size
        if (currentPage == 1){
            adapterPeople = PeopleAdapter(
                this,list
            )
            rvSearch.adapter = adapterPeople
            setScrollRecycleView()
        }else{
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
                    val intent = Intent(activity, PeopleDetailActivity::class.java)
                    intent.putExtra(Constant.idPeople,it)
                    startActivity(intent)
                }
            }
        })
        checkLastData()
        checkData()
    }

    override fun showDataPeopleFailed(message: String) {
        Log.d("ErrorFavorite",message)
        showData(false)
    }

    override fun checkLastData() {
        if (countData == limit){
            if (isPeople){
                adapterPeople?.addLoadingFooter()
            }else{
                adapterMovie?.addLoadingFooter()
            }
        }else{
            isLastPage = true
        }
    }

    override fun checkData() {
        if (checkAdapter(isPeople).itemCount == 0){
            showData(false)
        }else{
            showData(true)
        }
    }

    override fun showData(isShow: Boolean) {
        if (isShow){
            rvSearch.visibility  = View.VISIBLE
            noData.visibility   = View.GONE
        }else{
            rvSearch.visibility  = View.GONE
            noData.visibility   = View.VISIBLE
        }
    }

    override fun search(search: String) {
        query = search
        currentPage = 1
        presenter.checkData(currentPage,search,status)
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
        btnBackToTop.hide()
        btnBackToTop.setOnClickListener { rvSearch.smoothScrollToPosition(0) }
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            search(query)
        }
        searchBar.setOnSearchListener(object : FloatingSearchView.OnSearchListener{
            override fun onSearchAction(currentQuery: String?) {
                search(currentQuery ?: "")
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
            }

        })
        searchBar.setOnHomeActionClickListener { onBackPressed() }
        searchBar.setOnMenuItemClickListener { item ->
            if (item?.itemId == R.id.action_voice){
                Util.onVoiceClicked(this)
            }
        }
    }

    override fun showLoading() {
        if (isPeople){
            shimmerViewPeople.visibility  = View.VISIBLE
            shimmerViewPeople.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
            shimmerViewPeople.startShimmer()
        }else{
            shimmerView.visibility  = View.VISIBLE
            shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
            shimmerView.startShimmer()
        }
        refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        if (isPeople){
            shimmerViewPeople.stopShimmer()
            shimmerViewPeople.visibility      = View.GONE
        }else{
            shimmerView.stopShimmer()
            shimmerView.visibility      = View.GONE
        }
        refreshLayout.visibility    = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches: ArrayList<String> =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) ?: arrayListOf()
            if (matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchBar.setSearchText(searchWrd)
                    search(searchWrd)
                }
            }
            return
        }
    }
}