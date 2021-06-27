package com.plogging.ecorun.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentSettingBinding
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {
    override fun getViewBinding() = FragmentSettingBinding.inflate(layoutInflater)
    private lateinit var getPhotoFromAlbum: ActivityResultLauncher<Intent>
    override val viewModel: SettingViewModel by viewModels()
    private var changedUserImage: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCameraImage()
        getAlbumImage()
        responseApi()
        initView()
    }

    private fun getAlbumImage() {
        getPhotoFromAlbum =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result.data?.data?.toBitmap(requireContext().contentResolver)
                    ?.saveImageIn(requireContext().contentResolver)
                    ?.composeSchedulers()
                    ?.subscribe({ changedProfile(result.data?.data!!) }, {})
                    ?.addTo(disposables)
            }
    }

    private fun initView() {
        binding.tvSettingUserNickname.text = SharedPreference.getUserName(requireContext())
        binding.ivSettingUserProfile.setImageURI(SharedPreference.getUserImage(requireContext()))
        binding.ivSettingCheck.visibility = INVISIBLE
    }

    private fun getCameraImage() {
        findNavController()
            .currentBackStackEntry?.savedStateHandle?.getLiveData<Uri>("uri")
            ?.observe(viewLifecycleOwner) { changedProfile(it) }
    }

    private fun changedProfile(uri: Uri?) {
        uriToRequestBody(uri, requireContext().contentResolver, getString(R.string.profile_img))
            .composeSchedulers()
            .subscribe({
                viewModel.profile = it
                binding.ivSettingUserProfile.setImageURI(uri)
                changedUserImage = uri
                viewModel.changeProfile()
            }, {})
            .addTo(disposables)
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                SharedPreference.setUserImage(requireContext(), changedUserImage)
                binding.ivSettingCheck.visibility = VISIBLE
            }
        }
    }

    private fun createDialog() {
        activity?.let {
            AlertDialog.Builder(it).setTitle(getString(R.string.alert_title_profile))
                .setItems(R.array.select_image) { _, which -> selectCameraOrAlbum(which) }
                .create()
                .show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun selectCameraOrAlbum(index: Int) {
        when (index) {
            0 -> findNavController().navigate(R.id.action_setting_to_profile_camera)
            1 -> with(Intent(Intent.ACTION_PICK)) {
                type = MediaStore.Images.Media.CONTENT_TYPE
                type = "image/*"
                getPhotoFromAlbum.launch(this)
            }
        }
    }

    override fun clickListener() {
        binding.tvSettingNickname.setOnClickListener { findNavController().navigate(R.id.action_setting_to_change_nickname) }
        binding.tvSettingWithdraw.setOnClickListener { findNavController().navigate(R.id.action_setting_to_withdraw) }
        binding.tvSettingSignOut.setOnClickListener { findNavController().navigate(R.id.action_setting_to_sign_out) }
        binding.ivSettingUserBack.setOnClickListener { findNavController().popBackStack() }
        binding.tvSettingPicture.setOnClickListener { createDialog() }
        binding.tvSettingPassword.setOnClickListener {
            if (SharedPreference.getUserEmail(requireContext()).isCustomType())
                findNavController().navigate(R.id.action_setting_to_change_password)
            else requireContext().toast(getString(R.string.cannot_change_password))
        }
    }
}