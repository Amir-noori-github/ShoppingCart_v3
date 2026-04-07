package org.example.shoppingcart;

public class CartCalculator {

    public static double calculateTotal(double[] prices, int[] quantities) {
        double total = 0;

        for (int i = 0; i < prices.length; i++) {
            total += prices[i] * quantities[i];
        }

        return total;
    }
}