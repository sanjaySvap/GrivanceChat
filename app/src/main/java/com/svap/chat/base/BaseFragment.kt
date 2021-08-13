package com.svap.chat.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.svap.chat.R
import com.svap.chat.utils.AppPreferencesHelper
import com.svap.chat.utils.extentions.gone
import com.svap.chat.utils.extentions.visible
import org.koin.android.ext.android.get

abstract class BaseFragment<D : ViewDataBinding>(private val resourceId: Int) : Fragment() {
    protected val mSharePresenter: AppPreferencesHelper = get()
    protected lateinit var mBinding: D
    protected var progressBar: ProgressBar?= null
    protected var noDataMessage: LinearLayout? = null
    protected var noDataMessageTv: TextView? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<D>(
            inflater, resourceId, container, false
    ).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    open fun initView() {}
    open fun initListener() {}
    open fun initObserver() {}
    open fun initAdapter() {}
    open fun initClicks() {}
    open fun onConnect() {}
    open fun onConnectionError(msg: String) {}

    protected fun onErrorReturn(message: String?) {
        message?.let {
            noDataMessage?.visible()
            noDataMessageTv?.text = message
        }?: kotlin.run {
            noDataMessage?.gone()
        }
    }

}
