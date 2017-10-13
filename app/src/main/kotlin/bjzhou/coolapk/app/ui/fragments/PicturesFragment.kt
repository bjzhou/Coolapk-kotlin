package bjzhou.coolapk.app.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.PictureEntity
import bjzhou.coolapk.app.net.ApiManager
import com.squareup.picasso.Picasso
import java.util.*

/**
 * author: zhoubinjia
 * date: 2017/1/25
 */
class PicturesFragment : Fragment() {

    private var type: String = "recommended"
    private lateinit var mAdapter: PicturesAdapter

    internal enum class PictureType {
        recommend, hot, splash, newest, _2k
    }

    private lateinit var mPicturesView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = arguments.getInt(ARG_SECTION_NUMBER)
        if (position == 4) {
            type = "2k"
        } else {
            type = PictureType.values()[position].name
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    private fun findViews(parent: View) {
        mPicturesView = parent.findViewById(R.id.picturesView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViews(view)
        mAdapter = PicturesAdapter()
        mPicturesView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mPicturesView.adapter = mAdapter
        ApiManager.instance.mServiceV6.getPictureList(type = type).enqueue(mAdapter::setEntities)
    }

    internal inner class PicturesAdapter : RecyclerView.Adapter<ViewHolder>() {

        private var entities: List<PictureEntity> = ArrayList()

        fun setEntities(entities: List<PictureEntity>) {
            this.entities = entities
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid_item_picture, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Picasso.with(holder.itemView.context)
                    .load(entities[position].pic)
                    .fit()
                    .into(holder.image)
        }

        override fun getItemCount(): Int {
            return entities.size
        }
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    companion object {

        private val ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER"

        fun newInstance(sectionNumber: Int): PicturesFragment {
            val fragment = PicturesFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
