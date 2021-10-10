package n7.mev.ui.sounds.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.mev.databinding.ItemSoundBinding
import n7.mev.ui.sounds.vo.SoundVO

class SoundViewHolder private constructor(
    private val binding: ItemSoundBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: SoundVO, onSoundClickListener: (model: SoundVO) -> Unit) {
        binding.tvName.text = model.name
        binding.root.setOnClickListener { onSoundClickListener(model) }
    }

    fun unbind() {
        binding.root.setOnClickListener(null)
    }

    companion object {
        fun from(inflater: LayoutInflater, parent: ViewGroup): SoundViewHolder {
            val binding = ItemSoundBinding.inflate(inflater, parent, false)
            return SoundViewHolder(binding)
        }
    }

}