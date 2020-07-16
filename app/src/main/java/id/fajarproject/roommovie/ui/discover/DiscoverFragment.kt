package id.fajarproject.roommovie.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import id.fajarproject.roommovie.R
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment(private var viewContract : DiscoverContract.Parsing,private var isMovie : Boolean) : SuperBottomSheetFragment(),DiscoverContract.Fragment {

    private lateinit var arrayPopular : Array<String>
    private lateinit var arrayRating : Array<String>
    private lateinit var arrayDate : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arrayPopular = resources.getStringArray(R.array.arrayPopularity)
        arrayDate    = resources.getStringArray(R.array.arrayDate)
        arrayRating  = resources.getStringArray(R.array.arrayRating)
        setViewSpinner()
        setAction()
    }


    override fun setViewSpinner(){
        spPopular.adapter   = getAdapter(arrayPopular)
        spRating.adapter    = getAdapter(arrayRating)
        spDate.adapter      = getAdapter(arrayDate)
    }

    override fun setAction(){
        spPopular.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                1 -> {
                    viewContract.onPassData("popularity.asc")
                    dismiss()
                    true
                }
                2 -> {
                    viewContract.onPassData("popularity.desc")
                    dismiss()
                    true
                }
                else -> {
                    false
                }
            }
        }
        spRating.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                1 -> {
                    viewContract.onPassData("vote_average.asc")
                    dismiss()
                    true
                }
                2 -> {
                    viewContract.onPassData("vote_average.desc")
                    dismiss()
                    true
                }
                else -> {
                    false
                }
            }
        }
        spDate.setOnItemClickListener { _, _, position, _ ->
            var date = "release_date"
            if (!isMovie){
                date = "first_air_date"
            }
            when (position) {
                1 -> {
                    viewContract.onPassData("$date.asc")
                    dismiss()
                    true
                }
                2 -> {
                    viewContract.onPassData("$date.desc")
                    dismiss()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun getAdapter(list: Array<String>) : ArrayAdapter<String?>{
        val adapter: ArrayAdapter<String?> = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item, list
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    override fun getBackgroundColor(): Int {
        return ContextCompat.getColor(requireContext(),R.color.background)
    }

    override fun getCornerRadius(): Float {
        return resources.getDimension(R.dimen.padding_20)
    }
}