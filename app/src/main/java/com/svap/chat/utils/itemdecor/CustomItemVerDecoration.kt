package com.svap.chat.utils.itemdecor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.svap.chat.utils.extentions.toPx

class CustomItemVerDecoration(private val verticalSpaceHeight: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = verticalSpaceHeight.toPx
        }
        outRect.bottom = verticalSpaceHeight.toPx
        outRect.right = verticalSpaceHeight.toPx
        outRect.left = verticalSpaceHeight.toPx
    }
}