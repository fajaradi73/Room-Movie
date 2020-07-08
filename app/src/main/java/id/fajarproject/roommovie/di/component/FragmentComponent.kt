package id.fajarproject.roommovie.di.component

import dagger.Component
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.ui.movie.MovieFragment
import id.fajarproject.roommovie.ui.people.PeopleFragment
import id.fajarproject.roommovie.ui.setting.SettingFragment
import id.fajarproject.roommovie.ui.tv.TvFragment


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
@Component(modules = [FragmentModule::class])
interface FragmentComponent {

    fun inject(movieFragment: MovieFragment)

    fun inject(tvFragment: TvFragment)

    fun inject(peopleFragment: PeopleFragment)

    fun inject(settingFragment: SettingFragment)
}