package com.itheamc.parlaymanager.repositories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.itheamc.parlaymanager.models.Leg;

import java.util.List;

@Dao
public interface LegDao {
    @Query("SELECT * FROM leg")
    LiveData<List<Leg>> getAllLegs();

    @Query("SELECT * FROM leg WHERE _id LIKE :id")
    Leg findById(int id);

    @Insert
    void insertLeg(Leg leg);

    @Insert
    void insertLegs(Leg... legs);

    @Delete
    void deleteLeg(Leg leg);

    @Query("UPDATE leg SET bet_amount=:wager WHERE _id = :id")
    void updateWager(double wager, int id);

    @Query("DELETE FROM leg WHERE _id = :id")
    void deleteLegById(int id);
}
