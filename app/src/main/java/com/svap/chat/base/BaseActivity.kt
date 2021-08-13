package com.svap.chat.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.svap.chat.R
import com.svap.chat.utils.AppPreferencesHelper
import com.svap.chat.utils.extentions.gone
import com.svap.chat.utils.extentions.visible
import org.koin.android.ext.android.inject

abstract class BaseActivity<D : ViewDataBinding>(private val resourceId: Int) : AppCompatActivity() {
    protected val mSharePresenter: AppPreferencesHelper by inject()
    protected lateinit var mBinding: D

    var mLoadingNextPage = false
    protected var progressBar: ProgressBar?= null
    protected var noDataMessage: LinearLayout? = null
    protected var noDataMessageTv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, resourceId)
        mBinding.lifecycleOwner = this
        supportActionBar?.hide()

        initCommonView()
        initView()
        initObserver()
        initListener()
        initAdapter()
        initClicks()
    }

    private fun initCommonView() {
        progressBar = mBinding.root.findViewById(R.id.progress_bar)
        noDataMessage = mBinding.root.findViewById(R.id.no_data_root)
        noDataMessageTv = mBinding.root.findViewById(R.id.tv_no_data)

        mBinding.root.findViewById<ImageView>(R.id.back_btn)?.setOnClickListener {
            onBackPressed()
        }
    }

    open fun initView() {}
    open fun initListener() {}
    open fun initObserver() {}
    open fun initAdapter() {}
    open fun initClicks() {}

    protected fun onErrorReturn(message: String?) {
        message?.let {
            noDataMessageTv?.visible()
            noDataMessageTv?.text = message
        }?: kotlin.run {
            noDataMessageTv?.gone()
        }
    }
}
