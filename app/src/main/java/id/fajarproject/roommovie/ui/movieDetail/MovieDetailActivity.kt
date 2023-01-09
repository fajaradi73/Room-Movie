package id.fajarproject.roommovie.ui.movieDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.*
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.*
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.credits.CreditsActivity
import id.fajarproject.roommovie.ui.detailAdapter.*
import id.fajarproject.roommovie.ui.discover.DiscoverActivity
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailActivity
import id.fajarproject.roommovie.ui.picture.PictureActivity
import id.fajarproject.roommovie.ui.previewPicture.PreviewPictureActivity
import id.fajarproject.roommovie.ui.video.VideoListActivity
import id.fajarproject.roommovie.ui.video.VideoPlayerActivity
import id.fajarproject.roommovie.ui.widget.AppBarStateChangeListener
import id.fajarproject.roommovie.ui.widget.DialogListener
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.ui.widget.PatternEditableBuilder
import id.fajarproject.roommovie.ui.widget.PatternEditableBuilder.SpannableClickedListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import org.parceler.Parcels
import java.util.regex.Pattern
import javax.inject.Inject


class MovieDetailActivity : BaseActivity(), MovieDetailContract.View {

    @Inject
    lateinit var presenter: MovieDetailContract.Presenter
    private var id = 0
    private var reenterState: Bundle? = null
    private lateinit var movieDetailBinding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieDetailBinding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(movieDetailBinding.root)
        injectDependency()
        presenter.attach(this, this)
        setToolbar()
        setUI()

        id = intent.getIntExtra(Constant.idMovie, 0)

        if (id == 0) {
            showDialogNoData("")
            return
        }
        if (isConnection) {
            presenter.loadData(id)
        }
        ActivityCompat.setExitSharedElementCallback(this, exitElementCallback)

    }


    @SuppressLint("SetTextI18n")
    override fun showDataSuccess(data: MovieItem) {
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.posterPath)
            .placeholder(Util.circleLoading(this))
            .into(movieDetailBinding.ivMovie)
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.backdropPath)
            .placeholder(Util.circleLoading(this))
            .into(movieDetailBinding.ivBackground)
        setOnSetChange(data.title ?: "")

        movieDetailBinding.movieDetailOverview.tvTagLine.text = data.tagline
        movieDetailBinding.movieDetailOverview.tvRatting.text =
            "${data.voteAverage.toString()}  •  ${data.releaseDate} \u2022 ${
                data.runtime?.div(
                    60
                )
            }h${data.runtime?.rem(60)}m "

        movieDetailBinding.movieDetailOverview.tvOverview.text = data.overview
        movieDetailBinding.movieDetailInfo.tvStatus.text = data.status
        movieDetailBinding.movieDetailInfo.tvLanguage.text =
            presenter.getLanguage(data.originalLanguage, data.spokenLanguages)
        movieDetailBinding.movieDetailInfo.tvBudget.text = "$ ${Util.currencyFormatter(data.budget.toString())}"
        movieDetailBinding.movieDetailInfo.tvRevenue.text = "$ ${Util.currencyFormatter(data.revenue.toString())}"
        movieDetailBinding.movieDetailOverview.tvGenre.text = presenter.getGenre(data.genres)
        setClickableSpan(movieDetailBinding.movieDetailOverview.tvGenre, data.genres)

        data.external_ids?.let {
            setViewExternalIDs(it)
        }
        data.keywords?.keywords?.let {
            setViewKeyword(it)
        }
        data.credits?.cast?.let {
            if (it.size > 0) {
                movieDetailBinding.movieDetailPeople.tvFullCast.visibility = View.VISIBLE
                setViewCasts(it)
                movieDetailBinding.movieDetailPeople.tvFullCast.setOnClickListener {
                    val intent = Intent(this, CreditsActivity::class.java)
                    intent.putExtra(Constant.isMovie, true)
                    intent.putExtra(Constant.INTENT_STATUS, data.title)
                    intent.putExtra(Constant.idMovie, data.id)
                    startActivity(intent)
                }
            } else {
                movieDetailBinding.movieDetailPeople.tvFullCast.visibility = View.GONE
            }
        }
        data.videos?.results?.let {
            if (it.size > 0) {
                movieDetailBinding.movieDetailMedia.allVideo.visibility = View.VISIBLE
                setViewVideo(it)
                movieDetailBinding.movieDetailMedia.allVideo.setOnClickListener {
                    val intent = Intent(this, VideoListActivity::class.java)
                    intent.putExtra(Constant.title, data.title)
                    intent.putExtra(Constant.idMovie, data.id)
                    startActivity(intent)
                }
            } else {
                movieDetailBinding.movieDetailMedia.allVideo.visibility = View.GONE
            }
        }
        data.images?.backdrops?.let { list: MutableList<PicturesItem?> ->
            if (list.size > 0) {
                movieDetailBinding.movieDetailMedia.allBackdrops.visibility = View.VISIBLE
                setViewBackdrops(list)
                movieDetailBinding.movieDetailMedia.allBackdrops.setOnClickListener {
                    moveToPicture(data.title ?: "", list)
                }
            } else {
                movieDetailBinding.movieDetailMedia.allBackdrops.visibility = View.GONE
            }
        }
        data.images?.posters?.let { list: MutableList<PicturesItem?> ->
            if (list.size > 0) {
                movieDetailBinding.movieDetailMedia.allPosters.visibility = View.VISIBLE
                setViewPosters(list)
                movieDetailBinding.movieDetailMedia.allPosters.setOnClickListener {
                    moveToPicture(data.title ?: "", list)
                }
            } else {
                movieDetailBinding.movieDetailMedia.allPosters.visibility = View.GONE
            }
        }

        data.recommendations?.movieList?.let {
            setViewRecommendation(it)
        }
        movieDetailBinding.movieDetailInfo.link.setOnClickListener {
            data.homepage?.let {
                if (it.isNotEmpty()) {
                    setOpenURL("$it/", "homepage")
                } else {
                    setOpenURL(Constant.BASE_THE_MOVIE_DB, "homepage")
                }
            } ?: kotlin.run {
                setOpenURL(Constant.BASE_THE_MOVIE_DB, "homepage")
            }
        }
    }

    override fun setViewKeyword(list: MutableList<KeywordsItem?>) {
        for (i in list.indices) {
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(6, 6, 6, 6)
            configureChip(list[i], params, i)
        }
    }

    override fun configureChip(item: KeywordsItem?, params: RelativeLayout.LayoutParams, i: Int) {
        val chip = Chip(this, null, R.style.CustomChipChoice)
        chip.layoutParams = params
        chip.text = item?.name
        chip.setTextAppearanceResource(R.style.ChipText)
        chip.isClickable = true
        chip.checkedIcon = null
        chip.tag = item?.name
        chip.setOnClickListener {
            item?.id?.let {
                moveToDiscover(item.name ?: "", "", it.toString())
            }
        }
        movieDetailBinding.movieDetailInfo.cgKeyword.addView(chip, i, params)
    }

    override fun setViewCasts(list: MutableList<CreditsItem?>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieDetailBinding.movieDetailPeople.rvCast.layoutManager = layoutManager
        val adapter =
            DetailCastAdapter(
                this,
                list
            )
        movieDetailBinding.movieDetailPeople.rvCast.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id.let {
                    val intent = Intent(this@MovieDetailActivity, PeopleDetailActivity::class.java)
                    intent.putExtra(Constant.idPeople, it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setViewVideo(list: MutableList<VideosItem?>) {
        if (list.size > 0) {
            movieDetailBinding.movieDetailMedia.allVideo.visibility = View.VISIBLE
            if (movieDetailBinding.movieDetailMedia.video.text.contains(" (${list.size})")) {
                movieDetailBinding.movieDetailMedia.video.text = getString(R.string.video)
            }
            Util.setSpannable(
                movieDetailBinding.movieDetailMedia.video,
                " (${list.size})",
                ContextCompat.getColor(this, R.color.textColorSecondary)
            )
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            movieDetailBinding.movieDetailMedia.rvVideo.layoutManager = layoutManager
            val adapter =
                DetailVideoAdapter(
                    this,
                    list
                )
            movieDetailBinding.movieDetailMedia.rvVideo.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val item = adapter.getItem(position)
                    item?.key?.let {
                        val intent =
                            Intent(this@MovieDetailActivity, VideoPlayerActivity::class.java)
                        intent.putExtra(Constant.keyVideo, it)
                        intent.putExtra(Constant.title, item.name)
                        startActivity(intent)
                    }
                }
            })
        } else {
            movieDetailBinding.movieDetailMedia.allVideo.visibility = View.GONE
        }
    }

    override fun setViewBackdrops(list: MutableList<PicturesItem?>) {
        if (list.size > 0) {
            movieDetailBinding.movieDetailMedia.allBackdrops.visibility = View.VISIBLE
            if (movieDetailBinding.movieDetailMedia.backdrops.text.contains(" (${list.size})")) {
                movieDetailBinding.movieDetailMedia.backdrops.text = getString(R.string.backdrops)
            }
            Util.setSpannable(
                movieDetailBinding.movieDetailMedia.backdrops,
                " (${list.size})",
                ContextCompat.getColor(this, R.color.textColorSecondary)
            )
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            movieDetailBinding.movieDetailMedia.rvBackdrops.layoutManager = layoutManager
            val adapter =
                DetailBackdropsAdapter(
                    this,
                    list
                )
            movieDetailBinding.movieDetailMedia.rvBackdrops.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    view?.let {
                        showPreviewImage(it, position, list, true)
                    }
                }
            })
        } else {
            movieDetailBinding.movieDetailMedia.allBackdrops.visibility = View.GONE
        }
    }

    override fun setViewPosters(list: MutableList<PicturesItem?>) {
        if (list.size > 0) {
            movieDetailBinding.movieDetailMedia.allPosters.visibility = View.VISIBLE
            if (movieDetailBinding.movieDetailMedia.posters.text.contains(" (${list.size})")) {
                movieDetailBinding.movieDetailMedia.posters.text = getString(R.string.posters)
            }
            Util.setSpannable(
                movieDetailBinding.movieDetailMedia.posters,
                " (${list.size})",
                ContextCompat.getColor(this, R.color.textColorSecondary)
            )
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            movieDetailBinding.movieDetailMedia.rvPosters.layoutManager = layoutManager
            val adapter =
                DetailPostersAdapter(
                    this,
                    list
                )
            movieDetailBinding.movieDetailMedia.rvPosters.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    view?.let {
                        showPreviewImage(it, position, list, false)
                    }
                }
            })
        } else {
            movieDetailBinding.movieDetailMedia.allPosters.visibility = View.GONE
        }
    }

    override fun setViewRecommendation(list: MutableList<MovieItem?>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        movieDetailBinding.movieDetailRecommendation.rvRecommendation.layoutManager = layoutManager
        val adapter =
            DetailRecommendationsAdapter(
                this,
                list
            )
        movieDetailBinding.movieDetailRecommendation.rvRecommendation.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id?.let {
                    val intent = Intent(this@MovieDetailActivity, MovieDetailActivity::class.java)
                    intent.putExtra(Constant.idMovie, it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun showPreviewImage(
        view: View,
        position: Int,
        data: MutableList<PicturesItem?>,
        isBackdrops: Boolean
    ) {
        val intent = Intent(this, PreviewPictureActivity::class.java)
        intent.putExtra(Constant.position, position)
        intent.putExtra(Constant.dataPicture, Parcels.wrap(data))
        intent.putExtra(Constant.isDetail, true)
        intent.putExtra(Constant.isBackdrops, isBackdrops)
        val p1 = Pair.create(view, ViewCompat.getTransitionName(view))
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1).toBundle()

        startActivity(intent, bundle)
    }

    override fun moveToPicture(title: String, list: MutableList<PicturesItem?>) {
        val intent = Intent(this, PictureActivity::class.java)
        intent.putExtra(Constant.title, title)
        intent.putExtra(Constant.dataPicture, Parcels.wrap(list))
        startActivity(intent)
    }

    override fun moveToDiscover(status: String, genre: String, keywords: String) {
        val intent = Intent(this, DiscoverActivity::class.java)
        intent.putExtra(Constant.isMovie, true)
        intent.putExtra(Constant.INTENT_STATUS, status)
        intent.putExtra(Constant.keywords, keywords)
        intent.putExtra(Constant.genre, genre)
        startActivity(intent)
    }

    override fun setClickableSpan(textView: TextView, list: MutableList<GenresItem?>?) {
        PatternEditableBuilder().addPattern(Pattern.compile("(\\w+) (\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(text, it.toString(), "")
                    }
                }
            }).addPattern(Pattern.compile("(\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(text, it.toString(), "")
                    }
                }
            }).addPattern(Pattern.compile("(\\w+) & (\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(text, it.toString(), "")
                    }
                }
            }).into(textView)
        textView.highlightColor = Color.TRANSPARENT
    }

    override fun setViewExternalIDs(data: ExternalIds) {
        data.facebookId?.let { s: String ->
            movieDetailBinding.movieDetailInfo.facebook.visibility = View.VISIBLE
            movieDetailBinding.movieDetailInfo.facebook.setOnClickListener {
                setOpenURL(s, "facebook")
            }
        } ?: kotlin.run {
            movieDetailBinding.movieDetailInfo.facebook.visibility = View.GONE
        }
        data.twitterId?.let { s: String ->
            movieDetailBinding.movieDetailInfo.twitter.visibility = View.VISIBLE
            movieDetailBinding.movieDetailInfo.twitter.setOnClickListener {
                setOpenURL(s, "twitter")
            }
        } ?: kotlin.run {
            movieDetailBinding.movieDetailInfo.twitter.visibility = View.GONE
        }
        data.instagramId?.let { s: String ->
            movieDetailBinding.movieDetailInfo.instagram.visibility = View.VISIBLE
            movieDetailBinding.movieDetailInfo.instagram.setOnClickListener {
                setOpenURL(s, "instagram")
            }
        } ?: kotlin.run {
            movieDetailBinding.movieDetailInfo.instagram.visibility = View.GONE
        }
    }

    override fun setOpenURL(url: String, status: String) {
        val intent: Intent? = when (status) {
            "facebook" -> {
                Util.startFacebook(this, url)
            }
            "instagram" -> {
                Util.startInstagram(this, url)
            }
            "twitter" -> {
                Util.startTwitter(this, url)
            }
            else -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            }
        }
        startActivity(intent)
    }

    override fun setOnSetChange(name: String) {
        movieDetailBinding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                movieDetailBinding.appbar.post {
                    if (state == State.EXPANDED) {
                        movieDetailBinding.tvTitle.text = name
                        title = ""
                    } else if (state == State.COLLAPSED) {
                        movieDetailBinding.tvTitle.text = ""
                        title = name
                    }
                }
            }
        })
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorDetail", message)
        showDialogNoData(message)
    }

    override fun showDialogNoData(message: String) {
        Util.showRoundedDialog(
            this,
            getString(R.string.no_data),
            message,
            false,
            object : DialogListener {
                override fun onYes() {
                    finish()
                }

                override fun onNo() {
                }
            })
    }

    private val exitElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(
            names: MutableList<String>,
            sharedElements: MutableMap<String, View>
        ) {
            if (reenterState != null) {
                val startingPosition = reenterState?.getInt(Constant.EXTRA_STARTING_ALBUM_POSITION)
                val currentPosition = reenterState?.getInt(Constant.EXTRA_CURRENT_ALBUM_POSITION)
                val isBackdrops = reenterState?.getBoolean(Constant.isBackdrops) ?: true
                if (startingPosition != currentPosition) {
                    // Current element has changed, need to override previous exit transitions
                    val newTransitionName = getString(R.string.transition_title, currentPosition)
                    val newSharedElement = if (isBackdrops)
                        movieDetailBinding.movieDetailMedia.rvBackdrops.findViewWithTag<ConstraintLayout>(
                            newTransitionName
                        )
                    else
                        movieDetailBinding.movieDetailMedia.rvPosters.findViewWithTag(newTransitionName)
                    if (newSharedElement != null) {
                        names.clear()
                        names.add(newTransitionName)

                        sharedElements.clear()
                        sharedElements[newTransitionName] = newSharedElement
                    }
                }
                reenterState = null
            }
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        reenterState = Bundle(data?.extras)
        reenterState?.let {
            val startingPosition = it.getInt(Constant.EXTRA_STARTING_ALBUM_POSITION)
            val currentPosition = it.getInt(Constant.EXTRA_CURRENT_ALBUM_POSITION)
            val isBackdrops = it.getBoolean(Constant.isBackdrops, true)
            val view =
                if (isBackdrops) movieDetailBinding.movieDetailMedia.rvBackdrops else movieDetailBinding.movieDetailMedia.rvPosters
            if (startingPosition != currentPosition) view.scrollToPosition(currentPosition)
            ActivityCompat.postponeEnterTransition(this)

            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    ActivityCompat.startPostponedEnterTransition(this@MovieDetailActivity)
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
        setSupportActionBar(movieDetailBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            movieDetailBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    override fun setUI() {
        movieDetailBinding.refreshLayout.setOnRefreshListener {
            movieDetailBinding.refreshLayout.isRefreshing = false
            presenter.loadData(id)
        }
    }

    override fun showLoading() {
        movieDetailBinding.refreshLayout.visibility = View.GONE
        movieDetailBinding.appbar.visibility = View.GONE
        movieDetailBinding.shimmerView.visibility = View.VISIBLE
        movieDetailBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        movieDetailBinding.shimmerView.startShimmer()
    }

    override fun hideLoading() {
        movieDetailBinding.shimmerView.stopShimmer()
        movieDetailBinding.shimmerView.visibility = View.GONE
        movieDetailBinding.refreshLayout.visibility = View.VISIBLE
        movieDetailBinding.appbar.visibility = View.VISIBLE
    }

}