package com.example.medscape20.presentation.screens.auth.avatar

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton


class ImagePickerBottomSheet() : BottomSheetDialogFragment() {

    private var imageUri: Uri? = null

    // Launcher for gallery image selection
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = uri
                handleImageSelection()
            }
        }

    // Launcher for camera image capture
    private val openAppDetailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            imageUri?.let { uri ->
                imageUri = uri
                handleImageSelection()
            }
        }
    }

    private val takeCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openCamera()
            } else {
                //permission not given
                showPermissionRationaleDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_image_picker_layout,
            container,
            false
        ) // Inflate your layout

        view.findViewById<LinearLayout>(R.id.take_photo).setOnClickListener {
            takeCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        view.findViewById<LinearLayout>(R.id.choose_from_gallery).setOnClickListener {
            openGallery()
        }

        return view
    }


    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        imageUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        takePictureLauncher.launch(imageUri)
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun handleImageSelection() {
        //to send it back to avatar screen
        setFragmentResult("avatar", Bundle().apply {
            putString("avatar_uri", imageUri.toString())
        })
        dismiss()
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.camera_permission_needed))
            .setMessage(
                getString(R.string.this_app_need_camera_permission_for_capturing_photos_from_camera)
            )
            .setPositiveButton("OK") { _, _ ->
                //request the permission again
                takeCameraPermissionLauncher.launch(Manifest.permission.CAMERA)

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                //navigate back to previous screen as permission is not granted
                findNavController().navigateUp()

            }
            .setNeutralButton("App Settings") { dialog, _ ->
                //navigate to app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    //to tell it that we want details of this package i.e our app
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                openAppDetailsLauncher.launch(intent)

            }.create().show()
    }
}