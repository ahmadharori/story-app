package com.dicoding.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.StoryRowItemBinding
import com.dicoding.storyapp.view.storyDetail.StoryDetailActivity
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.storyapp.database.Story

class StoryListAdapter :
    PagingDataAdapter<Story, StoryListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    inner class ListViewHolder(var binding: StoryRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Story) {
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .into(binding.photo)
                binding.apply {
                    name.text = data.name
                    description.text = data.description
                }
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra("Story", data)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(binding.photo, "photo"),
                            Pair(binding.name, "name"),
                            Pair(binding.description, "description")
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}