package com.ivanmorgillo.corsoandroid.teama.favourite

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teama.R

private const val BACKGROUND_CORNER_OFFSET = 20
private const val ICON_MARGIN = 5

class SwipeToDeleteCallback(private val mAdapter: FavouriteAdapter, context: Context?, val view: View) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val icon: Drawable?
    private val background: ColorDrawable

    init {
        icon = ContextCompat.getDrawable(context!!,
            R.drawable.ic_swipe_delete)
        background = ColorDrawable(Color.RED)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        mAdapter.deleteItem(position, view)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / ICON_MARGIN
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        if (dX > 0) { // Swiping to the right
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom) // 220 left e right indietro
            background.setBounds(itemView.left, itemView.top,
                itemView.left + dX.toInt() + BACKGROUND_CORNER_OFFSET,
                itemView.bottom)
        } else if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(itemView.right + dX.toInt() - BACKGROUND_CORNER_OFFSET,
                itemView.top, itemView.right, itemView.bottom)
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
            // icon.setBounds(0, 0, 0, 0); // ADD THIS LINE
        }
        background.draw(c)
        icon.draw(c)
    }
}
