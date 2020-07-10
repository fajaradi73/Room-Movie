package id.fajarproject.roommovie.ui.movieDetail

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.*
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.detailAdapter.*
import id.fajarproject.roommovie.ui.widget.AppBarStateChangeListener
import id.fajarproject.roommovie.ui.widget.DialogListener
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.ui.widget.PatternEditableBuilder
import id.fajarproject.roommovie.ui.widget.PatternEditableBuilder.SpannableClickedListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.activity_movie_detail_info.*
import kotlinx.android.synthetic.main.activity_movie_detail_media.*
import kotlinx.android.synthetic.main.activity_movie_detail_overview.*
import kotlinx.android.synthetic.main.activity_movie_detail_people.*
import kotlinx.android.synthetic.main.activity_movie_detail_recommendation.*
import java.util.regex.Pattern
import javax.inject.Inject


class MovieDetailActivity : BaseActivity(),MovieDetailContract.View {

    @Inject lateinit var presenter: MovieDetailContract.Presenter
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        injectDependency()
        presenter.attach(this,this)
        setToolbar()
        setUI()

        id      = intent.getIntExtra(Constant.idMovie,0)

        if (id == 0){
            showDialogNoData()
            return
        }
        if (isConnection) {
            presenter.loadData(id)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showDataSuccess(data: MovieItem) {
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.posterPath)
            .placeholder(Util.circleLoading(this))
            .into(ivMovie)
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.backdropPath)
            .placeholder(Util.circleLoading(this))
            .into(ivBackground)
        setOnSetChange(data.title ?: "")

        tvTagLine.text  = data.tagline
        tvRatting.text  = "${data.voteAverage.toString()}  •  ${data.releaseDate} \u2022 ${data.runtime?.div(
        60
        )}h${data.runtime?.rem(60)}m "

        tvOverview.text = data.overview
        tvStatus.text   = data.status
        tvLanguage.text = presenter.getLanguage(data.originalLanguage,data.spokenLanguages)
        tvBudget.text   = "$ ${Util.currencyFormatter(data.budget.toString())}"
        tvRevenue.text  = "$ ${Util.currencyFormatter(data.revenue.toString())}"
        tvGenre.text    = presenter.getGenre(data.genres)
        setClickableSpan(tvGenre)

        data.external_ids?.let {
            setViewExternalIDs(it)
        }
        data.keywords?.keywords?.let {
            setViewKeyword(it)
        }
        data.credits?.cast?.let {
            setViewCasts(it)
        }
        data.videos?.results?.let {
            setViewVideo(it)
        }
        data.images?.backdrops?.let {
            setViewBackdrops(it)
        }
        data.images?.posters?.let {
            setViewPosters(it)
        }
        data.recommendations?.movieList?.let {
            setViewRecommendation(it)
        }
        ivLink.setOnClickListener {
            data.homepage?.let {
                if (it.isNotEmpty()) {
                    setOpenURL("https://$it/", "homepage")
                }else{
                    setOpenURL(Constant.BASE_THE_MOVIE_DB,"homepage")
                }
            } ?: kotlin.run {
                setOpenURL(Constant.BASE_THE_MOVIE_DB,"homepage")
            }
        }
    }

    override fun setViewKeyword(list: MutableList<KeywordsItem?>){
        for (i in list.indices){
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(6, 6, 6, 6)
            configureChip(list[i],params,i)
        }
    }

    override fun configureChip(item: KeywordsItem?,params : RelativeLayout.LayoutParams, i : Int){
        val chip = Chip(this,null,R.style.CustomChipChoice)
        chip.layoutParams   = params
        chip.text           = item?.name
        chip.setTextAppearanceResource(R.style.ChipText)
        chip.isClickable    = true
        chip.checkedIcon    = null
        chip.tag            = item?.name
        chip.setOnClickListener {

        }
        cgKeyword.addView(chip,i,params)
    }

    override fun setViewCasts(list: MutableList<CastItem?>) {
        val layoutManager           = LinearLayoutManager(this)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvCast.layoutManager        = layoutManager
        val adapter                 =
            DetailCastAdapter(
                this,
                list
            )
        rvCast.adapter              = adapter
    }

    override fun setViewVideo(list: MutableList<VideosItem?>) {
        if (video.text.contains(" (${list.size})")) {
            video.text = getString(R.string.video)
        }
        Util.setSpannable(video, " (${list.size})", ContextCompat.getColor(this,R.color.textColorSecondary))
        val layoutManager           = LinearLayoutManager(this)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvVideo.layoutManager       = layoutManager
        val adapter                 =
            DetailVideoAdapter(
                this,
                list
            )
        rvVideo.adapter             = adapter
    }

    override fun setViewBackdrops(list: MutableList<BackdropsItem?>) {
        if (backdrops.text.contains(" (${list.size})")) {
            backdrops.text = getString(R.string.backdrops)
        }
        Util.setSpannable(backdrops," (${list.size})",ContextCompat.getColor(this,R.color.textColorSecondary))
        val layoutManager           = LinearLayoutManager(this)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvBackdrops.layoutManager   = layoutManager
        val adapter                 =
            DetailBackdropsAdapter(
                this,
                list
            )
        rvBackdrops.adapter         = adapter
    }

    override fun setViewPosters(list: MutableList<PostersItem?>) {
        if (posters.text.contains(" (${list.size})")) {
            posters.text = getString(R.string.posters)
        }
        Util.setSpannable(posters," (${list.size})",ContextCompat.getColor(this,R.color.textColorSecondary))
        val layoutManager           = LinearLayoutManager(this)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvPosters.layoutManager     = layoutManager
        val adapter                 =
            DetailPostersAdapter(
                this,
                list
            )
        rvPosters.adapter           = adapter
    }

    override fun setViewRecommendation(list: MutableList<MovieItem?>) {
        val layoutManager                   = LinearLayoutManager(this)
        layoutManager.orientation           = LinearLayoutManager.HORIZONTAL
        rvRecommendation.layoutManager      = layoutManager
        val adapter                         =
            DetailRecommendationsAdapter(
                this,
                list
            )
        rvRecommendation.adapter            = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id?.let {
                    val intent = Intent(this@MovieDetailActivity,MovieDetailActivity::class.java)
                    intent.putExtra(Constant.idMovie,it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setClickableSpan(textView : TextView){
        PatternEditableBuilder().addPattern(Pattern.compile("(\\w+) (\\w+)"),ContextCompat.getColor(this,R.color.textColorPrimary),
            object : SpannableClickedListener {
                override fun onSpanClicked(text: String?) {
                    Toast.makeText(
                        this@MovieDetailActivity, "$text",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }).addPattern(Pattern.compile("(\\w+)"),ContextCompat.getColor(this,R.color.textColorPrimary),
            object : SpannableClickedListener {
                override fun onSpanClicked(text: String?) {
                    Toast.makeText(
                        this@MovieDetailActivity, "$text",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }).into(textView)
        textView.highlightColor = Color.TRANSPARENT
    }

    override fun setViewExternalIDs(data: ExternalIds) {
        data.facebookId?.let { s: String ->
            ivFacebook.visibility = View.VISIBLE
            ivFacebook.setOnClickListener {
                setOpenURL(s,"facebook")
            }
        } ?: kotlin.run {
            ivFacebook.visibility = View.GONE
        }
        data.twitterId?.let { s: String ->
            ivTwitter.visibility = View.VISIBLE
            ivTwitter.setOnClickListener {
                setOpenURL(s,"twitter")
            }
        } ?: kotlin.run {
            ivTwitter.visibility = View.GONE
        }
        data.instagramId?.let { s: String ->
            ivInstagram.visibility = View.VISIBLE
            ivInstagram.setOnClickListener {
                setOpenURL(s,"instagram")
            }
        } ?: kotlin.run {
            ivInstagram.visibility = View.GONE
        }
    }

    override fun setOpenURL(url: String,status : String) {
        val intent : Intent? = when (status) {
            "facebook" -> {
                Util.startFacebook(this,url)
            }
            "instagram" -> {
                Util.startInstagram(this,url)
            }
            "twitter" ->{
                Util.startTwitter(this,url)
            }
            else -> {
                Intent(Intent.ACTION_VIEW,Uri.parse(url))
            }
        }
        startActivity(intent)
    }

    override fun setOnSetChange(name : String){
        appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener(){
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                if (state == State.EXPANDED){
                    tvTitle.text    = name
                    title           = ""
                }else if (state == State.COLLAPSED){
                    tvTitle.text    = ""
                    title           = name
                }
            }
        })
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorDetail",message)
        showDialogNoData()
    }

    override fun showDialogNoData() {
        Util.showRoundedDialog(this,getString(R.string.no_data),"",false,object : DialogListener{
            override fun onYes() {
                finish()
            }

            override fun onNo() {
            }
        })
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
        Util.setColorFilter(
            toolbar.navigationIcon!!,
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
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.loadData(id)
        }
    }

    override fun showLoading() {
        refreshLayout.visibility    = View.GONE
        appbar.visibility           = View.GONE
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        shimmerView.startShimmer()
    }

    override fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        refreshLayout.visibility    = View.VISIBLE
        appbar.visibility           = View.VISIBLE
    }

}