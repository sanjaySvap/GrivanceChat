package com.svap.chat.utils.itemdecor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.svap.chat.utils.extentions.toPx

class RecyclerViewDecoration(
    private val start: Int = 0,
    private val top: Int = 0,
    private val end: Int = 0,
    private val bottom: Int = 0,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = start.toPx(parent.context).toInt()
        outRect.top = top.toPx(parent.context).toInt()
        outRect.right = end.toPx(parent.context).toInt()
        outRect.bottom = bottom.toPx(parent.context).toInt()
    }
}