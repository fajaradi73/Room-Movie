package id.fajarproject.roommovie.ui.tvDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityTvDetailBinding
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
import id.fajarproject.roommovie.ui.seasons.SeasonActivity
import id.fajarproject.roommovie.ui.video.VideoListActivity
import id.fajarproject.roommovie.ui.video.VideoPlayerActivity
import id.fajarproject.roommovie.ui.widget.AppBarStateChangeListener
import id.fajarproject.roommovie.ui.widget.DialogListener
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.ui.widget.PatternEditableBuilder
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import org.parceler.Parcels
import java.util.regex.Pattern
import javax.inject.Inject

class TvDetailActivity : BaseActivity(), TvDetailContract.View {

    @Inject
    lateinit var presenter: TvDetailContract.Presenter
    var id = 0
    private var reenterState: Bundle? = null

    private lateinit var tvDetailBinding: ActivityTvDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvDetailBinding = ActivityTvDetailBinding.inflate(layoutInflater)
        setContentView(tvDetailBinding.root)
        injectDependency()
        presenter.attach(this, this)
        setToolbar()
        setUI()

        id = intent.getIntExtra(Constant.idMovie, 0)

        if (id == 0) {
            showDialogNoData("")
            return
        }
        if (isConnection)
            presenter.loadData(id)
        ActivityCompat.setExitSharedElementCallback(this, exitElementCallback)

    }

    @SuppressLint("SetTextI18n")
    override fun showDataSuccess(data: MovieItem) {
        tvDetailBinding.tvDetailOverview.tvGenre.text = presenter.getGenre(data.genres)
        setClickableSpan(tvDetailBinding.tvDetailOverview.tvGenre, data.genres)

        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.posterPath)
            .placeholder(Util.circleLoading(this))
            .into(tvDetailBinding.ivMovie)
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.backdropPath)
            .placeholder(Util.circleLoading(this))
            .into(tvDetailBinding.ivBackground)

        setOnSetChange(data.name ?: "")
        var runtime = 0
        data.episodeRunTime?.let { list ->
            if (list.size > 0) {
                list[0]?.let {
                    runtime = it
                }
            }
        }
        tvDetailBinding.tvDetailOverview.tvRatting.text =
            "${data.voteAverage.toString()}  •  ${data.firstAirDate} \u2022 ${runtime}m "
        tvDetailBinding.tvDetailOverview.tvOverview.text = data.overview

        data.external_ids?.let {
            setViewExternalIDs(it)
        }
        tvDetailBinding.tvDetailInfo.tvStatus.text = data.status
        tvDetailBinding.tvDetailInfo.tvName.text = data.originalName
        tvDetailBinding.tvDetailInfo.tvLanguage.text = Util.getLanguage(data.originalLanguage, this)
        tvDetailBinding.tvDetailInfo.tvType.text = data.type

        data.keywords?.results?.let {
            if (it.size > 0) {
                setViewKeyword(it)
            }
        }
        data.credits?.cast?.let {
            if (it.size > 0) {
                tvDetailBinding.tvDetailPeople.tvFullCast.visibility = View.VISIBLE
                setViewCasts(it)
                tvDetailBinding.tvDetailPeople.tvFullCast.setOnClickListener {
                    val intent = Intent(activity, CreditsActivity::class.java)
                    intent.putExtra(Constant.isMovie, false)
                    intent.putExtra(Constant.INTENT_STATUS, data.name)
                    intent.putExtra(Constant.idMovie, data.id)
                    startActivity(intent)
                }
            } else {
                tvDetailBinding.tvDetailPeople.tvFullCast.visibility = View.GONE
            }
        }
        data.videos?.results?.let {
            if (it.size > 0) {
                tvDetailBinding.tvDetailMedia.allVideo.visibility = View.VISIBLE
                setViewVideo(it)
                tvDetailBinding.tvDetailMedia.allVideo.setOnClickListener {
                    val intent = Intent(this, VideoListActivity::class.java)
                    intent.putExtra(Constant.title, data.name)
                    intent.putExtra(Constant.idMovie, data.id)
                    intent.putExtra(Constant.isMovie, false)
                    startActivity(intent)
                }
            } else {
                tvDetailBinding.tvDetailMedia.allVideo.visibility = View.GONE
            }
        }
        data.images?.backdrops?.let { list: MutableList<PicturesItem?> ->
            if (list.size > 0) {
                tvDetailBinding.tvDetailMedia.allBackdrops.visibility = View.VISIBLE
                setViewBackdrops(list)
                tvDetailBinding.tvDetailMedia.allBackdrops.setOnClickListener {
                    moveToPicture(data.name ?: "", list)
                }
            } else {
                tvDetailBinding.tvDetailMedia.allBackdrops.visibility = View.GONE
            }
        }
        data.images?.posters?.let { list: MutableList<PicturesItem?> ->
            if (list.size > 0) {
                tvDetailBinding.tvDetailMedia.allPosters.visibility = View.VISIBLE
                setViewPosters(list)
                tvDetailBinding.tvDetailMedia.allPosters.setOnClickListener {
                    moveToPicture(data.name ?: "", list)
                }
            } else {
                tvDetailBinding.tvDetailMedia.allPosters.visibility = View.GONE
            }
        }
        data.recommendations?.movieList?.let {
            if (it.size > 0) {
                setViewRecommendation(it)
            }
        }
        data.networks?.let {
            if (it.size > 0) {
                setViewNetwork(it)
            }
        }

        data.seasons?.let { list: MutableList<SeasonsItem?> ->
            tvDetailBinding.tvDetailSeason.tvFullSeason.visibility = View.VISIBLE
            val last: MutableList<SeasonsItem?> = arrayListOf()

            list[list.size - 1]?.airDate?.let {
                last.add(list[list.size - 1])
            } ?: kotlin.run {
                if (list.size > 1) {
                    last.add(list[list.size - 2])
                } else {
                    last.add(list[list.size])
                }
            }
            setViewSeason(last, data.name ?: "")
            tvDetailBinding.tvDetailSeason.tvFullSeason.setOnClickListener {
                val intent = Intent(activity, SeasonActivity::class.java)
                intent.putExtra(Constant.title, data.name)
                intent.putExtra(Constant.season, Parcels.wrap(list))
                startActivity(intent)
            }
        } ?: kotlin.run {
            tvDetailBinding.tvDetailSeason.tvFullSeason.visibility = View.GONE
        }

        tvDetailBinding.tvDetailInfo.link.setOnClickListener {
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
        chip.isClickable = true
        chip.checkedIcon = null
        chip.tag = item?.name
        chip.setTextAppearanceResource(R.style.ChipText)
        chip.setOnClickListener {
            item?.id?.let {
                moveToDiscover(item.name ?: "", "", it.toString(), "")
            }
        }
        tvDetailBinding.tvDetailInfo.cgKeyword.addView(chip, i, params)
    }

    override fun setViewCasts(list: MutableList<CreditsItem?>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tvDetailBinding.tvDetailPeople.rvCast.layoutManager = layoutManager
        val adapter =
            DetailCastAdapter(
                this,
                list
            )
        tvDetailBinding.tvDetailPeople.rvCast.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id.let {
                    val intent = Intent(activity, PeopleDetailActivity::class.java)
                    intent.putExtra(Constant.idPeople, it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setViewVideo(list: MutableList<VideosItem?>) {
        if (list.size > 0) {
            tvDetailBinding.tvDetailMedia.allVideo.visibility = View.VISIBLE
            if (tvDetailBinding.tvDetailMedia.video.text.contains(" (${list.size})")) {
                tvDetailBinding.tvDetailMedia.video.text = getString(R.string.video)
            }
            Util.setSpannable(
                tvDetailBinding.tvDetailMedia.video,
                " (${list.size})",
                ContextCompat.getColor(this, R.color.textColorSecondary)
            )
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            tvDetailBinding.tvDetailMedia.rvVideo.layoutManager = layoutManager
            val adapter =
                DetailVideoAdapter(
                    this,
                    list
                )
            tvDetailBinding.tvDetailMedia.rvVideo.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val item = adapter.getItem(position)
                    item?.key?.let {
                        val intent = Intent(this@TvDetailActivity, VideoPlayerActivity::class.java)
                        intent.putExtra(Constant.keyVideo, it)
                        intent.putExtra(Constant.title, item.name)
                        startActivity(intent)
                    }
                }
            })
        } else {
            tvDetailBinding.tvDetailMedia.allVideo.visibility = View.GONE
        }
    }

    override fun setViewBackdrops(list: MutableList<PicturesItem?>) {
        if (list.size > 0) {
            tvDetailBinding.tvDetailMedia.allBackdrops.visibility = View.VISIBLE
            if (tvDetailBinding.tvDetailMedia.backdrops.text.contains(" (${list.size})")) {
                tvDetailBinding.tvDetailMedia.backdrops.text = getString(R.string.backdrops)
            }
            Util.setSpannable(
                tvDetailBinding.tvDetailMedia.backdrops,
                " (${list.size})",
                ContextCompat.getColor(this, R.color.textColorSecondary)
            )
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            tvDetailBinding.tvDetailMedia.rvBackdrops.layoutManager = layoutManager
            val adapter =
                DetailBackdropsAdapter(
                    this,
                    list
                )
            tvDetailBinding.tvDetailMedia.rvBackdrops.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    view?.let {
                        showPreviewImage(it, position, list, false)
                    }
                }
            })
        } else {
            tvDetailBinding.tvDetailMedia.allBackdrops.visibility = View.GONE
        }
    }

    override fun setViewPosters(list: MutableList<PicturesItem?>) {
        if (list.size > 0) {
            tvDetailBinding.tvDetailMedia.allPosters.visibility = View.VISIBLE
            if (tvDetailBinding.tvDetailMedia.posters.text.contains(" (${list.size})")) {
                tvDetailBinding.tvDetailMedia.posters.text = getString(R.string.posters)
            }
            Util.setSpannable(
                tvDetailBinding.tvDetailMedia.posters,
                " (${list.size})",
                ContextCompat.getColor(this, R.color.textColorSecondary)
            )
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            tvDetailBinding.tvDetailMedia.rvPosters.layoutManager = layoutManager
            val adapter =
                DetailPostersAdapter(
                    this,
                    list
                )
            tvDetailBinding.tvDetailMedia.rvPosters.adapter = adapter
            adapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    view?.let {
                        showPreviewImage(it, position, list, false)
                    }
                }
            })
        } else {
            tvDetailBinding.tvDetailMedia.allPosters.visibility = View.GONE
        }
    }

    override fun setViewRecommendation(list: MutableList<MovieItem?>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tvDetailBinding.tvDetailRecommendation.rvRecommendation.layoutManager = layoutManager
        val adapter =
            DetailRecommendationsAdapter(
                this,
                list
            )
        tvDetailBinding.tvDetailRecommendation.rvRecommendation.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id?.let {
                    val intent = Intent(this@TvDetailActivity, TvDetailActivity::class.java)
                    intent.putExtra(Constant.idMovie, it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setViewNetwork(list: MutableList<NetworksItem?>) {
        val layoutManager = GridLayoutManager(this, 4)
        tvDetailBinding.tvDetailInfo.rvNetwork.layoutManager = layoutManager
        val adapter =
            DetailNetworkAdapter(
                this,
                list
            )
        tvDetailBinding.tvDetailInfo.rvNetwork.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id?.let {
                    moveToDiscover(item.name ?: "", "", "", it.toString())
                }
            }
        })
    }

    override fun setViewSeason(list: MutableList<SeasonsItem?>, title: String) {

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        tvDetailBinding.tvDetailSeason.rvSeason.layoutManager = layoutManager
        val adapter =
            DetailSeasonAdapter(
                this,
                list, title
            )
        tvDetailBinding.tvDetailSeason.rvSeason.adapter = adapter
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

    override fun moveToDiscover(status: String, genre: String, keywords: String, networks: String) {
        val intent = Intent(activity, DiscoverActivity::class.java)
        intent.putExtra(Constant.isMovie, false)
        intent.putExtra(Constant.INTENT_STATUS, status)
        intent.putExtra(Constant.keywords, keywords)
        intent.putExtra(Constant.genre, genre)
        intent.putExtra(Constant.networks, networks)
        startActivity(intent)
    }

    override fun setClickableSpan(textView: TextView, list: MutableList<GenresItem?>?) {
        PatternEditableBuilder().addPattern(Pattern.compile("(\\w+) (\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : PatternEditableBuilder.SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(item.name ?: text, it.toString(), "", "")
                    }
                }
            }).addPattern(Pattern.compile("(\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : PatternEditableBuilder.SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(item.name ?: text, it.toString(), "", "")
                    }
                }
            }).addPattern(Pattern.compile("(\\w+) & (\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : PatternEditableBuilder.SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(item.name ?: text, it.toString(), "", "")
                    }
                }
            }).addPattern(Pattern.compile("(\\w+)-(\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : PatternEditableBuilder.SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(item.name ?: text, it.toString(), "", "")
                    }
                }
            }).addPattern(Pattern.compile("(\\w+)-(\\w+) & (\\w+)"),
            ContextCompat.getColor(this, R.color.textColorPrimary),
            object : PatternEditableBuilder.SpannableClickedListener {
                override fun onSpanClicked(text: String) {
                    val item = presenter.getItem(list, text)
                    item?.id?.let {
                        moveToDiscover(item.name ?: text, it.toString(), "", "")
                    }
                }
            }).into(textView)
        textView.highlightColor = Color.TRANSPARENT
    }

    override fun setViewExternalIDs(data: ExternalIds) {
        data.facebookId?.let { s: String ->
            tvDetailBinding.tvDetailInfo.facebook.visibility = View.VISIBLE
            tvDetailBinding.tvDetailInfo.facebook.setOnClickListener {
                setOpenURL(s, "facebook")
            }
        } ?: kotlin.run {
            tvDetailBinding.tvDetailInfo.facebook.visibility = View.GONE
        }
        data.twitterId?.let { s: String ->
            tvDetailBinding.tvDetailInfo.twitter.visibility = View.VISIBLE
            tvDetailBinding.tvDetailInfo.twitter.setOnClickListener {
                setOpenURL(s, "twitter")
            }
        } ?: kotlin.run {
            tvDetailBinding.tvDetailInfo.twitter.visibility = View.GONE
        }
        data.instagramId?.let { s: String ->
            tvDetailBinding.tvDetailInfo.instagram.visibility = View.VISIBLE
            tvDetailBinding.tvDetailInfo.instagram.setOnClickListener {
                setOpenURL(s, "instagram")
            }
        } ?: kotlin.run {
            tvDetailBinding.tvDetailInfo.instagram.visibility = View.GONE
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
        tvDetailBinding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                tvDetailBinding.appbar.post {
                    if (state == State.EXPANDED) {
                        tvDetailBinding.tvTitle.text = name
                        title = ""
                    } else if (state == State.COLLAPSED) {
                        tvDetailBinding.tvTitle.text = ""
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
//                finish()
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
                        tvDetailBinding.tvDetailMedia.rvBackdrops.findViewWithTag<ConstraintLayout>(
                            newTransitionName
                        )
                    else
                        tvDetailBinding.tvDetailMedia.rvPosters.findViewWithTag(newTransitionName)
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
                if (isBackdrops) tvDetailBinding.tvDetailMedia.rvBackdrops else tvDetailBinding.tvDetailMedia.rvPosters
            if (startingPosition != currentPosition) view.scrollToPosition(currentPosition)
            ActivityCompat.postponeEnterTransition(this)

            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    ActivityCompat.startPostponedEnterTransition(this@TvDetailActivity)
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
        setSupportActionBar(tvDetailBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            tvDetailBinding.toolbar.navigationIcon!!,
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
        tvDetailBinding.refreshLayout.setOnRefreshListener {
            tvDetailBinding.refreshLayout.isRefreshing = false
            presenter.loadData(id)
        }
    }

    override fun showLoading() {
        tvDetailBinding.refreshLayout.visibility = View.GONE
        tvDetailBinding.appbar.visibility = View.GONE
        tvDetailBinding.shimmerView.visibility = View.VISIBLE
        tvDetailBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        tvDetailBinding.shimmerView.startShimmer()
    }

    override fun hideLoading() {
        tvDetailBinding.shimmerView.stopShimmer()
        tvDetailBinding.shimmerView.visibility = View.GONE
        tvDetailBinding.refreshLayout.visibility = View.VISIBLE
        tvDetailBinding.appbar.visibility = View.VISIBLE
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
}