package id.fajarproject.roommovie.ui.credits

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.FragmentCreditsBinding
import id.fajarproject.roommovie.di.component.DaggerFragmentComponent
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util

/**
 * A placeholder fragment containing a simple view.
 */
class CreditsFragment : Fragment(),CreditsContract.Fragment {

    private lateinit var creditsBinding: FragmentCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    lateinit var activity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
    }

    override fun injectDependency() {
        val fragmentComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        fragmentComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        creditsBinding = FragmentCreditsBinding.inflate(inflater,container,false)
        return creditsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
    }

    override fun setRecycleView() {
        val mNoOfColumns        = Util.calculateNoOfColumns(requireContext())
        val layoutManager       = GridLayoutManager(requireContext(),mNoOfColumns)
        creditsBinding.rvCredits.layoutManager = layoutManager
    }

    override fun showData(list: MutableList<CreditsItem?>) {
        val adapter         = CreditsAdapter(activity,list)
        creditsBinding.rvCredits.adapter   = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item?.id.let {
                    val intent = Intent(activity, PeopleDetailActivity::class.java)
                    intent.putExtra(Constant.idPeople,it)
                    startActivity(intent)
                }
            }
        })
    }
}