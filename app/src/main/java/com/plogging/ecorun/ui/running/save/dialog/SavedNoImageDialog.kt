package com.plogging.ecorun.ui.running.save.dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import com.plogging.ecorun.ui.running.save.SaveViewModel
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class SavedNoImageDialog : BaseFragmentDialog<FragmentDialogBinding, SaveViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    private lateinit var saveViewModel: SaveViewModel
    override val viewModel: Nothing? = null
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.fragments[0].let {
            saveViewModel = ViewModelProvider(it).get(SaveViewModel::class.java)
        }
        initView()
    }

    override fun initView() {
        binding.tvDialogSubTitle.text = getString(R.string.dialog_save_sub_title)
        binding.tvDialogTitle.text = getString(R.string.dialog_save_title)
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_photo)
        binding.btnDialogSecond.isVisible = true
        binding.btnDialogFirst.isVisible = true
        binding.btnDialogOne.isVisible = false
    }

    override fun clickListener() {
        binding.btnDialogFirst.setOnClickListener { dismiss() }
        binding.btnDialogSecond.setOnClickListener { setDefaultImageBody() }
    }

    private fun setDefaultImageBody() {
        val scale = resources.displayMetrics.density
        val trashNum = arguments?.get(getString(R.string.trash_num)) as Int
        val distance =
            (arguments?.get(getString(R.string.distance)) as Float).meterToKilometer().toFloat()
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_default_plogging)
            .copy(Bitmap.Config.ARGB_8888, true)
        bitmap =
            Bitmap.createScaledBitmap(bitmap, bitmap.width * 2, bitmap.height * 2, false)
        Canvas(bitmap).apply { // bitmap이 Canvas에 Wrapping되면 bitmap은 immutable
            drawDistance(bitmap, scale, distance, R.drawable.ic_mark_running, resources)
            drawTrash(bitmap, scale, trashNum, R.drawable.ic_mark_trash, resources)
            drawMark(bitmap, R.drawable.ic_plogging_mark, resources)
            drawDate(bitmap, scale)
        }
        setImageBody()
    }

    private fun setImageBody() {
        saveBitmapToMediaStore(bitmap, requireContext().contentResolver)
            .subscribeOn(Schedulers.io())
            .flatMap {
                uriToRequestBody(it, requireContext().contentResolver, getString(R.string.plogging_param))
            }
            .subscribe({
                saveViewModel.imageBody = it
                saveViewModel.savePlogging() }, {})
            .addTo(disposables)
    }
}