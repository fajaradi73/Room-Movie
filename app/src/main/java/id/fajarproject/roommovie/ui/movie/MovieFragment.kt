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
import id.fajarproject.roommovie.databinding.FragmentMovieBinding
import id.fajarproject.roommovie.databinding.ShimmerHomeBinding
import id.fajarproject.roommovie.di.component.DaggerFragmentComponent
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.movieList.MovieListActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */


class MovieFragment : BaseFragment(), MovieContract.View {

    @Inject
    lateinit var presenter: MovieContract.Presenter

    private lateinit var movieBinding: FragmentMovieBinding
    private lateinit var shimmerHomeBinding: ShimmerHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        movieBinding = FragmentMovieBinding.inflate(inflater, container, false)
        shimmerHomeBinding = ShimmerHomeBinding.inflate(inflater, container, false)
        return movieBinding.root
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach<Fragment>(this, this)
        presenter.loadDataPopular()
        setUI()
    }

    override fun moveToActivity(status: String) {
        val intent = Intent(requireContext(), MovieListActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS, status)
        startActivity(intent)
    }

    override fun showDataPopularSuccess(list: MutableList<MovieItem?>) {
        movieBinding.viewPopular.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieBinding.rvPopular.layoutManager = layoutManager
        val adapter = MovieAdapter(requireContext(), list)
        movieBinding.rvPopular.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataPopularFailed(message: String) {
        Log.e("ErrorPopular", message)
        movieBinding.viewPopular.visibility = View.GONE
    }

    override fun showDataNowPlayingSuccess(list: MutableList<MovieItem?>) {
        movieBinding.viewNowPlaying.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieBinding.rvNowPlaying.layoutManager = layoutManager
        val adapter = MovieAdapter(requireContext(), list)
        movieBinding.rvNowPlaying.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataNowPlayingFailed(message: String) {
        Log.e("ErrorNowPlaying", message)
        movieBinding.viewNowPlaying.visibility = View.GONE
    }

    override fun showDataTopRatedSuccess(list: MutableList<MovieItem?>) {
        movieBinding.viewTopRated.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieBinding.rvTopRated.layoutManager = layoutManager
        val adapter = MovieAdapter(requireContext(), list)
        movieBinding.rvTopRated.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataTopRatedFailed(message: String) {
        Log.e("ErrorTopRated", message)
        movieBinding.viewTopRated.visibility = View.GONE
    }

    override fun showDataUpcomingSuccess(list: MutableList<MovieItem?>) {
        movieBinding.viewUpcoming.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieBinding.rvUpcoming.layoutManager = layoutManager
        val adapter = MovieAdapter(requireContext(), list)
        movieBinding.rvUpcoming.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataUpcomingFailed(message: String) {
        Log.e("ErrorUpcoming", message)
        movieBinding.viewUpcoming.visibility = View.GONE
    }

    override fun showDataTrendingSuccess(list: MutableList<MovieItem?>) {
        movieBinding.viewTrending.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieBinding.rvTrending.layoutManager = layoutManager
        val adapter = MovieAdapter(requireContext(), list)
        movieBinding.rvTrending.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataTrendingFailed(message: String) {
        Log.e("ErrorTrending", message)
        movieBinding.viewTrending.visibility = View.GONE
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
        movieBinding.refreshLayout.isRefreshing = false
        movieBinding.refreshLayout.setOnRefreshListener {
            movieBinding.refreshLayout.isRefreshing = false
            presenter.loadDataPopular()
        }
        movieBinding.cvPopular.setOnClickListener {
            moveToActivity(getString(R.string.what_s_popular))
        }
        movieBinding.cvNowPlaying.setOnClickListener {
            moveToActivity(getString(R.string.now_playing))
        }
        movieBinding.cvTopRated.setOnClickListener {
            moveToActivity(getString(R.string.top_rated))
        }
        movieBinding.cvUpcoming.setOnClickListener {
            moveToActivity(getString(R.string.upcoming))
        }
        movieBinding.cvTrending.setOnClickListener {
            moveToActivity(getString(R.string.trending))
        }
        Util.changeVisibility(
            arrayOf(shimmerHomeBinding.llAiring, shimmerHomeBinding.llOnTheAir),
            View.GONE
        )
        Util.setViewPercents(
            requireContext(),
            arrayOf(
                shimmerHomeBinding.viewShimmer1,
                shimmerHomeBinding.viewShimmer2,
                shimmerHomeBinding.viewShimmer3,
                shimmerHomeBinding.viewShimmer4,
                shimmerHomeBinding.viewShimmer5,
                shimmerHomeBinding.viewShimmer6,
                shimmerHomeBinding.viewShimmer7,
                shimmerHomeBinding.viewShimmer8,
                shimmerHomeBinding.viewShimmer9,
                shimmerHomeBinding.viewShimmer10
            )
        )
    }

    override fun showLoading() {
        shimmerHomeBinding.scrollView.smoothScrollTo(0, 0)
        movieBinding.shimmerView.visibility = View.VISIBLE
        movieBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        movieBinding.shimmerView.startShimmer()
        movieBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        movieBinding.shimmerView.stopShimmer()
        movieBinding.shimmerView.visibility = View.GONE
        movieBinding.refreshLayout.visibility = View.VISIBLE
    }

    override fun setItemClickListener(adapter: MovieAdapter) {
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getMovie(position)
                item.id?.let {
                    moveToDetail(it)
                }
            }
        })
    }

    override fun moveToDetail(id: Int) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra(Constant.idMovie, id)
        startActivity(intent)
    }
}