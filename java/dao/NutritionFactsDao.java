package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.NutritionFacts;
import utils.DatabaseConnection;

public class NutritionFactsDao {
    // Get the nutrition facts of a product from the database
    public NutritionFacts getNutritionFactsById(long productId) {
        NutritionFacts nutritionFacts = null;
        String sql = "SELECT * FROM nutrition_facts WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Double> nutrients = new HashMap<>();
                nutrients.put("calories", rs.getDouble("calories"));
                nutrients.put("fat", rs.getDouble("fat"));
                nutrients.put("carbs", rs.getDouble("carbs"));
                nutrients.put("protein", rs.getDouble("protein"));
                nutrients.put("fiber", rs.getDouble("fiber"));
                nutrients.put("sugar", rs.getDouble("sugar"));

                nutritionFacts = new NutritionFacts(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        new HashMap<>(nutrients),
                        rs.getDouble("serving_size"),
                        rs.getString("update_date"),
                        rs.getString("updater")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nutritionFacts;
    }
    // Add the nutrition facts of a product to the database
    public void addNutritionFacts(NutritionFacts nutritionFacts) {
        String sql = "INSERT INTO nutrition_facts (product_id, product_name, category, serving_size, calories, fat, carbs, protein, fiber, sugar, update_date, updater) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, nutritionFacts.getProductId());
            pstmt.setString(2, nutritionFacts.getProductName());
            pstmt.setString(3, nutritionFacts.getCategory());
            pstmt.setDouble(4, nutritionFacts.getServingSize());
            pstmt.setDouble(5, nutritionFacts.getNutrientValue("calories"));
            pstmt.setDouble(6, nutritionFacts.getNutrientValue("fat"));
            pstmt.setDouble(7, nutritionFacts.getNutrientValue("carbs"));
            pstmt.setDouble(8, nutritionFacts.getNutrientValue("protein"));
            pstmt.setDouble(9, nutritionFacts.getNutrientValue("fiber"));
            pstmt.setDouble(10, nutritionFacts.getNutrientValue("sugar"));
            pstmt.setString(11, nutritionFacts.getUpdateDate());
            pstmt.setString(12, nutritionFacts.getUpdater());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Update the nutrition facts of a product in the database
    public void updateNutritionFacts(NutritionFacts nutritionFacts) {
        String sql = "UPDATE nutrition_facts SET product_name = ?, category = ?, serving_size = ?, calories = ?, fat = ?, carbs = ?, protein = ?, fiber = ?, sugar = ?, update_date = ?, updater = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nutritionFacts.getProductName());
            pstmt.setString(2, nutritionFacts.getCategory());
            pstmt.setDouble(3, nutritionFacts.getServingSize());
            pstmt.setDouble(4, nutritionFacts.getNutrientValue("calories"));
            pstmt.setDouble(5, nutritionFacts.getNutrientValue("fat"));
            pstmt.setDouble(6, nutritionFacts.getNutrientValue("carbs"));
            pstmt.setDouble(7, nutritionFacts.getNutrientValue("protein"));
            pstmt.setDouble(8, nutritionFacts.getNutrientValue("fiber"));
            pstmt.setDouble(9, nutritionFacts.getNutrientValue("sugar"));
            pstmt.setString(10, nutritionFacts.getUpdateDate());
            pstmt.setString(11, nutritionFacts.getUpdater());
            pstmt.setLong(12, nutritionFacts.getProductId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Remove the nutrition facts of a product from the database
    public void deleteNutritionFacts(long productId) {
        String sql = "DELETE FROM nutrition_facts WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, productId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}