package n7.mev.ui.sounds.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import n7.mev.ui.sounds.vo.SoundVO

class SoundsAdapter constructor(
    private val inflater: LayoutInflater,
    private val onSoundClickListener: (model: SoundVO) -> Unit,
) : ListAdapter<SoundVO, SoundViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SoundViewHolder.from(inflater, parent)

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(getItem(position), onSoundClickListener)
    }

    private class DiffCallback : DiffUtil.ItemCallback<SoundVO>() {
        override fun areItemsTheSame(oldItem: SoundVO, newItem: SoundVO) = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: SoundVO, newItem: SoundVO) = oldItem == newItem
    }

}