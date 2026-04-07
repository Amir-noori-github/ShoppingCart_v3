package org.example.shoppingcart;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartCalculatorTest {

    // Test individual item cost calculations
    @Test
    public void testSingleItemCost() {
        double[] prices = {10.0};
        int[] quantities = {3};

        double result = CartCalculator.calculateTotal(prices, quantities);

        assertEquals(30.0, result);
    }

    // Test multiple items total
    @Test
    public void testMultipleItemsTotal() {
        double[] prices = {10.0, 5.0, 2.5};
        int[] quantities = {2, 3, 4};

        double expected = 10*2 + 5*3 + 2.5*4;
        double result = CartCalculator.calculateTotal(prices, quantities);

        assertEquals(expected, result);
    }

    // Test empty arrays
    @Test
    public void testEmptyArrays() {
        double[] prices = {};
        int[] quantities = {};

        double result = CartCalculator.calculateTotal(prices, quantities);

        assertEquals(0.0, result);
    }

    // Test negative values (optional but good practice)
    @Test
    public void testNegativeValues() {
        double[] prices = {10.0, -5.0};
        int[] quantities = {1, 2};

        double result = CartCalculator.calculateTotal(prices, quantities);

        assertEquals(10*1 + (-5)*2, result);
    }
}