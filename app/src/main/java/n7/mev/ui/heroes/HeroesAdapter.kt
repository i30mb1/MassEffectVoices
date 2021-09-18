package n7.mev.ui.heroes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.mev.databinding.ModulesItemBinding
import n7.mev.ui.heroes.vo.HeroVO

class HeroesAdapter constructor(
    private val inflater: LayoutInflater,
    private val onHeroClickListener: (model: HeroVO) -> Unit
) : ListAdapter<HeroVO, HeroesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(inflater, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), onHeroClickListener)

    override fun onViewDetachedFromWindow(holder: ViewHolder) = holder.unbind()

    class ViewHolder private constructor(
        var binding: ModulesItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: HeroVO, onHeroClickListener: (model: HeroVO) -> Unit) {
            binding.tvName.text = model.name
            binding.root.setOnClickListener { onHeroClickListener(model) }
        }

        fun unbind() {
            binding.root.setOnClickListener(null)
        }

        companion object {
            fun from(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
                val binding = ModulesItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HeroVO>() {
        override fun areItemsTheSame(oldItem: HeroVO, newItem: HeroVO) = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: HeroVO, newItem: HeroVO) = oldItem == newItem
    }

}

