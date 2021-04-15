package com.itheamc.parlaymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.itheamc.parlaymanager.callbacks.ItemsClickListener;
import com.itheamc.parlaymanager.databinding.LegViewBinding;
import com.itheamc.parlaymanager.models.Leg;
import com.itheamc.parlaymanager.utils.MathUtils;

import static com.itheamc.parlaymanager.models.Leg.legItemCallback;

public class LegsAdapter extends ListAdapter<Leg, LegsAdapter.LegsViewHolder> {
    private final ItemsClickListener itemsClickListener;

    public LegsAdapter(@NonNull ItemsClickListener itemsClickListener) {
        super(legItemCallback);
        this.itemsClickListener = itemsClickListener;
    }

    @NonNull
    @Override
    public LegsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LegViewBinding legViewBinding = LegViewBinding.inflate(inflater, parent, false);
        return new LegsViewHolder(legViewBinding, itemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LegsViewHolder holder, int position) {
        Leg leg = getItem(position);
        holder.legViewBinding.setLeg(leg);
        holder.legViewBinding.setPosition(position + 1);
        holder.legViewBinding.setEarning(MathUtils.calcEarning(leg.get_bet_amount(), leg.get_american_odds()));
        holder.legViewBinding.setProfit(MathUtils.calcProfit(leg.get_bet_amount(), leg.get_american_odds()));
    }

    public static class LegsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final LegViewBinding legViewBinding;
        private final ItemsClickListener itemsClickListener;

        public LegsViewHolder(@NonNull LegViewBinding legViewBinding, ItemsClickListener itemsClickListener) {
            super(legViewBinding.getRoot());
            this.legViewBinding = legViewBinding;
            this.itemsClickListener = itemsClickListener;
            this.legViewBinding.getRoot().setOnClickListener(this);
            this.legViewBinding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemsClickListener.onClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemsClickListener.onLongClick(getAdapterPosition());
            return true;
        }
    }
}
