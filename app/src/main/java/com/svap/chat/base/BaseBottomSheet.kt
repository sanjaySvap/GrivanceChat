package com.svap.chat.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.svap.chat.R

abstract class BaseBottomSheet<D:ViewDataBinding>(val mCallBack: OnItemClickListener<*>? = null) :
    BottomSheetDialogFragment() {
    protected lateinit var mBinding:D
    interface OnItemClickListener<T> {
        fun onItemClick(value: T?)
    }

    protected var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,getLayoutResources(),container,false)
        mView = mBinding.root
        return mBinding.root
    }

    abstract fun getLayoutResources(): Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dg = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dg.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundResource(0)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dg
    }

}