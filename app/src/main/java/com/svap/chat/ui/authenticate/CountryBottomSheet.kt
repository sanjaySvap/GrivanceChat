package com.svap.chat.ui.authenticate

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.svap.chat.R
import com.svap.chat.base.BaseBottomSheet
import com.svap.chat.base.BaseCallBack
import com.svap.chat.databinding.BottomCountryListBinding
import com.svap.chat.ui.authenticate.adapter.CountryAdapter
import com.svap.chat.ui.authenticate.model.CountryModel
import com.svap.chat.utils.country.Country
import com.svap.chat.utils.extentions.visibilityFilter

class CountryBottomSheet(
    private val list: ArrayList<Country>,
    private val onSelect: (country: Country) -> Unit
) : BaseBottomSheet<BottomCountryListBinding>(),
    BaseCallBack<Country> {

    override fun getLayoutResources() = R.layout.bottom_country_list

    private val mAdapter:CountryAdapter by lazy { CountryAdapter(list, this@CountryBottomSheet) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.adapter = mAdapter

        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }

        mBinding.tvDone.setOnClickListener {
            dismiss()
          //  onSelect(list[ mAdapter.selected])
        }

        mBinding.inNoData.tvNoData.text = "No Data Found"
        mBinding.inNoData.noDataRoot.visibilityFilter {
            list.isEmpty()
        }

        mBinding.etSearch.doAfterTextChanged {
            val str = it.toString().trim()
            if(str.isNotEmpty()){
                val listF = list.filter { it.Name.contains(str,true) } as ArrayList
                mAdapter.mList = listF
                mAdapter.notifyDataSetChanged()
            }else{
                mAdapter.mList = list
                mAdapter.notifyDataSetChanged()
            }

            mBinding.inNoData.noDataRoot.visibilityFilter {
                mAdapter.mList.isEmpty()
            }
        }
    }

    override fun getCallbackItem(item: Country, position: Int, tag: String) {
        dismiss()
        mBinding.etSearch.setText("")
        onSelect(item)
    }
}