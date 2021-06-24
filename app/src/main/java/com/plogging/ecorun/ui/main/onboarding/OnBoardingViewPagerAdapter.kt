package com.plogging.ecorun.ui.main.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plogging.ecorun.data.model.OnBoardView
import com.plogging.ecorun.databinding.ItemViewpagerOnBoardBinding
import com.plogging.ecorun.util.recycler.DiffCallback


class OnBoardingViewPagerAdapter :
    ListAdapter<OnBoardView, OnBoardingViewPagerAdapter.OnBoardingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OnBoardingViewHolder(
            ItemViewpagerOnBoardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OnBoardingViewHolder(private val itemViewBinding: ItemViewpagerOnBoardBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun bind(item: OnBoardView) {
            itemViewBinding.tvOnBoardingSubTitle.setText(item.subTitle)
            Glide.with(itemViewBinding.ivItemOnBoard).load(item.url)
                .into(itemViewBinding.ivItemOnBoard)
            itemViewBinding.tvOnBoardingTitle.setText(item.title)
        }
    }
}