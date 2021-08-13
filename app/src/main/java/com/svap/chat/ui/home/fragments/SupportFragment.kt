package com.svap.chat.ui.home.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.svap.chat.R
import com.svap.chat.base.BaseFragment
import com.svap.chat.base.BaseVmFragment
import com.svap.chat.databinding.FragmentHomeBinding
import com.svap.chat.databinding.FragmentSupportBinding

class SupportFragment : BaseFragment<FragmentSupportBinding>(R.layout.fragment_support) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvLogin.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + getString(R.string.supportmail)))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            intent.putExtra(Intent.EXTRA_TEXT, "Extra Message")
            startActivity(intent)
        }

    }
}