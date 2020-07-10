package id.fajarproject.roommovie.ui.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerFragmentComponent
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.movieList.MovieListActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.shimmer_home.*
import javax.inject.Inject

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */


class MovieFragment : BaseFragment(),MovieContract.View {

    @Inject lateinit var presenter : MovieContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach<Fragment>(this,this)
        presenter.loadDataPopular()
        setUI()
    }

    override fun moveToActivity(status: String) {
        val intent = Intent(requireContext(),MovieListActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS,status)
        startActivity(intent)
    }

    override fun showDataPopularSuccess(list: MutableList<MovieItem?>) {
        viewPopular.visibility      = View.VISIBLE
        val layoutManager           = LinearLayoutManager(activity)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvPopular.layoutManager     = layoutManager
        val adapter                 = MovieAdapter(requireContext(),list)
        rvPopular.adapter           = adapter
        setItemClickListener(adapter)
    }

    override fun showDataPopularFailed(message: String) {
        Log.e("ErrorPopular",message)
        viewPopular.visibility      = View.GONE
    }

    override fun showDataNowPlayingSuccess(list: MutableList<MovieItem?>) {
        viewNowPlaying.visibility   = View.VISIBLE
        val layoutManager           = LinearLayoutManager(activity)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvNowPlaying.layoutManager  = layoutManager
        val adapter                 = MovieAdapter(requireContext(),list)
        rvNowPlaying.adapter        = adapter
        setItemClickListener(adapter)
    }

    override fun showDataNowPlayingFailed(message: String) {
        Log.e("ErrorNowPlaying",message)
        viewNowPlaying.visibility = View.GONE
    }

    override fun showDataTopRatedSuccess(list: MutableList<MovieItem?>) {
        viewTopRated.visibility   = View.VISIBLE
        val layoutManager         = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvTopRated.layoutManager  = layoutManager
        val adapter               = MovieAdapter(requireContext(),list)
        rvTopRated.adapter        = adapter
        setItemClickListener(adapter)
    }

    override fun showDataTopRatedFailed(message: String) {
        Log.e("ErrorTopRated",message)
        viewTopRated.visibility = View.GONE
    }

    override fun showDataUpcomingSuccess(list: MutableList<MovieItem?>) {
        viewUpcoming.visibility   = View.VISIBLE
        val layoutManager         = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvUpcoming.layoutManager  = layoutManager
        val adapter               = MovieAdapter(requireContext(),list)
        rvUpcoming.adapter        = adapter
        setItemClickListener(adapter)
    }

    override fun showDataUpcomingFailed(message: String) {
        Log.e("ErrorUpcoming",message)
        viewUpcoming.visibility = View.GONE
    }

    override fun showDataTrendingSuccess(list: MutableList<MovieItem?>) {
        viewTrending.visibility   = View.VISIBLE
        val layoutManager         = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvTrending.layoutManager  = layoutManager
        val adapter               = MovieAdapter(requireContext(),list)
        rvTrending.adapter        = adapter
        setItemClickListener(adapter)
    }

    override fun showDataTrendingFailed(message: String) {
        Log.e("ErrorTrending",message)
        viewTrending.visibility = View.GONE
    }

    override fun injectDependency() {
        val fragmentComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        fragmentComponent.inject(this)
    }

    override fun setToolbar() {
    }

    override fun setUI() {
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.loadDataPopular()
        }
        cvPopular.setOnClickListener {
            moveToActivity(getString(R.string.what_s_popular))
        }
        cvNowPlaying.setOnClickListener {
            moveToActivity(getString(R.string.now_playing))
        }
        cvTopRated.setOnClickListener {
            moveToActivity(getString(R.string.top_rated))
        }
        cvUpcoming.setOnClickListener {
            moveToActivity(getString(R.string.upcoming))
        }
        cvTrending.setOnClickListener {
            moveToActivity(getString(R.string.trending))
        }
        Util.changeVisibility(arrayOf(llAiring,llOnTheAir),View.GONE)
        Util.setViewPercents(requireContext(), arrayOf(viewShimmer1,viewShimmer2,viewShimmer3,viewShimmer4,viewShimmer5,viewShimmer6,viewShimmer7,viewShimmer8,viewShimmer9,viewShimmer10))
    }

    override fun showLoading() {
        scrollView.smoothScrollTo(0,0)
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

    override fun setItemClickListener(adapter: MovieAdapter){
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getMovie(position)
                item.id?.let {
                    moveToDetail(it)
                }
            }
        })
    }

    override fun moveToDetail(id : Int){
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra(Constant.idMovie,id)
        startActivity(intent)
    }
}