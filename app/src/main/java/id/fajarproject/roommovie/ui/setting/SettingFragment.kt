package id.fajarproject.roommovie.ui.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.FragmentSettingBinding
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.home.HomeActivity
import id.fajarproject.roommovie.ui.widget.SpinnerImageAdapter
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import java.util.*


class SettingFragment : BaseFragment(), SettingContract.View {

    lateinit var activity: HomeActivity
    private lateinit var settingBinding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.activity = requireActivity() as HomeActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        settingBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return settingBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val size = Util.initializeCache(requireContext())
        settingBinding.tvSize.text = Util.readableFileSize(size)
        settingBinding.cvCache.setOnClickListener {
            Util.deleteCache(requireContext())
            updateFragment()
        }
        settingBinding.tvVersion.text = "${getString(R.string.app_version)} ${Util.getAppVersion()}"
        settingBinding.cvPrivacyPolicy.setOnClickListener {
            moveToDetail(getString(R.string.privacy_policy))
        }
        settingBinding.cvTermsOfUse.setOnClickListener {
            moveToDetail(getString(R.string.terms_of_use))
        }
        settingBinding.cvContribution.setOnClickListener {
            moveToDetail(getString(R.string.contribution))
        }

        settingBinding.switchMode.isChecked =
            AppPreference.getBooleanPreferenceByName(requireContext(), Constant.isNightMode)

        settingBinding.switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppPreference.writePreference(requireContext(), Constant.isNightMode, true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppPreference.writePreference(requireContext(), Constant.isNightMode, false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            refreshActivity()
        }

        settingBinding.cvNightMode.setOnClickListener {
            settingBinding.switchMode.isChecked = !settingBinding.switchMode.isChecked
        }
        settingBinding.cvLanguage.setOnClickListener {
            settingBinding.spLanguage.callOnClick()
        }

        val adapter = SpinnerImageAdapter(
            requireContext(),
            arrayOf(
                R.drawable.ic_indonesia,
                R.drawable.ic_england
            )
        )
        Log.d("Locale", "${Locale.getDefault()}")

        settingBinding.spLanguage.adapter = adapter
        settingBinding.spLanguage.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                AppPreference.writePreference(activity, Constant.locale, "in")
                activity.updateLocale(Locale("in", "ID"))
            } else {
                AppPreference.writePreference(activity, Constant.locale, "en")
                activity.updateLocale(Locale.ENGLISH)
            }
            true
        }

        when (AppPreference.getStringPreferenceByName(activity, Constant.locale)) {
            "en" -> {
                settingBinding.spLanguage.setSelection(1)
            }
            "in" -> {
                settingBinding.spLanguage.setSelection(0)
            }
            else -> {
                settingBinding.spLanguage.setSelection(0)
            }
        }
    }

    override fun updateFragment() {
        parentFragmentManager.beginTransaction().detach(this).attach(this)
            .setPrimaryNavigationFragment(this).commit()
    }

    override fun moveToDetail(status: String) {
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS, status)
        startActivity(intent)
    }

    override fun refreshActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        startActivity(intent)
    }

}