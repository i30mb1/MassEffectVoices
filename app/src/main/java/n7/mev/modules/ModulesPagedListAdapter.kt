package n7.mev.modules

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import n7.mev.R
import n7.mev.databinding.ModulesItemBinding
import java.util.*

class ModulesPagedListAdapter internal constructor(private val fragment: ModulesFragment) : RecyclerView.Adapter<ModulesPagedListAdapter.ViewHolder>() {
    private var inflater: LayoutInflater? = null
    private val list = LinkedList<String?>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (inflater == null) inflater = LayoutInflater.from(parent.context)
        val binding: ModulesItemBinding = DataBindingUtil.inflate(inflater!!, R.layout.modules_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = list[position]
        holder.bindTo(name)
    }

    fun addAll(newList: Set<String?>?) {
        list.clear()
        list.addAll(newList!!)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(var binding: ModulesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(name: String?) {
            binding.fragment = fragment
            binding.name = name
            binding.executePendingBindings()
            setDrawableFromResources(name)
        }

        private fun setDrawableFromResources(name: String?) {
            val resourceId = binding.ivModulesItemIcon.resources.getIdentifier(name, "drawable", binding.ivModulesItemIcon.context.packageName)
            if (resourceId != 0) Picasso.get().load(resourceId).into(binding.ivModulesItemIcon)
            //            binding.ivModulesItemIcon.setImageResource(resourceId);
        }

    }

}