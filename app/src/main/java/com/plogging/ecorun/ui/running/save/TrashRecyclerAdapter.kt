package com.plogging.ecorun.ui.running.save

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plogging.ecorun.util.constant.Constant.CAN
import com.plogging.ecorun.util.constant.Constant.EXTRA
import com.plogging.ecorun.util.constant.Constant.GLASSES
import com.plogging.ecorun.util.constant.Constant.PAPER
import com.plogging.ecorun.util.constant.Constant.PLASTIC
import com.plogging.ecorun.util.constant.Constant.VINYL
import com.plogging.ecorun.data.model.Trash
import com.plogging.ecorun.databinding.ItemTrashBinding
import com.plogging.ecorun.util.recycler.DiffCallback

class TrashRecyclerAdapter : ListAdapter<Trash, TrashRecyclerAdapter.TrashTypeViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrashTypeViewHolder = TrashTypeViewHolder(
        ItemTrashBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: TrashTypeViewHolder,
        position: Int
    ) = holder.bind(getItem(position), position)

    @SuppressLint("SetTextI18n")
    inner class TrashTypeViewHolder(private val itemViewBinding: ItemTrashBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun bind(item: Trash, position: Int) {
            val trashTypeToString = when (item.trashType) {
                0 -> VINYL
                1 -> GLASSES
                2 -> PAPER
                3 -> PLASTIC
                4 -> CAN
                else -> EXTRA
            }
            itemViewBinding.tvSaveItemTrash.text = trashTypeToString
            itemViewBinding.tvSaveItemCount.text = "${item.pickCount}개"
            if (position == itemCount - 1) itemViewBinding.vSaveItemLine
                .setBackgroundColor(Color.parseColor("#37d5ab"))
        }
    }
}