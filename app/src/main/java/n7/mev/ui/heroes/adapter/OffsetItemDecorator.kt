package n7.mev.ui.heroes.adapter

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecorator(
    topOffset: Int = 8,
    botOffset: Int = 8,
) : RecyclerView.ItemDecoration() {

    var isExtraPaddingBot = 0

    private val Int.toPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density.toInt())

    private val topOffset = topOffset.toPx
    private val botOffset = botOffset.toPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val childCount = parent.adapter?.itemCount ?: 0

        with(outRect) {
            if (position == 0) top = topOffset
            if (position == childCount - 1) bottom = botOffset + isExtraPaddingBot
        }

    }

}