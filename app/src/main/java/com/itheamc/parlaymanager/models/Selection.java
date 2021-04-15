package com.itheamc.parlaymanager.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

public class Selection {
    private int id;
    private String title;
    private double bet_amount;
    private List<Leg> legs;

    // Constructor
    public Selection(int id, String title, double bet_amount, List<Leg> legs) {
        this.id = id;
        this.title = title;
        this.bet_amount = bet_amount;
        this.legs = legs;
    }


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBet_amount() {
        return bet_amount;
    }

    public void setBet_amount(double bet_amount) {
        this.bet_amount = bet_amount;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }


    // Overriding toString() method
    @NonNull
    @Override
    public String toString() {
        return "Selection{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", bet_amount=" + bet_amount +
                ", legs=" + legs +
                '}';
    }

    // Overriding equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Selection selection = (Selection) o;
        return getId() == selection.getId() &&
                Double.compare(selection.getBet_amount(), getBet_amount()) == 0 &&
                getTitle().equals(selection.getTitle()) &&
                getLegs().equals(selection.getLegs());
    }


    // Creating DiffUtil.ItemCallback
    public static DiffUtil.ItemCallback<Selection> selectionItemCallback = new DiffUtil.ItemCallback<Selection>() {
        @Override
        public boolean areItemsTheSame(@NonNull Selection oldItem, @NonNull Selection newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Selection oldItem, @NonNull Selection newItem) {
            return oldItem.getId() == newItem.getId() &&
                    oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getBet_amount() == newItem.getBet_amount() &&
                    oldItem.getLegs().equals(newItem.getLegs());
        }
    };
}
