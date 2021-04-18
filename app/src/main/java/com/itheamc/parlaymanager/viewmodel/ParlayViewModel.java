package com.itheamc.parlaymanager.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.itheamc.parlaymanager.models.Leg;
import com.itheamc.parlaymanager.models.Selection;
import com.itheamc.parlaymanager.models.Ticket;
import com.itheamc.parlaymanager.repositories.LegsRepository;

import java.util.ArrayList;
import java.util.List;

public class ParlayViewModel extends AndroidViewModel {
    private static final String TAG = "ParlayViewModel";
    private final LiveData<List<Leg>> legsList;
    private final LegsRepository legsRepository;
    private List<Selection> selectionList;
    private List<Ticket> ticketList;

    public ParlayViewModel(@NonNull Application application) {
        super(application);
        legsRepository = new LegsRepository(application);
        legsList = legsRepository.getLegsData();
    }


    // Getter for LegsList
    public LiveData<List<Leg>> getLegsList() {
        return legsList;
    }


    // Function to insert leg
    public void insertLeg(Leg leg) {
        legsRepository.insertLeg(leg);
    }


    // Function to insert more than one legs at once
    public void insertLegs(Leg... legs) {
        legsRepository.insertLegs(legs);
    }


    // Function to delete the leg
    public void deleteLeg(Leg leg) {
        legsRepository.deleteLeg(leg);
    }

    // Function to delete the leg by id
    public void deleteLegById(int id) {
        legsRepository.deleteLegById(id);
    }

    // Function to update wager of the legs
    public void updateWager(double wager, int id) {
        legsRepository.updateWager(wager, id);
    }

    // Selection List
    public List<Selection> getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(List<Selection> selectionList) {
        Log.d(TAG, "setSelectionList: Added");
        this.selectionList = new ArrayList<>();
        this.selectionList = selectionList;
    }

    // Function to add selection
    public void addSelection(Selection selection) {
        if (selectionList == null) {
            selectionList = new ArrayList<>();
        }

        selectionList.add(selection);
    }


    // Function to Tickets list

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        if (this.ticketList == null) {
            this.ticketList = new ArrayList<>();
        }
        this.ticketList = ticketList;
    }
}
