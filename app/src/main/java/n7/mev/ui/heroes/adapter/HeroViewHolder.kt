package n7.mev.ui.heroes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.mev.databinding.ItemHeroesBinding
import n7.mev.ui.heroes.vo.HeroVO

class HeroViewHolder private constructor(
    private val binding: ItemHeroesBinding
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
        fun from(inflater: LayoutInflater, parent: ViewGroup): HeroViewHolder {
            val binding = ItemHeroesBinding.inflate(inflater, parent, false)
            return HeroViewHolder(binding)
        }
    }
}