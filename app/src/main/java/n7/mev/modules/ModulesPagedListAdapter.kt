package n7.mev.modules

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.mev.data.source.local.FeatureModule
import n7.mev.databinding.ModulesItemBinding
import n7.mev.ui.heroes.HeroesFragment

class ModulesPagedListAdapter constructor(
        private val fragment: HeroesFragment
) : ListAdapter<FeatureModule, ModulesPagedListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindTo(getItem(position), fragment)

    class ViewHolder private constructor(
            var binding: ModulesItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(model: FeatureModule, fragment: HeroesFragment) {

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ModulesItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class DiffCallback : DiffUtil.ItemCallback<FeatureModule>() {
    override fun areItemsTheSame(oldItem: FeatureModule, newItem: FeatureModule) = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FeatureModule, newItem: FeatureModule) = true
}