package com.plogging.ecorun.ui.setting.profile

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentCameraBinding
import com.plogging.ecorun.ui.running.save.CameraFragment.Companion.TAG
import com.plogging.ecorun.util.extension.*
import io.reactivex.rxkotlin.addTo
import java.io.File

class ProfileCameraFragment : BaseFragment<FragmentCameraBinding, ProfileViewModel>() {
    private lateinit var imageCaptureCallback: ImageCapture.OnImageCapturedCallback
    override fun getViewBinding() = FragmentCameraBinding.inflate(layoutInflater)
    private lateinit var orientationEventListener: OrientationEventListener
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override val viewModel: ProfileViewModel by viewModels()
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var bitmap: Bitmap
    private var orientation = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOrientationEventListener()
        initCaptureCallbackObject()
        permissionCheck()
        getOutputDirectory()
    }

    // 기울기 감지
    private fun initOrientationEventListener() {
        orientationEventListener = object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(ot: Int) {
                orientation = ot
            }
        }
    }

    // 사진 찍는 버튼을 눌렀을 때
    private fun initCaptureCallbackObject() {
        imageCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                showSavingButton()
                bitmap = bitmap.flipBitmap(cameraSelector) // 전면 카메라의 경우 좌우 반전
                bitmap = bitmap.imageCrop( // image view에 맞게 image crop eight 와 width는 반대이다.
                    binding.pvCamera.height.toFloat(),
                    binding.pvCamera.width.toFloat()
                )
                bitmap = bitmap.rotate(orientation) // 기기 기울기에 따라 이미지 회전
                orientationEventListener.disable() // 기울기 구하는 이벤트 종료
                Glide.with(requireContext()).load(bitmap).into(binding.ivCameraCapturePreview)
            }
        }
    }

    private fun permissionCheck() {
        if (!allGranted()) registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.all { isGranted -> isGranted == true }) startCamera()
            else requireContext().toast(getString(R.string.need_camera_permission))
        }.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        else startCamera()

    }

    // 사진이 저장될 파일 만들기
    private fun getOutputDirectory() {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        outputDirectory = if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    private fun savePhoto() {
        bitmap.saveImageIn(requireContext().contentResolver)
            .composeSchedulers()
            .subscribe({
                findNavController().previousBackStackEntry?.savedStateHandle?.set("uri", it)
                findNavController().popBackStack()
                binding.pgCamera.visibility = INVISIBLE
            }, {})
            .addTo(disposables)
    }

    @SuppressLint("RestrictedApi")
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        orientationEventListener.enable() // 기울기 구하는 이벤트 시작
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            imageCaptureCallback
        )
    }

    private fun showSavingButton() {
        binding.ivCameraCapturePreview.visibility = VISIBLE
        binding.btnCameraCapture.visibility = INVISIBLE
        binding.btnCameraChange.visibility = INVISIBLE
        binding.btnCameraSave.visibility = VISIBLE
        binding.pvCamera.visibility = INVISIBLE
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.pvCamera.surfaceProvider) }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun clickListener() {
        binding.ivCameraArrow.setOnClickListener { findNavController().popBackStack() }
        binding.btnCameraCapture.setOnClickListener { takePhoto() }
        binding.btnCameraSave.setOnClickListener {
            binding.pgCamera.visibility = VISIBLE
            savePhoto()
        }
        binding.btnCameraChange.setOnClickListener {
            cameraSelector = when (cameraSelector) {
                CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
                CameraSelector.DEFAULT_FRONT_CAMERA -> CameraSelector.DEFAULT_BACK_CAMERA
                else -> CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
    }

    private fun allGranted() =
        requireContext().isPermissionGranted(Manifest.permission.CAMERA) &&
                requireContext().isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}