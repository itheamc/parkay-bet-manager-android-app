package com.itheamc.parlaymanager.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.itheamc.parlaymanager.R;
import com.itheamc.parlaymanager.adapters.SelectionAdapter;
import com.itheamc.parlaymanager.callbacks.CombinationCallback;
import com.itheamc.parlaymanager.callbacks.ItemsClickListener;
import com.itheamc.parlaymanager.databinding.AddLegDialogBinding;
import com.itheamc.parlaymanager.databinding.EditWagerDialogBinding;
import com.itheamc.parlaymanager.databinding.FragmentSelectionsBinding;
import com.itheamc.parlaymanager.databinding.TicketDialogBinding;
import com.itheamc.parlaymanager.models.Leg;
import com.itheamc.parlaymanager.models.Selection;
import com.itheamc.parlaymanager.models.Ticket;
import com.itheamc.parlaymanager.utils.CombinationUtils;
import com.itheamc.parlaymanager.utils.MathUtils;
import com.itheamc.parlaymanager.utils.NetworkUtil;
import com.itheamc.parlaymanager.utils.NotifyUtils;
import com.itheamc.parlaymanager.viewmodel.ParlayViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.itheamc.parlaymanager.utils.Constraints.ADD_LEG_IN_SELECTION;
import static com.itheamc.parlaymanager.utils.Constraints.CREATE_TICKETS;
import static com.itheamc.parlaymanager.utils.Constraints.DELETE_SELECTION;
import static com.itheamc.parlaymanager.utils.Constraints.EDIT_SELECTION_WAGER;


public class SelectionsFragment extends Fragment implements ItemsClickListener, CombinationCallback {
    private static final String TAG = "SelectionsFragment";
    private FragmentSelectionsBinding selectionsBinding;
    private NavController navController;
    private ParlayViewModel viewModel;
    private SelectionAdapter selectionAdapter;


    public SelectionsFragment() {
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
        selectionsBinding = FragmentSelectionsBinding.inflate(inflater, container, false);
        return selectionsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ParlayViewModel.class);

        selectionAdapter = new SelectionAdapter(this);
        selectionsBinding.selectionsRecyclerView.setAdapter(selectionAdapter);

        viewModel.getLegsList().observe(getViewLifecycleOwner(), new Observer<List<Leg>>() {
            @Override
            public void onChanged(List<Leg> legs) {
                generateSelectionsList(legs);
            }
        });

        selectionsBinding.addSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_selectionsFragment_to_addFragment);
            }
        });

        // Banner Ads Code
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: " + initializationStatus.toString());
            }
        });

        AdView mAdView = selectionsBinding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    // Function to generate selection list
    private void generateSelectionsList(List<Leg> legs) {
        List<String> selections = new ArrayList<>();
        List<Selection> selectionList = new ArrayList<>();
        // extracting the selection
        for (Leg leg : legs) {
            if (!selections.contains(leg.get_selection_name())) {
                selections.add(leg.get_selection_name());
                Log.d(TAG, "generateSelectionsList: selection");
            }
        }

        // adding items to the selection list
        for (int i = 0; i < selections.size(); i++) {
            List<Leg> tempLeg = new ArrayList<>();
            for (Leg leg : legs) {
                if (!selections.get(i).equals(leg.get_selection_name())) {
                    continue;
                }

                tempLeg.add(leg);
            }
            Selection selection = new Selection(
                    i + 1,
                    selections.get(i),
                    tempLeg.get(0).get_bet_amount(),
                    tempLeg
            );

            selectionList.add(selection);
        }

        if (selectionList.isEmpty()) {
            selectionAdapter.submitList(selectionList);
            return;
        }
        viewModel.setSelectionList(selectionList);
        selectionAdapter.submitList(viewModel.getSelectionList());
        Log.d(TAG, "generateSelectionsList: " + viewModel.getSelectionList().toString());
    }



    /*_______________Methods Implemented From ItemsClickListener___________________*/

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
        Selection selection = viewModel.getSelectionList().get(position);
        if (type == CREATE_TICKETS) {

            if (getContext() != null && NetworkUtil.isConnected(getContext())) {
                showTicketSizePopUp(selection);
            } else {
                NotifyUtils.showToast(getContext(), "No Network Connection");
            }
        } else if (type == ADD_LEG_IN_SELECTION) {
            showAddLegPopUp(selection);
        } else if (type == EDIT_SELECTION_WAGER) {
            showUpdateWagerPopUp(selection);
        } else if (type == DELETE_SELECTION) {
            deleteSelection(selection);
        } else {
            NotifyUtils.showToast(getContext(), "Undefined menu item clicked...");
        }
    }

    @Override
    public void onSwipe(int selectionPosition, int legPosition) {
        Selection selection = viewModel.getSelectionList().get(selectionPosition);
        Leg leg = selection.getLegs().get(legPosition);
        viewModel.deleteLegById(leg.get_id());
        Snackbar.make(selectionsBinding.addSelection, String.format("%s (%s) removed successfully", leg.get_name(), String.valueOf(leg.get_bet_amount())), Snackbar.LENGTH_LONG)
        .setAnchorView(selectionsBinding.addSelection)
        .setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.insertLeg(new Leg(
                        leg.get_name(),
                        leg.get_bet_amount(),
                        leg.get_american_odds(),
                        leg.get_selection_name()
                ));
                NotifyUtils.showToast(getContext(), String.format("%s (%s) re-added successfully", leg.get_name(), String.valueOf(leg.get_bet_amount())));
            }
        })
        .show();
    }



    // Function to show popup dialog to select the items size in ticket
    public void showTicketSizePopUp(Selection selection) {
        // add listener to button
        final Dialog dialog = new Dialog(getContext());   // Create custom dialog object
        TicketDialogBinding dialogBinding = TicketDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        NumberPicker numberPicker = dialogBinding.numberPicker;
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(selection.getLegs().size());
        AtomicInteger r = new AtomicInteger(numberPicker.getMinValue());

        dialogBinding.setCombination(MathUtils.calcCombination(selection.getLegs(), r.intValue()));

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            r.set(newVal);
            dialogBinding.setCombination(MathUtils.calcCombination(selection.getLegs(), newVal));
        });

        dialogBinding.createTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createTickets(selection, r.intValue());
            }
        });

    }


    // Function to create combinations
    private void createTickets(Selection selection, int r) {
        CombinationUtils.getInstance(this,
                Executors.newFixedThreadPool(4),
                HandlerCompat.createAsync(Looper.getMainLooper()))
                .generateCombination(selection.getLegs(), r);
    }

    /*________________Function Override from CombinationCallback_______________*/
    @Override
    public void onCombinationCreated(List<List<Integer>> lists) {
        Log.d(TAG, "onCombinationCreated: " + lists.toString());
        convertList(lists);
    }


    // Function to convert List<List<Integer>> to  List<List<Legs>>
    private void convertList(List<List<Integer>> integerList) {
        List<Ticket> ticketList = new ArrayList<>();
        double betAmount = 0;
        int count = 0;
        for (List<Integer> integers : integerList) {
            List<Leg> tempLegs = new ArrayList<>();
            for (Integer integer : integers) {
                for (Leg leg : Objects.requireNonNull(viewModel.getLegsList().getValue())) {
                    if (integer == leg.get_id()) {
                        betAmount = leg.get_bet_amount();
                        tempLegs.add(leg);
                    }
                }
            }
            count++;

            // Updating the bet amount of the legs
            double finalBet = MathUtils.roundUp(betAmount/integerList.size());
            List<Leg> finalLegs = new ArrayList<>();
            for (Leg leg: tempLegs) {
                finalLegs.add(new Leg(
                        leg.get_name(),
                        finalBet,
                        leg.get_american_odds(),
                        leg.get_selection_name()
                ));
            }
            // Adding tempLegs to the ticket list
            ticketList.add(new Ticket(
                    count,
                    finalBet,
                    finalLegs
            ));
        }

        viewModel.setTicketList(ticketList);
        navController.navigate(R.id.action_selectionsFragment_to_ticketsFragment);
    }


    // Function to create custom dialog to add leg in the selection
    public void showAddLegPopUp(Selection selection) {
        // add listener to button
        final Dialog dialog = new Dialog(getContext());   // Create custom dialog object
        AddLegDialogBinding dialogBinding = AddLegDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        EditText name_et = dialogBinding.legTitle;
        EditText american_odds_et = dialogBinding.americanOdds;

        dialogBinding.addSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_et.getText().toString().trim();
                String american_odds = american_odds_et.getText().toString().trim();

                try {
                    if (name.isEmpty() || american_odds.isEmpty()) {
                        NotifyUtils.showToast(getContext(), "Please fill all the details");
                        return;
                    }

                    if (Integer.parseInt(american_odds) < 100 && Integer.parseInt(american_odds) > -100) {
                        NotifyUtils.showToast(getContext(), "American odds can't be between 100 and -100");
                        return;
                    }

                    // Getting some info about the selection
                    // on which leg is going to added
                    String selection_title = selection.getTitle();
                    double wager_amount = selection.getBet_amount();

                    // Creating new leg to add to the list
                    Leg leg = new Leg(
                            name,
                            wager_amount,
                            Integer.parseInt(american_odds),
                            selection_title
                    );

                    // Finally adding leg to the room database
                    viewModel.insertLeg(leg);
                    NotifyUtils.showToast(getContext(), "Added Successfully");
                    dialog.dismiss();
                } catch (Exception e) {
                    NotifyUtils.showToast(getContext(), "You have entered invalid value in american odds.");
                    Log.e(TAG, "onClick: ", e.getCause());
                }
            }
        });
    }


    // Function to create custom dialog to edit wager amount
    public void showUpdateWagerPopUp(Selection selection) {
        // add listener to button
        final Dialog dialog = new Dialog(getContext());   // Create custom dialog object
        EditWagerDialogBinding dialogBinding = EditWagerDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        EditText wager_et = dialogBinding.wagerEditText;

        dialogBinding.editWagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wager = wager_et.getText().toString().trim();
                if (wager.isEmpty() || Double.parseDouble(wager) < 0) {
                    NotifyUtils.showToast(getContext(), "Please input wager amount");
                    return;
                }

                if (Double.parseDouble(wager) < 1) {
                    NotifyUtils.showToast(getContext(), "Wager amount can't be less than 1.");
                    return;
                }

                if (Double.parseDouble(wager) == selection.getBet_amount()) {
                    NotifyUtils.showToast(getContext(), "New wager amount can't be same with old wager.");
                    return;
                }

                // Getting some info about the selection
                // on which leg is going to added
                List<Leg> legs = selection.getLegs();

                // Creating new leg to add to the list
                for (Leg leg : legs) {
                    viewModel.updateWager(Double.parseDouble(wager), leg.get_id());
                }
                // Finally adding leg to the room database
//                viewModel.insertLeg(leg);
                NotifyUtils.showToast(getContext(), "Updated Successfully");
                dialog.dismiss();
            }
        });
    }


    // Function to delete the selection
    private void deleteSelection(Selection selection) {
        List<Leg> legs = selection.getLegs();
        List<Leg> legList = viewModel.getLegsList().getValue();

        if (legList != null && legList.size() > 0) {
            for (Leg leg : legs) {
                viewModel.deleteLeg(leg);
                for (Leg leg1 : legList) {
                    if (leg.get_id() == leg1.get_id()) {
                        legList.remove(leg);
                        break;
                    }
                }
            }

            generateSelectionsList(legList);
            NotifyUtils.showToast(getContext(), "Deleted Successfully");
        }
    }

}