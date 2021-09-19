package n7.mev.ui.heroes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import n7.mev.ui.heroes.vo.HeroVO

class HeroesSmallAdapter constructor(
    private val inflater: LayoutInflater,
    private val onHeroClickListener: (model: HeroVO) -> Unit
) : ListAdapter<HeroVO, HeroSmallViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroSmallViewHolder = HeroSmallViewHolder.from(inflater, parent)

    override fun onBindViewHolder(holder: HeroSmallViewHolder, position: Int) = holder.bind(getItem(position), onHeroClickListener)

    override fun onViewDetachedFromWindow(holder: HeroSmallViewHolder) = holder.unbind()

    private class DiffCallback : DiffUtil.ItemCallback<HeroVO>() {
        override fun areItemsTheSame(oldItem: HeroVO, newItem: HeroVO) = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: HeroVO, newItem: HeroVO) = oldItem == newItem
    }

}

