package com.plogging.ecorun.ui.main.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plogging.ecorun.R
import com.plogging.ecorun.data.model.GlobalRank
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.databinding.ItemSingleImageBinding
import com.plogging.ecorun.util.glide.GlideApp
import com.plogging.ecorun.util.recycler.DiffCallback

class UserPagingAdapter(private val otherUser: GlobalRank?) :
    PagingDataAdapter<MyDatabasePlogging, UserPagingAdapter.UserPloggingViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: UserPloggingViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPloggingViewHolder =
        UserPloggingViewHolder(
            ItemSingleImageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    inner class UserPloggingViewHolder(private val binding: ItemSingleImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyDatabasePlogging) {
            binding.ivPloggingImage.setOnClickListener {
                if (otherUser == null) { // 자신의 플로깅 정보
                    val bundle = bundleOf("ploggingData" to item)
                    it.findNavController().navigate(R.id.action_user_to_detail_plogging, bundle)
                } else { // 다른 사람 랭킹 플로깅 정보
                    val bundle = bundleOf("imageUri" to item.ploggingImg)
                    val extras = FragmentNavigatorExtras(it to "detailPhotoView")
                    it.findNavController()
                        .navigate(R.id.action_rank_to_image_dialog, bundle, null, extras)
                }
            }
            GlideApp.with(itemView.context)
                .load(item.ploggingImg)
                .override(400)
                .placeholder(R.drawable.bg_default)
                .into(binding.ivPloggingImage)
        }
    }
}