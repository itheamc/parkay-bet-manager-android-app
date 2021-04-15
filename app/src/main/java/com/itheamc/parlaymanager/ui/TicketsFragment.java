package com.itheamc.parlaymanager.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.itheamc.parlaymanager.R;
import com.itheamc.parlaymanager.adapters.TicketsAdapter;
import com.itheamc.parlaymanager.callbacks.ItemsClickListener;
import com.itheamc.parlaymanager.databinding.FragmentTicketsBinding;
import com.itheamc.parlaymanager.viewmodel.ParlayViewModel;

import java.util.Random;


public class TicketsFragment extends Fragment implements ItemsClickListener {
    private static final String TAG = "TicketsFragment";
    private FragmentTicketsBinding ticketsBinding;
    private TicketsAdapter ticketsAdapter;
    private ParlayViewModel viewModel;
    private NavController navController;
    private InterstitialAd mInterstitialAd;

    public TicketsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ticketsBinding = FragmentTicketsBinding.inflate(inflater, container, false);
        return ticketsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ParlayViewModel.class);

        ticketsAdapter = new TicketsAdapter(this);
        ticketsBinding.ticketRecyclerView.setAdapter(ticketsAdapter);
        ticketsAdapter.submitList(viewModel.getTicketList());

        // Banner Ads Code
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: " + initializationStatus.toString());
            }
        });

        AdView mAdView = ticketsBinding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        requestInterstitialAds(adRequest);
    }



    /*___________Method overrided from ItemsClickListener______________*/
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
    public void onSwipe(int position) {

    }


    // Load interstitial ads
    private void requestInterstitialAds(AdRequest adRequest) {
        if (getContext() == null) {
            return;
        }
        InterstitialAd.load(getContext(), getResources().getString(R.string.interstitial_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                showInterstitialAds();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });
    }

    // Function to show the interstitial ads
    private void showInterstitialAds() {
        if (mInterstitialAd == null) {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            return;
        }

        // Set fullscreen content callback to interstitial ads
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });
        
        mInterstitialAd.show(requireActivity());

    }


    // Function to generate random number
    private int generateRandInt() {
        Random random = new Random();
        int rand = random.nextInt(100);
        return rand % 3;
    }
}