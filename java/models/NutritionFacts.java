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

    public void addNutrient(String nutrientName, double value) {
        nutrients.put(nutrientName, value);
    }

    public void removeNutrient(String nutrientName) {
        nutrients.remove(nutrientName);
    }

    public HashMap<String, Double> getNutrients() {
        return nutrients;
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

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}