package org.example.shoppingcart;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class MainController {

    // Root container (for RTL/LTR)
    @FXML private VBox rootVBox;

    // Title
    @FXML private Label lblTitle;

    // Language selection
    @FXML private Label lblSelectLanguage;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private Button btnConfirmLanguage;

    // Number of items
    @FXML private Label lblNumItems;
    @FXML private TextField tfNumItems;
    @FXML private Button btnEnterItems;

    // Dynamic items container
    @FXML private VBox itemsContainer;

    // Calculate total
    @FXML private Button btnCalculateTotal;
    @FXML private Label lblTotalText;
    @FXML private Label lblTotalValue;

    // Localization
    private Locale currentLocale = new Locale("en", "US");
    private Map<String, String> localizedStrings;

    // Dynamic rows
    private ItemRow[] itemRows;

    @FXML
    public void initialize() {
        cbLanguage.getItems().addAll("English", "Finnish", "Swedish", "Japanese", "Arabic");

        localizedStrings = LocalizationService.getLocalizedStrings(currentLocale);
        applyLocalization();
        applyTextDirection();
    }

    @FXML
    public void onConfirmLanguage() {
        String selected = cbLanguage.getValue();
        if (selected == null) return;

        switch (selected) {
            case "Finnish" -> currentLocale = new Locale("fi", "FI");
            case "Swedish" -> currentLocale = new Locale("sv", "SE");
            case "Japanese" -> currentLocale = new Locale("ja", "JP");
            case "Arabic" -> currentLocale = new Locale("ar", "AR");
            default -> currentLocale = new Locale("en", "US");
        }

        localizedStrings = LocalizationService.getLocalizedStrings(currentLocale);
        applyLocalization();
        applyTextDirection();
    }

    private void applyLocalization() {
        lblTitle.setText(localizedStrings.get("title"));
        lblSelectLanguage.setText(localizedStrings.get("select_language"));
        btnConfirmLanguage.setText(localizedStrings.get("confirm_language"));

        lblNumItems.setText(localizedStrings.get("enter_num_items"));
        btnEnterItems.setText(localizedStrings.get("enter_items"));

        btnCalculateTotal.setText(localizedStrings.get("calculate_total"));
        lblTotalText.setText(localizedStrings.get("total"));
    }

    private void applyTextDirection() {
        boolean isRTL = currentLocale.getLanguage().equals("ar");

        Platform.runLater(() -> {
            rootVBox.setNodeOrientation(
                    isRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT
            );
        });
    }

    @FXML
    public void onEnterItems() {
        itemsContainer.getChildren().clear();
        lblTotalValue.setText("");
        itemRows = null;

        String numItemsStr = tfNumItems.getText();
        if (numItemsStr == null || numItemsStr.isEmpty()) {
            System.out.println("No number entered");
            return;
        }

        int numItems;
        try {
            numItems = Integer.parseInt(numItemsStr);
            if (numItems <= 0) return;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
            return;
        }

        itemRows = new ItemRow[numItems];
        boolean isRTL = currentLocale.getLanguage().equals("ar");

        for (int i = 0; i < numItems; i++) {
            ItemRow row = new ItemRow(
                    localizedStrings.get("price_label"),
                    localizedStrings.get("quantity_label")
            );

            row.setNodeOrientation(
                    isRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT
            );

            itemRows[i] = row;
            itemsContainer.getChildren().add(row);
        }
    }

    @FXML
    public void onCalculateTotal() {
        if (itemRows == null || itemRows.length == 0) {
            lblTotalValue.setText("0");
            return;
        }

        double[] prices = new double[itemRows.length];
        int[] quantities = new int[itemRows.length];

        List<CartItem> cartItems = new ArrayList<>();

        for (int i = 0; i < itemRows.length; i++) {
            try {
                String priceText = normalizeDigits(itemRows[i].priceField.getText());
                String qtyText   = normalizeDigits(itemRows[i].quantityField.getText());

                double price = Double.parseDouble(priceText);
                int qty = Integer.parseInt(qtyText);

                prices[i] = price;
                quantities[i] = qty;

                double subtotal = price * qty;

                cartItems.add(new CartItem(i + 1, price, qty, subtotal));

            } catch (NumberFormatException e) {
                lblTotalValue.setText("Error");
                return;
            }
        }

        double total = CartCalculator.calculateTotal(prices, quantities);

        // Display formatted total
        lblTotalValue.setText(formatNumber(total, currentLocale));

        // Count total items
        int totalItems = Arrays.stream(quantities).sum();

        // Save to DB
        CartService.saveCart(
                totalItems,
                total,
                currentLocale.getLanguage(),
                cartItems
        );
    }
    private String normalizeDigits(String input) {
        if (input == null) return "";

        return input
                .replace("٠","0").replace("١","1").replace("٢","2")
                .replace("٣","3").replace("٤","4").replace("٥","5")
                .replace("٦","6").replace("٧","7").replace("٨","8")
                .replace("٩","9");
    }
    private String formatNumber(double number, Locale locale) {
        return String.format(locale, "%.2f", number);
    }
}