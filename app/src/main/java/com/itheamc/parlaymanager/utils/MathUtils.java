package com.itheamc.parlaymanager.utils;

import android.annotation.SuppressLint;

import com.itheamc.parlaymanager.models.Leg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MathUtils {
    // Function Permutation
    public static int calcPermutation(List<Leg> legs, int r) {
        return calcFactorial(legs.size()) / calcFactorial(legs.size() - r);
    }

    // Function to calculate combination
    public static int calcCombination(List<Leg> legs, int r) {
        int n = legs.size();
        return calcFactorial(n) / (calcFactorial(r) * calcFactorial(n - r));
    }

    // Function to calculate combination
    public static int calcCombination(int[] arr, int r) {
        int n = arr.length;
        return calcFactorial(n) / (calcFactorial(r) * calcFactorial(n - r));
    }

    // function to calculate factorial of Nth term
    private static int calcFactorial(int n) {
        int factorial = 1;
        for (int i = n; i > 0; i--) {
            factorial = factorial * i;
        }

        return factorial;
    }


    /*_________________Functions for Leg Model____________________ */

    // function to calculate decimal odds
    public static double calcDecimalOdds(int american_odds) {
        double decimal_odds  = 0.0;
        if (american_odds > 0) {
            decimal_odds = ((double) american_odds + 100.0)/100.0;
        } else {
            decimal_odds = ((double) american_odds - 100.0)/(double) american_odds;
        }

        return roundUp(decimal_odds);
    }


    // function to calculate decimal odds
    public static String calcFractionalOdds(int american_odds) {
        String fractional_odds  = "";
        if (american_odds > 0) {
            fractional_odds = calcFraction(american_odds, 100);
        } else {
            fractional_odds = calcFraction(100, -1*american_odds);
        }

        return fractional_odds;
    }


    // Function to calculate the fraction of the given numbers
    @SuppressLint("DefaultLocale")
    public static String calcFraction(int nominator, int denominator) {
        int n = nominator;
        int d = denominator;

//        for (int i = 2; i < 20; i++) {
//            if (!isModulusZero(n, d, i)) {
//                continue;
//            }
//
//            n = n/i;
//            d = d/i;
//        }

        int i = 2;
        while (i < 20) {
            if (!isModulusZero(n, d, i)) {
                i++;
                continue;
            }

            n = n/i;
            d = d/i;
            i = 2;
        }

        return String.format("%d/%d", n, d);
    }


    // Function to check whether nominator and denominator is divided by given number or not
    private static boolean isModulusZero(int n, int d, int i) {
        return n%i == 0 && d%i == 0;
    }


    // Function to calculate win chance
    public static double calcWinChance(int american_odds) {
        return roundUp((1.00/calcDecimalOdds(american_odds)) * 100.00);
    }

    // Function to calculate earning
    public static double calcEarning(double bet_amount, int american_odds) {
        return roundUp(bet_amount * calcDecimalOdds(american_odds));
    }


    // Function to calculate profit
    public static double calcProfit(double bet_amount, int american_odds) {
        return roundUp(calcEarning(bet_amount, american_odds) - bet_amount);
    }

    /*___________________________Combine Parley______________________*/
    // FUnction to calculate true odds
    public static double calcTrueOdds(List<Leg> legs) {
        double trueOdds = 1;
        for (Leg l: legs) {
            trueOdds *= l.get_decimal_odds();
        }

        return roundUp(trueOdds);
    }

    // Function to calculate combined earnings
    public static double calcCombinedEarning(List<Leg> legs, double bet_amount) {
        return roundUp(calcTrueOdds(legs) * bet_amount);
    }

    // Function to calculate combined profits
    public static double calcCombinedProfits(List<Leg> legs, double bet_amount) {
        return roundUp(calcCombinedEarning(legs, bet_amount) - bet_amount);
    }




//    // Function to roundup using custom class
//    public static double roundUp(double val) {
//        double d = val;
//        String s = String.valueOf(d);
//        if (s.contains(".")) {
//            int i = s.indexOf(".");
//            if (s.substring(i+1, s.length()-1).length() > 2) {
//                d = Double.parseDouble(s.substring(0, i+3));
//                if (Double.parseDouble(String.valueOf(s.charAt(i + 3))) >= 5) {
//                    d += 0.01;
//                }
//            } else {
//                d = Double.parseDouble(s);
//            }
//        }
//
//        return d;
//    }

    // Function to roundup using builtin class
    public static double roundUp(double val) {
        BigDecimal instance = new BigDecimal(val);
        instance = instance.setScale(3, RoundingMode.HALF_UP);
        return instance.doubleValue();
    }
}
