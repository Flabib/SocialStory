package id.my.fahdilabib.socialstory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import id.my.fahdilabib.socialstory.R
import id.my.fahdilabib.socialstory.data.remote.responses.StoryResponse
import id.my.fahdilabib.socialstory.databinding.RvItemStoryBinding
import id.my.fahdilabib.socialstory.utils.uctHumanize

class StoryRecyclerAdapter : PagingDataAdapter<StoryResponse, StoryRecyclerAdapter.StoryHolder>(DIFF_CALLBACK)  {

    private var onItemClickCallback: OnItemClickCallback? = null
//    var stories = listOf<StoryResponse>()
//        set(value) {
//            field = value
//
//            notifyDataSetChanged()
//        }

    interface OnItemClickCallback {
        fun onItemClicked(it: View, story: StoryResponse)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
        val binding = RvItemStoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return StoryHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class StoryHolder(val binding: RvItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryResponse) {
            with (itemView) {
                binding.itemImage.load(story.photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.image_loading)
                    transformations(CircleCropTransformation())
                }

                binding.itemName.text = story.name
                binding.itemDate.text = uctHumanize(story.createdAt)

                setOnClickListener {
                    onItemClickCallback?.onItemClicked(it, story)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse>() {
            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
