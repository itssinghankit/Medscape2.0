package com.example.medscape20.presentation.screens.user.home.category

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopBottomSpaceItemDecoration(private val topSpace: Int, private val botSpace: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        when (position) {
            0 -> outRect.top = topSpace // Add top space to the first item
            itemCount - 1 -> outRect.bottom = botSpace // Add bottom space to the last item
        }
    }
}