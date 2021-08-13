package com.svap.chat.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.svap.chat.R
import com.svap.chat.ui.dialog.DialogAlert
import com.svap.chat.ui.dialog.DialogProgress
import com.svap.chat.utils.app_enum.ErrorType
import com.svap.chat.utils.app_enum.ProgressAction
import com.svap.chat.utils.extentions.gone
import com.svap.chat.utils.extentions.visible
import org.koin.androidx.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass

abstract class BaseVmActivity<D : ViewDataBinding, V : BaseViewModel>(
        private val resourceId: Int,
        private val viewModelClass: KClass<V>
) : BaseActivity<D>(resourceId) {

    protected val mViewModel: V by lazy { getViewModel(clazz = viewModelClass) }
    private var progressDialog: DialogProgress? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeError()
    }

    private fun observeError() {
        mViewModel.progressHandler.observe(this, Observer{
            when (it ?: ProgressAction.NONE) {
                ProgressAction.PROGRESS_DIALOG -> {
                    progressDialog = DialogProgress(this, it.message ?: getString(R.string.loading))
                    progressDialog?.show()
                }
                ProgressAction.PROGRESS_BAR -> {
                    progressBar?.visible()
                }
                ProgressAction.DISMISS -> {
                    progressDialog?.dismiss()
                    progressBar?.gone()
                }
                ProgressAction.NONE -> {}
            }
        })

        mViewModel.errorMessage.observe(this,Observer {
            when (it.errorType) {
                ErrorType.DIALOG -> {
                    DialogAlert(
                        view = mBinding.root,
                        message = it.message,
                        actionText = "OK",
                    ).show()
                }
                ErrorType.MESSAGE ->{}
                ErrorType.TOAST -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                ErrorType.NONE -> {}
            }
        })
    }

}
