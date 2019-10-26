package n7.mev.main;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import n7.mev.R;
import n7.mev.databinding.SoundItemBinding;

public class SoundPagedListAdapter extends PagedListAdapter<SoundModel, SoundPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<SoundModel> diffCallback = new DiffUtil.ItemCallback<SoundModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull SoundModel oldItem, @NonNull SoundModel newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SoundModel oldItem, @NonNull SoundModel newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };
    private LayoutInflater inflater;
    private MainViewModel mainViewModel;
    private int maxPosition = 0;

    SoundPagedListAdapter(MainViewModel mainViewModel) {
        super(diffCallback);
        this.mainViewModel = mainViewModel;
    }

    private void setAnimation(View itemView, int position) {
        if (position > maxPosition) {
            maxPosition = position;
        } else {
            return;
        }
//        itemView.setTranslationX(itemView.getX() + 400);
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
//        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, View.TRANSLATION_X, itemView.getX() + 400, 0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, View.ALPHA, 0.f, 0.5f, 1.f);
        animator.setStartDelay(250);
        animator.setDuration(500);
        animatorSet.playTogether(animator);
        animator.start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        SoundItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.sound_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoundModel model = getItem(position);
        if (model != null) {
            holder.bindTo(model);
//            setAnimation(holder.itemView, position);
        } else {
            holder.clear();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SoundItemBinding binding;

        ViewHolder(SoundItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(final SoundModel model) {
            binding.setSound(model);
            binding.setModel(mainViewModel);
            binding.executePendingBindings();
        }

        public void clear() {
            binding.tvName.setText("...");
            binding.tvNumber.setText("...");
        }

    }

}
