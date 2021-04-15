package com.itheamc.parlaymanager.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.itheamc.parlaymanager.R;
import com.itheamc.parlaymanager.callbacks.ItemsClickListener;
import com.itheamc.parlaymanager.databinding.SelectionViewBinding;
import com.itheamc.parlaymanager.models.Leg;
import com.itheamc.parlaymanager.models.Selection;
import com.itheamc.parlaymanager.utils.Constraints;
import com.itheamc.parlaymanager.utils.MathUtils;

import java.util.List;

import static com.itheamc.parlaymanager.models.Selection.selectionItemCallback;

public class SelectionAdapter extends ListAdapter<Selection, SelectionAdapter.SelectionViewHolder> {
    private final ItemsClickListener itemsClickListener;

    public SelectionAdapter(@NonNull ItemsClickListener itemsClickListener) {
        super(selectionItemCallback);
        this.itemsClickListener = itemsClickListener;
    }

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SelectionViewBinding viewBinding = SelectionViewBinding.inflate(inflater, parent, false);
        return new SelectionViewHolder(viewBinding, itemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
        Selection selection = getItem(position);
        List<Leg> legs = selection.getLegs();
        holder.selectionViewBinding.setSelection(selection);
        holder.selectionViewBinding.setTrueodds(MathUtils.calcTrueOdds(legs));
        holder.selectionViewBinding.setEarning(MathUtils.calcCombinedEarning(legs, selection.getBet_amount()));
        holder.selectionViewBinding.setProfit(MathUtils.calcCombinedProfits(legs, selection.getBet_amount()));
        holder.legsAdapter.submitList(legs);
    }

    public static class SelectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final SelectionViewBinding selectionViewBinding;
        private final ItemsClickListener clickListener;
        private LegsAdapter legsAdapter;

        public SelectionViewHolder(@NonNull SelectionViewBinding viewBinding, ItemsClickListener clickListener) {
            super(viewBinding.getRoot());
            this.selectionViewBinding = viewBinding;
            this.clickListener = clickListener;
            legsAdapter = new LegsAdapter(clickListener);
            selectionViewBinding.recyclerView.setAdapter(legsAdapter);
            selectionViewBinding.optionMenu.setOnClickListener(this);

            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(selectionViewBinding.recyclerView);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.selection_menu);
            popupMenu.setForceShowIcon(true);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.selection_menu_tickets) {
                clickListener.onMenuClick(getAdapterPosition(), Constraints.CREATE_TICKETS);
                return true;
            } else if (id == R.id.selection_menu_add) {
                clickListener.onMenuClick(getAdapterPosition(), Constraints.ADD_LEG_IN_SELECTION);
                return true;
            } else if (id == R.id.selection_menu_delete) {
                clickListener.onMenuClick(getAdapterPosition(), Constraints.DELETE_SELECTION);
                return true;
            } else if (id == R.id.selection_menu_edit) {
                clickListener.onMenuClick(getAdapterPosition(), Constraints.EDIT_SELECTION_WAGER);
                return true;
            } else {
                return false;
            }

        }
    }
}
