package com.itheamc.parlaymanager.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.itheamc.parlaymanager.utils.MathUtils;

import java.util.Objects;

import static com.itheamc.parlaymanager.utils.MathUtils.calcDecimalOdds;
import static com.itheamc.parlaymanager.utils.MathUtils.calcFractionalOdds;

@Entity(tableName = "leg")
public class Leg {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int _id;

    @ColumnInfo(name = "team_name")
    private final String _name;

    @ColumnInfo(name = "bet_amount")
    private double _bet_amount;

    @ColumnInfo(name = "american_odds")
    private final int _american_odds;

    @ColumnInfo(name = "selection_name")
    private final String _selection_name;

    @ColumnInfo(name = "decimal_odds")
    private double _decimal_odds;

    @ColumnInfo(name = "fractional_odds")
    private String _fractional_odds;

    @ColumnInfo(name = "win_chance")
    private double _win_chance;


    // Constructor
    public Leg(String _name, double _bet_amount, int _american_odds, String selection_name) {
        this._name = _name;
        this._bet_amount = _bet_amount;
        this._american_odds = _american_odds;
        this._selection_name = selection_name;
        set_decimal_odds(calcDecimalOdds(_american_odds));
        set_fractional_odds(calcFractionalOdds(_american_odds));
        set_win_chance(MathUtils.calcWinChance(_american_odds));
    }


    // Getters and Setters
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public double get_bet_amount() {
        return _bet_amount;
    }

    public void set_bet_amount(double _bet_amount) {
        this._bet_amount = _bet_amount;
    }

    public int get_american_odds() {
        return _american_odds;
    }

    public String get_selection_name() {
        return _selection_name;
    }

    public double get_decimal_odds() {
        return _decimal_odds;
    }

    public void set_decimal_odds(double _decimal_odds) {
        this._decimal_odds = _decimal_odds;
    }

    public String get_fractional_odds() {
        return _fractional_odds;
    }

    public void set_fractional_odds(String _fractional_odds) {
        this._fractional_odds = _fractional_odds;
    }

    public double get_win_chance() {
        return _win_chance;
    }

    public void set_win_chance(double _win_chance) {
        this._win_chance = _win_chance;
    }

    // Overriding to string method
    @NonNull
    @Override
    public String toString() {
        return "Leg{" +
                "_id=" + _id +
                ", _name='" + _name + '\'' +
                ", _bet_amount=" + _bet_amount +
                ", _american_odds=" + _american_odds +
                ", _selection_name='" + _selection_name + '\'' +
                ", _decimal_odds=" + _decimal_odds +
                ", _fractional_odds='" + _fractional_odds + '\'' +
                ", _win_chance=" + _win_chance +
                '}';
    }


    // Comparator method for sorting legs by win chance in descending order
    public double descendOrderByWinChance(Leg leg) {
        double winning_chance = ((Leg) leg).get_win_chance();
        return  (winning_chance-this._win_chance);
    }

    // Comparator method for sorting legs by win chance in ascending order
    public double ascendOrderByWinChance(Leg leg) {
        double winning_chance = ((Leg) leg).get_win_chance();
        /* For Ascending order i.e lower to higher*/
        return this._win_chance-winning_chance;

    }



    // Overriding equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return get_id() == leg.get_id() &&
                Double.compare(leg.get_bet_amount(), get_bet_amount()) == 0 &&
                get_american_odds() == leg.get_american_odds() &&
                Double.compare(leg.get_decimal_odds(), get_decimal_odds()) == 0 &&
                Double.compare(leg.get_win_chance(), get_win_chance()) == 0 &&
                get_name().equals(leg.get_name()) &&
                get_selection_name().equals(leg.get_selection_name()) &&
                get_fractional_odds().equals(leg.get_fractional_odds());
    }


    // DiffUtil.ItemCallback
    public static DiffUtil.ItemCallback<Leg> legItemCallback = new DiffUtil.ItemCallback<Leg>() {
        @Override
        public boolean areItemsTheSame(@NonNull Leg oldItem, @NonNull Leg newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Leg oldItem, @NonNull Leg newItem) {
            return newItem.get_id() == oldItem.get_id() &&
                    newItem.get_name().equals(oldItem.get_name()) &&
                    newItem.get_fractional_odds().equals(oldItem.get_fractional_odds()) &&
                    newItem.get_selection_name().equals(oldItem.get_selection_name()) &&
                    newItem.get_bet_amount() == oldItem.get_bet_amount() &&
                    newItem.get_american_odds() == oldItem.get_american_odds() &&
                    newItem.get_decimal_odds() == oldItem.get_decimal_odds() &&
                    newItem.get_win_chance() == oldItem.get_win_chance();
        }
    };
}
