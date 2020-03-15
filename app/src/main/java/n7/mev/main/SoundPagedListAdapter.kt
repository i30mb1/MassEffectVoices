package n7.mev.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import n7.mev.R
import n7.mev.databinding.SoundItemBinding

class SoundPagedListAdapter internal constructor(private val mainViewModel: MainViewModel?) : PagedListAdapter<SoundModel, SoundPagedListAdapter.ViewHolder>(diffCallback) {
    private var inflater: LayoutInflater? = null
    private var maxPosition = 0
    private fun setAnimation(itemView: View, position: Int) {
        maxPosition = if (position > maxPosition) {
            position
        } else {
            return
        }
        //        itemView.setTranslationX(itemView.getX() + 400);
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        //        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, View.TRANSLATION_X, itemView.getX() + 400, 0);
        val animator = ObjectAnimator.ofFloat(itemView, View.ALPHA, 0f, 0.5f, 1f)
        animator.startDelay = 250
        animator.duration = 500
        animatorSet.playTogether(animator)
        animator.start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (inflater == null) inflater = LayoutInflater.from(parent.context)
        val binding: SoundItemBinding = DataBindingUtil.inflate(inflater!!, R.layout.sound_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        if (model != null) {
            holder.bindTo(model)
            //            setAnimation(holder.itemView, position);
        } else {
            holder.clear()
        }
    }

    inner class ViewHolder(var binding: SoundItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(model: SoundModel?) {
            binding.sound = model
            binding.model = mainViewModel
            binding.executePendingBindings()
        }

        fun clear() {
            binding.tvName.text = "..."
            binding.tvNumber.text = "..."
        }

    }

    companion object {
        private val diffCallback: DiffUtil.ItemCallback<SoundModel> = object : DiffUtil.ItemCallback<SoundModel>() {
            override fun areItemsTheSame(oldItem: SoundModel, newItem: SoundModel): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItem: SoundModel, newItem: SoundModel): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

}