package id.fajarproject.roommovie.ui.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

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
            requireFragmentManager().beginTransaction().
            detach(this).
            attach(this).
            setPrimaryNavigationFragment(this).
            commit()
        }
        tvVersion.text = "${getString(R.string.app_version)} ${Util.getAppVersion()}"
        cvPrivacyPolicy.setOnClickListener {
            moveToDetail(getString(R.string.privacy_policy))
        }
        cvTermsOfUse.setOnClickListener {
            moveToDetail(getString(R.string.terms_of_use))
        }
    }

    private fun moveToDetail(status : String){
        val intent = Intent(requireContext(),WebViewActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS,status)
        startActivity(intent)
    }

}