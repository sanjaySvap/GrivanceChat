package com.svap.chat.ui.home.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Observer
import com.fantasy.utils.extentions.getAllCountry
import com.fantasy.utils.extentions.optionDialog
import com.svap.chat.R
import com.svap.chat.base.BaseVmFragment
import com.svap.chat.databinding.FragmentEditProfileBinding
import com.svap.chat.ui.authenticate.CountryBottomSheet
import com.svap.chat.ui.dialog.DialogAlert
import com.svap.chat.ui.home.viewModel.ProfileViewModel
import com.svap.chat.utils.app_enum.Constant
import com.svap.chat.utils.bindingAdapter.setImageUrlUri
import com.svap.chat.utils.bindingAdapter.setProfileImageUrl
import com.svap.chat.utils.country.Country
import com.svap.chat.utils.extentions.*
import com.svap.chat.utils.permission.PermissionHelper
import com.yalantis.ucrop.UCrop
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class EditProfileFragment : BaseVmFragment<FragmentEditProfileBinding, ProfileViewModel>(
        R.layout.fragment_edit_profile,
        ProfileViewModel::class
) {

    private var mIamgePath = ""
    private var mCountry: String = ""
    private var mDob: String = ""
    private val mAllCountriesList: ArrayList<Country> by lazy {
        requireActivity().getAllCountry(mSharePresenter.allCountry)
    }
    private val mSheet: CountryBottomSheet by lazy {
        CountryBottomSheet(
                mAllCountriesList
        ) {
            mBinding.tvCountry.text = it.Name
            mCountry = it.Name
        }
    }

    override fun initObserver() {
        super.initObserver()
        mViewModel.mUser.observe(viewLifecycleOwner, Observer {
            mSharePresenter.saveCurrentUser(it)
            initProfile()
        })
    }

    private fun initProfile() {
        mSharePresenter.getCurrentUser().run {
            mBinding.etFirstName.setText("$first_name $last_name")
            mBinding.etMobile.setText(mobile_no ?: "")
            mBinding.etEmail.setText(email ?: "")
            mBinding.tvCountry.text = residential_country
            mBinding.tvDob.text = dob ?: ""
            mCountry = residential_country
            mDob = dob
            // mBinding.checkCall.isChecked = Preference.contains("call", true)
            mBinding.checkCall.isChecked = Preference.split(",").any { it.equals("call",true) }
            setProfileImageUrl(mBinding.ivProfile, image_name)
        }
    }

    override fun initView() {
        super.initView()
        initProfile()
        mViewModel.getProfile()

        mBinding.etFirstName.filters = arrayOf(
                InputFilter { cs, _, _, _, _, _ ->
                    if (cs == "") {
                        return@InputFilter cs
                    }
                    if (cs.toString().matches("[a-zA-Z ]+".toRegex())) {
                        cs
                    } else ""
                }
        )
    }

    override fun initClicks() {
        super.initClicks()
        mBinding.ivCamera.setOnClickListener {
            requireActivity().optionDialog("How would you like to add an photo?",
                    primaryKey = "Gallery",
                    secondaryKey = "Camera",
                    primaryKeyAction = {
                        PermissionHelper(requireActivity()).checkPermissions(
                                arrayOf(
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                        ) {
                            if (it) {
                                pickFromGallery()
                            } else {
                                requireActivity().showToast("Storage permission required!!.")
                            }
                        }
                    },
                    secondaryKeyAction = {
                        PermissionHelper(requireActivity()).checkPermissions(
                                arrayOf(
                                        android.Manifest.permission.CAMERA
                                )
                        ) {
                            if (it) {
                                startActivityForResult(
                                        Intent.createChooser(
                                                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                                                "Capture Image"
                                        ), Constant.REQUEST_CAMERA
                                )
                            } else {
                                requireActivity().showToast("Camera permission required!!.")
                            }
                        }
                    })
        }

        mBinding.tvCountry.setOnClickListener {
            mSheet.show(childFragmentManager, "")
        }

        mBinding.btnSave.setOnClickListener {
            editProfile()
        }

        mBinding.tvDob.setOnClickListener {
            mBinding.tvDob.toDatePicker { mDob = it }
        }
    }

    private fun editProfile() {
        val names = mBinding.etFirstName.getString().split(" ")
        val email = mBinding.etEmail.getString()
        val mobile = mBinding.etMobile.getString()

        val firstName = names[0]
        val lastName = if (names.size > 1) {
            names.subList(1,names.size).joinToString("")
        } else
            ""
        if (firstName.isEmpty()) {
            mBinding.root.showSnackbar("Please Enter your first name")
            return
        }

        if (lastName.isEmpty()) {
            mBinding.root.showSnackbar("Please Enter your last name")
            return
        }

        if (email.isEmpty()) {
            requireActivity().showToast("Please Enter your Email ID")
            return
        }
        if (mobile.isEmpty()) {
            requireActivity().showToast("Please Enter your mobile number")
            return
        }

        val pref = if (mBinding.checkCall.isChecked) "Call" else ""
        val map = hashMapOf(
                "user_name" to mSharePresenter.getCurrentUser().user_name.toRequestBody(),
                "first_name" to firstName.toRequestBody(),
                "last_name" to lastName.toRequestBody(),
                "dob" to mDob.toRequestBody(),
                "residential_Country" to mCountry.toRequestBody(),
                "Preference" to pref.toRequestBody(),
        )
        Log.d("imagePath", "imagePath " + mIamgePath)
        /*if (mIamgePath == "" && !TextUtils.isEmpty(mSharePresenter.getCurrentUser().image_name)) {
            mIamgePath = mSharePresenter.imagePath
        }*/
        mViewModel.editProfile(
                map,
                mIamgePath
        ) {
            mSharePresenter.imagePath = mIamgePath
            DialogAlert(
                    view = mBinding.root,
                    message = it,
                    actionText = "OK",
            ).show()
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes =
                arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select"
                ), Constant.REQUEST_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            data?.let {
                val response = it.extras?.get("data") as Bitmap
                val destination = File(requireContext().cacheDir, "user_profile.jpg")
                if (!destination.exists()) destination.createNewFile()

                val outputStream = destination.outputStream()
                response.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                UCrop.of(Uri.fromFile(destination), Uri.fromFile(destination))
                        .withAspectRatio(27F, 40F)
                        .start(requireContext(), this)
            }
        }

        if (requestCode == Constant.REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                val destinationFileName = "${System.currentTimeMillis()}_user_profile.jpg"
                val file = File(requireContext().cacheDir, destinationFileName)
                if (!file.exists()) file.createNewFile()
                data?.data?.let { sUri ->
                    UCrop.of(sUri, Uri.fromFile(file))
                            .withAspectRatio(27F, 40F)
                            .start(requireActivity(), this)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                UCrop.getOutput(it)?.let { resultUri ->
                    mIamgePath = resultUri.getRealPath(requireContext())
                    setProfile(resultUri)
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
        }
    }

    private fun setProfile(resultUri: Uri) {
        setImageUrlUri(mBinding.ivProfile, resultUri)
    }

}