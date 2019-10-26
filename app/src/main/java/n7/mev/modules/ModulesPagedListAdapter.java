package n7.mev.modules;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Set;

import n7.mev.R;
import n7.mev.databinding.ModulesItemBinding;

public class ModulesPagedListAdapter extends RecyclerView.Adapter<ModulesPagedListAdapter.ViewHolder> {

    private ModulesFragment fragment;
    private LayoutInflater inflater;
    private LinkedList<String> list = new LinkedList<>();

    ModulesPagedListAdapter(ModulesFragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        ModulesItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.modules_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = list.get(position);
        holder.bindTo(name);
    }

    public void addAll(Set<String> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ModulesItemBinding binding;

        ViewHolder(ModulesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(String name) {
            binding.setFragment(fragment);
            binding.setName(name);
            binding.executePendingBindings();

            setDrawableFromResources(name);
        }

        private void setDrawableFromResources(String name) {
            int resourceId = binding.ivModulesItemIcon.getResources().getIdentifier(name, "drawable", binding.ivModulesItemIcon.getContext().getPackageName());
            if (resourceId != 0)
                Picasso.get().load(resourceId).into(binding.ivModulesItemIcon);
//            binding.ivModulesItemIcon.setImageResource(resourceId);
        }

    }


}
