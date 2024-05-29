package models;

import java.util.HashMap;

public class NutritionFacts extends Product {
    private String updater;
    private String updateDate;
    private double servingSize;
    private HashMap<String, Double> nutrients;

    public NutritionFacts(long productId, String productName, String category, HashMap<String, Double> nutrients, double servingSize, String updateDate, String updater) {
        super(productId, productName, category);
        this.nutrients = nutrients;
        this.servingSize = servingSize;
        this.updateDate = updateDate;
        this.updater = updater;
    }

    public double getNutrientValue(String nutrientName) {
        return nutrients.get(nutrientName);
    }

    public double getServingSize() {
        return servingSize;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getUpdater() {
        return updater;
    }
}
