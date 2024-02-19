package com.example.test9.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.test9.databinding.FragmentHomeBinding
import com.example.test9.presentation.base.BaseFragment
import com.example.test9.presentation.callback.OnTakePhotoListener
import java.io.ByteArrayOutputStream

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    OnTakePhotoListener {

    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>

    override fun bind() {
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    processSelectedImage(uri)
                }
            }
    }

    override fun bindViewActionListeners() {
        binding.addImageBtn.setOnClickListener {
            showBottomSheet()
        }
    }

    override fun bindObserves() {
    }

    private fun showBottomSheet() {
        val bottomSheet = BottomSheet().apply {
            setListener(object : BottomSheet.BottomSheetListener {
                override fun onOptionSelected(option: String) {
                    when (option) {
                        "TAKE_PICTURE" -> onTakePicture()
                        "CHOOSE_GALLERY" -> chooseFromGallery()
                    }
                }
            })
        }
        bottomSheet.show(childFragmentManager, "BottomSheetDialog")
        bottomSheet.onTakePhotoListener = this@HomeFragment
    }

    private fun chooseFromGallery() {
        galleryResultLauncher.launch("image/*")
    }

    override fun onTakePicture() {
        if (checkCameraPermission()) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap: Bitmap? = data?.getParcelableExtra("data")
            imageBitmap?.let {
                compressAndDisplayImage(it)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compressAndDisplayImage(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val compressedImage = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
        binding.image.setImageBitmap(compressedImage)
    }

    private fun processSelectedImage(uri: Uri) {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        compressAndDisplayImage(bitmap)
    }
}
