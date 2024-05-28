package utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitialization {
    public static void initialize() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n"
                                + "    username TEXT PRIMARY KEY,\n"
                                + "    password TEXT NOT NULL,\n"
                                + "    nickname TEXT NOT NULL\n"
                                + ");";
        String createProductsTable = "CREATE TABLE IF NOT EXISTS products (\n"
                                   + "    id BIGINT PRIMARY KEY,\n"
                                   + "    name TEXT NOT NULL,\n"
                                   + "    category TEXT NOT NULL,\n"
                                   + "    image BLOB\n"
                                   + ");";
        String createNutritionFactsTable = "CREATE TABLE IF NOT EXISTS nutrition_facts (\n"
                                          + "    product_id BIGINT,\n"
                                          + "    product_name TEXT NOT NULL,\n"
                                          + "    category TEXT NOT NULL,\n"
                                          + "    serving_size DOUBLE,\n"
                                          + "    calories DOUBLE,\n"
                                          + "    fat DOUBLE,\n"
                                          + "    carbs DOUBLE,\n"
                                          + "    protein DOUBLE,\n"
                                          + "    fiber DOUBLE,\n"
                                          + "    sugar DOUBLE,\n"
                                          + "    update_date TEXT,\n"
                                          + "    updater TEXT,\n"
                                          + "    PRIMARY KEY (product_id),\n"
                                          + "    FOREIGN KEY (product_id) REFERENCES products (id)\n"
                                          + ");";

        try (Connection conn = DatabaseConnection.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createProductsTable);
            stmt.execute(createNutritionFactsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}