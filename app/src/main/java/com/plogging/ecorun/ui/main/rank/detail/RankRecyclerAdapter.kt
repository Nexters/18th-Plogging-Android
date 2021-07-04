package com.plogging.ecorun.ui.main.rank.detail

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.plogging.ecorun.R
import com.plogging.ecorun.data.model.GlobalRank
import com.plogging.ecorun.databinding.ItemRankBinding
import com.plogging.ecorun.util.glide.GlideApp
import com.plogging.ecorun.util.recycler.DiffCallback

class RankRecyclerAdapter :
    ListAdapter<GlobalRank, RankRecyclerAdapter.RankViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder =
        RankViewHolder(
            ItemRankBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    inner class RankViewHolder(private val itemRankBinding: ItemRankBinding) :
        RecyclerView.ViewHolder(itemRankBinding.root) {
        fun bind(item: GlobalRank, position: Int) {
            GlideApp.with(itemView.context)
                .load(item.profileImg)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.bg_default)
                .into(itemRankBinding.ivRankItemProfile)
            decorateTop3(position)
            itemRankBinding.tvRankItemNumber.text = (position + 1).toString()
            itemRankBinding.tvRankItemName.text = item.displayName
            itemRankBinding.tvRankItemScore.text = item.score
            val bundle = bundleOf("rankUserData" to item)
            itemView.setOnClickListener {
                it.findNavController().navigate(R.id.action_rank_to_other_plogging, bundle)
            }
        }

        private fun decorateTop3(rank: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (rank) {
                    0 -> {
                        itemRankBinding.ivRankItemProfile.strokeColor =
                            itemView.context.getColorStateList(R.color.red_500)
                        itemRankBinding.ivRankItemTop.setImageResource(R.drawable.ic_first)
                        itemRankBinding.clRankItem.backgroundTintList =
                            itemView.context.getColorStateList(R.color.red_100)
                        itemRankBinding.tvRankItemNumber.setTextColor(Color.WHITE)
                        itemRankBinding.ivRankItemTop.visibility = VISIBLE
                        itemRankBinding.cvRankItem.cardElevation = 0f
                    }
                    1 -> {
                        itemRankBinding.ivRankItemProfile.strokeColor =
                            itemView.context.getColorStateList(R.color.green_500)
                        itemRankBinding.ivRankItemTop.setImageResource(R.drawable.ic_second)
                        itemRankBinding.clRankItem.backgroundTintList =
                            itemView.context.getColorStateList(R.color.green_100)
                        itemRankBinding.tvRankItemNumber.setTextColor(Color.WHITE)
                        itemRankBinding.ivRankItemTop.visibility = VISIBLE
                        itemRankBinding.cvRankItem.cardElevation = 0f
                    }
                    2 -> {
                        itemRankBinding.ivRankItemProfile.strokeColor =
                            itemView.context.getColorStateList(R.color.yellow_800)
                        itemRankBinding.ivRankItemTop.setImageResource(R.drawable.ic_third)
                        itemRankBinding.clRankItem.backgroundTintList =
                            itemView.context.getColorStateList(R.color.yellow_100)
                        itemRankBinding.tvRankItemNumber.setTextColor(Color.WHITE)
                        itemRankBinding.ivRankItemTop.visibility = VISIBLE
                        itemRankBinding.cvRankItem.cardElevation = 0f
                    }
                    else -> {
                        itemRankBinding.ivRankItemProfile.strokeColor =
                            itemView.context.getColorStateList(R.color.gray_500)
                        itemRankBinding.clRankItem.backgroundTintList =
                            itemView.context.getColorStateList(R.color.white)
                        itemRankBinding.tvRankItemNumber.setTextColor(Color.parseColor("#6e6e6e"))
                        itemRankBinding.ivRankItemTop.visibility = INVISIBLE
                        itemRankBinding.cvRankItem.cardElevation = 1f
                    }
                }
            }
        }
    }
}