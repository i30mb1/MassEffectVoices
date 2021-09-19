package n7.mev.ui.heroes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.mev.databinding.ItemHeroesSmallBinding
import n7.mev.ui.heroes.vo.HeroVO

class HeroSmallViewHolder private constructor(
    private var binding: ItemHeroesSmallBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: HeroVO, onHeroClickListener: (model: HeroVO) -> Unit) {
        binding.tvName.text = model.name
        binding.ivPhoto.setImageResource(model.icon)
        binding.root.setOnClickListener { onHeroClickListener(model) }
    }

    fun unbind() {
        binding.root.setOnClickListener(null)
    }

    companion object {
        fun from(inflater: LayoutInflater, parent: ViewGroup): HeroSmallViewHolder {
            val binding = ItemHeroesSmallBinding.inflate(inflater, parent, false)
            return HeroSmallViewHolder(binding)
        }
    }
}