package id.fajarproject.roommovie.ui.movieList

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.base.LoadingViewHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.adapter_search.view.*

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MovieListAdapter(
    var activity: Activity,
    private var list: MutableList<MovieItem?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoadingAdded = false

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_ITEM){
            AdapterHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.adapter_search, parent, false)
                , this.onItemClickListener
            )
        }else {
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.adapter_loading,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val data: MovieItem = list[position] ?: MovieItem()
            holder.itemView.tvTitle.text        = data.title
            holder.itemView.tvDate.text         = Util.convertDate(data.releaseDate ?: "","yyyy-MM-dd","dd MMMM yyyy")
            holder.itemView.tvRatting.text      = data.voteAverage.toString()
            holder.itemView.tvGenre.text        = setGenre(data.genresIds ?: arrayListOf())
            Glide.with(activity)
                .load(Constant.BASE_IMAGE + data.posterPath)
                .error(R.drawable.ic_placeholder)
                .placeholder(Util.circleLoading(activity))
                .into(holder.itemView.ivMovie)
        } else {
            Log.d("Loading", ".....")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getMovieList(): List<MovieItem?> {
        return list
    }

    fun addData(movieItems: List<MovieItem?>) {
        list.addAll(movieItems)
        notifyDataSetChanged()
    }

    fun getMovie(position: Int): MovieItem {
        return list[position] ?: MovieItem()
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        list.add(MovieItem())
        notifyItemInserted(list.size - 1)
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = list.size - 1
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size - 1 && isLoadingAdded) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
    }

    private fun getGenre(id: Int) : String{
        var genre = ""
        val list : MutableList<GenresItem?>? = Util.getGenre(activity,Constant.genreMovie)
        if (list != null) {
            for (data in list){
                if (data?.id == id){
                    genre = data.name ?: ""
                    break
                }
            }
        }
        return genre
    }

    private fun setGenre(list: MutableList<Int>) : String{
        var nameGenre = ""
        for (i in list.indices){
            val name = getGenre(list[i])
            if (name.isNotEmpty()){
                nameGenre += if (i != list.lastIndex){
                    "$name \u2022 "
                }else{
                    name
                }
            }
        }
        return nameGenre
    }
}
