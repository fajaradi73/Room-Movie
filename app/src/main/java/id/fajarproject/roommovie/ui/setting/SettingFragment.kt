package id.fajarproject.roommovie.ui.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.home.HomeActivity
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : BaseFragment() {

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

        switchMode.isChecked = AppPreference.getBooleanPreferenceByName(requireContext(),Constant.isNightMode)

        switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                AppPreference.writePreference(requireContext(),Constant.isNightMode,true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppPreference.writePreference(requireContext(),Constant.isNightMode,false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            requireActivity().finish()
            startActivity(Intent(requireContext(),HomeActivity::class.java))
//            updateFragment()
        }
    }

    private fun updateFragment(){
        requireFragmentManager().beginTransaction().
        detach(this).
        attach(this).
        setPrimaryNavigationFragment(this).
        commit()
    }

    private fun moveToDetail(status : String){
        val intent = Intent(requireContext(),WebViewActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS,status)
        startActivity(intent)
    }

}