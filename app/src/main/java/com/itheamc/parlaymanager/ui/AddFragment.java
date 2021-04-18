package com.itheamc.parlaymanager.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.itheamc.parlaymanager.R;
import com.itheamc.parlaymanager.adapters.LegsAdapter;
import com.itheamc.parlaymanager.callbacks.ItemsClickListener;
import com.itheamc.parlaymanager.databinding.FragmentAddBinding;
import com.itheamc.parlaymanager.models.Leg;
import com.itheamc.parlaymanager.models.Selection;
import com.itheamc.parlaymanager.utils.NotifyUtils;
import com.itheamc.parlaymanager.viewmodel.ParlayViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddFragment extends Fragment implements ItemsClickListener {
    private static final String TAG = "AddFragment";
    private FragmentAddBinding addBinding;
    private ParlayViewModel viewModel;
    private NavController navController;

    private LegsAdapter legsAdapter;



    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addBinding = FragmentAddBinding.inflate(inflater, container, false);
        return addBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ParlayViewModel.class);
        legsAdapter = new LegsAdapter(this);
        addBinding.recyclerView.setAdapter(legsAdapter);


        viewModel.getLegsList().observe(getViewLifecycleOwner(), new Observer<List<Leg>>() {
            @Override
            public void onChanged(List<Leg> legs) {
                updateRecyclerView(legs);
            }
        });

        // Adding onClick Listener on the add button
        addBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLeg();
            }
        });

        // Banner Ads Code
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: " + initializationStatus.toString());
            }
        });

        AdView mAdView = addBinding.bannerAdView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void updateRecyclerView(List<Leg> legs) {
        List<Leg> legList = new ArrayList<>();

        for (Leg leg: legs) {
            if (addBinding.selectionName.getText().toString().trim().toLowerCase().equals(leg.get_selection_name().toLowerCase())) {
                legList.add(leg);
            }
        }

        legsAdapter.submitList(legList);

    }


    // Function to add the leg in the room database
    private void addLeg() {
        String selection_name = addBinding.selectionName.getText().toString().trim();
        String wager_amount = addBinding.inputWagerAmount.getText().toString().trim();
        String leg_title = addBinding.inputLegTitle.getText().toString().trim();
        String american_odds = addBinding.inputAmericanOdds.getText().toString().trim();

        try {
            if (TextUtils.isEmpty(selection_name) ||
                    TextUtils.isEmpty(wager_amount) ||
                    TextUtils.isEmpty(leg_title) ||
                    TextUtils.isEmpty(american_odds)) {
                NotifyUtils.showToast(getContext(), "Please provide all the details");
                return;
            }

            if (Integer.parseInt(american_odds) < 100 && Integer.parseInt(american_odds) > -100) {
                NotifyUtils.showToast(getContext(), "American odds can't be between 100 and -100");
                return;
            }

            if (Double.parseDouble(wager_amount) < 1) {
                NotifyUtils.showToast(getContext(), "Wager amount can't be less than 1.");
                return;
            }

            Leg leg = new Leg(
                    leg_title,
                    Integer.parseInt(wager_amount),
                    Integer.parseInt(american_odds),
                    selection_name
            );

            // Adding to room database through view model
            viewModel.insertLeg(leg);

            // Clearing the edit text
            addBinding.inputLegTitle.setText("");
            addBinding.inputAmericanOdds.setText("");

        } catch (Exception e) {
            NotifyUtils.showToast(getContext(),"You have entered invalid value in american odds");
        }
    }


    // Methods Overrided from the ItemsClickListener
    @Override
    public void onClick(int position) {

    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onOptionMenuClick(int position) {

    }

    @Override
    public void onMenuClick(int position, int type) {

    }

    @Override
    public void onSwipe(int selectionPosition, int legPosition) {

    }


}