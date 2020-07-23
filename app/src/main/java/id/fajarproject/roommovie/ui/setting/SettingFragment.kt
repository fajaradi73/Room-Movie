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
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.home.HomeActivity
import id.fajarproject.roommovie.ui.widget.SpinnerImageAdapter
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.fragment_setting.*
import java.util.*


class SettingFragment : BaseFragment(),SettingContract.View {

    lateinit var activity: HomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.activity = requireActivity() as HomeActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val size = Util.initializeCache(requireContext())
        tvSize.text = Util.readableFileSize(size)
        cvCache.setOnClickListener {
            Util.deleteCache(requireContext())
            updateFragment()
        }
        tvVersion.text = "${getString(R.string.app_version)} ${Util.getAppVersion()}"
        cvPrivacyPolicy.setOnClickListener {
            moveToDetail(getString(R.string.privacy_policy))
        }
        cvTermsOfUse.setOnClickListener {
            moveToDetail(getString(R.string.terms_of_use))
        }
        cvContribution.setOnClickListener {
            moveToDetail(getString(R.string.contribution))
        }

        switchMode.isChecked = AppPreference.getBooleanPreferenceByName(requireContext(),Constant.isNightMode)

        switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                AppPreference.writePreference(requireContext(),Constant.isNightMode,true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppPreference.writePreference(requireContext(),Constant.isNightMode,false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            refreshActivity()
        }

        cvNightMode.setOnClickListener {
            switchMode.isChecked = !switchMode.isChecked
        }
        cvLanguage.setOnClickListener {
            spLanguage.callOnClick()
        }

        val adapter = SpinnerImageAdapter(
            requireContext(),
            arrayOf(
                R.drawable.ic_indonesia,
                R.drawable.ic_england
            )
        )
        Log.d("Locale","${Locale.getDefault()}")

        spLanguage.adapter = adapter
        spLanguage.setOnItemClickListener { _, _, position, _ ->
            if (position == 0){
                AppPreference.writePreference(activity,Constant.locale,"in")
                activity.updateLocale(Locale("in","ID"))
            }else{
                AppPreference.writePreference(activity,Constant.locale,"en")
                activity.updateLocale(Locale.ENGLISH)
            }
            true
        }

        when (AppPreference.getStringPreferenceByName(activity,Constant.locale)) {
            "en" -> {
                spLanguage.setSelection(1)
            }
            "in" -> {
                spLanguage.setSelection(0)
            }
            else -> {
                spLanguage.setSelection(0)
            }
        }
    }

    override fun updateFragment(){
        requireFragmentManager().beginTransaction().
        detach(this).
        attach(this).
        setPrimaryNavigationFragment(this).
        commit()
    }

    override fun moveToDetail(status : String){
        val intent = Intent(requireContext(),WebViewActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS,status)
        startActivity(intent)
    }

    override fun refreshActivity() {
        val intent = Intent(requireContext(),HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        startActivity(intent)
    }

}