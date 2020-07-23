package id.fajarproject.roommovie.di.component

import dagger.Component
import id.fajarproject.roommovie.ui.movieList.MovieListActivity
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.ui.credits.CreditsActivity
import id.fajarproject.roommovie.ui.discover.DiscoverActivity
import id.fajarproject.roommovie.ui.home.HomeActivity
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailActivity
import id.fajarproject.roommovie.ui.picture.PictureActivity
import id.fajarproject.roommovie.ui.previewPicture.PreviewPictureActivity
import id.fajarproject.roommovie.ui.search.SearchActivity
import id.fajarproject.roommovie.ui.seasons.SeasonActivity
import id.fajarproject.roommovie.ui.setting.WebViewActivity
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.tvList.TvListActivity
import id.fajarproject.roommovie.ui.video.VideoPlayerActivity
import id.fajarproject.roommovie.ui.video.VideoListActivity


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
@Component(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(homeActivity: HomeActivity)
    fun inject(movieListActivity: MovieListActivity)
    fun inject(searchActivity: SearchActivity)
    fun inject(webViewActivity: WebViewActivity)
    fun inject(tvListActivity: TvListActivity)
    fun inject(movieDetailActivity: MovieDetailActivity)
    fun inject(tvDetailActivity: TvDetailActivity)
    fun inject(videoPlayerActivity: VideoPlayerActivity)
    fun inject(videoListActivity: VideoListActivity)
    fun inject(previewPictureActivity: PreviewPictureActivity)
    fun inject(pictureActivity: PictureActivity)
    fun inject(peopleDetailActivity: PeopleDetailActivity)
    fun inject(discoverActivity: DiscoverActivity)
    fun inject(creditsActivity: CreditsActivity)
    fun inject(seasonActivity: SeasonActivity)
}