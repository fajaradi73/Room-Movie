package id.fajarproject.roommovie.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import id.fajarproject.roommovie.ui.widget.MyContextWrapper
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import io.reactivex.disposables.CompositeDisposable


/**
 * Create by Fajar Adi Prasetyo on 10/07/2020.
 */
open class BaseFragment : Fragment() {

    private val subscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        if (AppPreference.getBooleanPreferenceByName(requireContext(),Constant.isNightMode)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

}