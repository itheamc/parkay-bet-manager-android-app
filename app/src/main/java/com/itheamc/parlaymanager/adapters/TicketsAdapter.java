package com.itheamc.parlaymanager.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.itheamc.parlaymanager.callbacks.ItemsClickListener;
import com.itheamc.parlaymanager.databinding.TicketViewBinding;
import com.itheamc.parlaymanager.models.Leg;
import com.itheamc.parlaymanager.models.Ticket;
import com.itheamc.parlaymanager.utils.MathUtils;

import java.util.List;

import static com.itheamc.parlaymanager.models.Ticket.ticketItemCallback;

public class TicketsAdapter extends ListAdapter<Ticket, TicketsAdapter.TicketViewHolder> {
    private final ItemsClickListener itemsClickListener;


    public TicketsAdapter(@NonNull ItemsClickListener itemsClickListener) {
        super(ticketItemCallback);
        this.itemsClickListener = itemsClickListener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TicketViewBinding ticketViewBinding = TicketViewBinding.inflate(inflater, parent, false);
        return new TicketViewHolder(ticketViewBinding, itemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = getItem(position);
        List<Leg> legs = ticket.getLegList();
        holder.viewBinding.setTicket(ticket);
        holder.legsAdapter.submitList(ticket.getLegList());
        holder.viewBinding.setOdds(MathUtils.calcTrueOdds(legs));
        holder.viewBinding.setEarning(MathUtils.calcCombinedEarning(legs, ticket.getBet_amount()));
        holder.viewBinding.setProfit(MathUtils.calcCombinedProfits(legs, ticket.getBet_amount()));
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final TicketViewBinding viewBinding;
        private final LegsAdapter legsAdapter;

        public TicketViewHolder(@NonNull TicketViewBinding ticketViewBinding, ItemsClickListener itemsClickListener) {
            super(ticketViewBinding.getRoot());

            this.viewBinding = ticketViewBinding;
            legsAdapter = new LegsAdapter(itemsClickListener);
            viewBinding.recyclerView.setAdapter(legsAdapter);
        }
    }
}
