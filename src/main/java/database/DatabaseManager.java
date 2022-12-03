package database;

import candy.Candy;
import global.GlobalVariables;

import java.sql.*;
import java.util.logging.Level;

import static global.GlobalVariables.*;

/**
 * This class deals with everything in database. Gets and puts info in tables, refresh tables and returns all the
 * necessary data from tables.
 */
public class DatabaseManager {
    public static Statement statement;
    PreparedStatement preparedStatement;
    public static Connection connection;

    public static void connectToDB() throws SQLException {
        connection = DBConnection.connectDB();
        statement = connection.createStatement();
    }

    public DatabaseManager() {
    }

    /**
     * This method puts user information in every column into table.
     *
     * @param newCandy candy-object, that contains full information about the candy.
     */
    public void addCandyToDB(Candy newCandy) {

        String insert = "INSERT INTO " + CANDIES_TABLE + "(" + CANDY_NAME + "," + CANDY_WEIGHT + "," +
                CANDY_PRICE + "," + CANDY_RATING + "," + CANDY_CALORIES + "," + CANDY_SUGAR + "," +
                CANDY_COLOR + ")" + "VALUES(?,?,?,?,?,?,?)";

        try {
            preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, newCandy.getName());
            preparedStatement.setString(2, newCandy.getWeight());
            preparedStatement.setString(3, newCandy.getPrice());
            preparedStatement.setString(4, newCandy.getConsumerRating());
            preparedStatement.setString(5, newCandy.getCaloricContent());
            preparedStatement.setString(6, newCandy.getSugarContent());
            preparedStatement.setString(7, newCandy.getWrapperColor());
            preparedStatement.executeUpdate();

            logger.info("User candy was successfully added to table " + CANDIES_TABLE);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "User candy can`t be added to database." + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds information about candy to table "gift".
     *
     * @param candyName key - name of the candy
     * @param amount    value - amount of candy
     */
    public void addCandyToGiftDB(String candyName, int amount) {
        String insert = "INSERT INTO " + GIFT_TABLE + "(" +
                CANDY_NAME + "," + CANDY_AMOUNT + ")" + "VALUES(?,?)";

        try {
            preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, candyName);
            preparedStatement.setString(2, String.valueOf(amount));
            preparedStatement.executeUpdate();
            logger.info("Candy was successfully added to table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Candy can`t be added to database." + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all names in table "candies"
     *
     * @return result of the query
     */
    public ResultSet showCandiesNames() {
        String getNames = "SELECT name FROM " + CANDIES_TABLE;
        ResultSet queryResult;

        try {
            queryResult = statement.executeQuery(getNames);
            logger.info("Query showed successfully all names in table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Names can`t be found" + e);
            throw new RuntimeException(e);
        }
        return queryResult;
    }

    /**
     * Shows full information of the candy by its name.
     *
     * @param name name of the candy
     * @return result of the query
     */
    public ResultSet findByName(String name) {
        String getName = "SELECT * FROM " + CANDIES_TABLE + " WHERE name = \"" + name + "\"";
        ResultSet queryResult;

        try {
            queryResult = statement.executeQuery(getName);
            logger.info("Query showed data by name in table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Candy can`t be found" + e);
            throw new RuntimeException(e);
        }
        return queryResult;
    }

    /**
     * @return if table "gift" is empty or not
     */
    public boolean giftIsEmpty() {
        String amountStatement = "SELECT COUNT(*) FROM " + GlobalVariables.GIFT_TABLE;
        ResultSet queryResult;
        int amount = 0;
        try {
            queryResult = statement.executeQuery(amountStatement);
            while (queryResult.next()) {
                amount = queryResult.getInt("count(*)");
            }
            logger.info("Query showed successfully if there is something in table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can`t check if gift is empty or not" + e);
            throw new RuntimeException(e);
        }
        return amount <= 0;
    }

    /**
     * Show all candies inside gift
     *
     * @return result of the query
     */
    public ResultSet showCandiesInsideGift() {
        String getCandies = "SELECT * FROM " + GIFT_TABLE;
        ResultSet queryResult;

        try {
            queryResult = statement.executeQuery(getCandies);
            logger.info("Query showed successfully candies in table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can`t check what gift consist of" + e);
            throw new RuntimeException(e);
        }
        return queryResult;
    }

    /**
     * Gets total amount of candies inside the gift
     *
     * @return result of the query
     */
    public ResultSet getAmountOfCandies() {
        String getCandies = "SELECT 'amount', SUM(amount) FROM " + GIFT_TABLE;
        ResultSet queryResult;

        try {
            queryResult = statement.executeQuery(getCandies);
            logger.info("Query showed successfully amount of candies in table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.info("Can`t show amount of candies in table " + GIFT_TABLE);
            throw new RuntimeException(e);
        }
        return queryResult;
    }

    /**
     * Sorts candies inside the gift by parameter
     *
     * @param parameter field in table that describes one characteristic of the candy
     * @return result of the query
     */
    public ResultSet sortBy(String parameter) {
        String sort = "SELECT gift.name, gift.amount FROM " + GIFT_TABLE + " LEFT JOIN " + CANDIES_TABLE +
                " ON gift.name = candies.name" + " ORDER BY candies." + parameter + " ASC";

        ResultSet queryResult;
        try {
            queryResult = statement.executeQuery(sort);
            logger.info("Query showed successfully sorted candies in table " + GIFT_TABLE + " by " + parameter);
        } catch (SQLException e) {
            logger.info("Can`t sort candies in table " + GIFT_TABLE);
            throw new RuntimeException(e);
        }
        return queryResult;
    }

    /**
     * Gets one characteristic from table candies of the candy from table gift
     *
     * @param name      name of the candy
     * @param parameter field to be found
     * @return founded value
     */
    public String findOneParameter(String name, String parameter) {
        String sort = "SELECT " + parameter + " FROM " + GlobalVariables.CANDIES_TABLE + " WHERE name = \"" + name + "\"";

        ResultSet queryResult;
        String getValue = "";
        try {
            queryResult = statement.executeQuery(sort);
            while (queryResult.next()) {
                getValue = queryResult.getString(parameter);
            }
            logger.info("Query showed successfully candy in table " + GIFT_TABLE + " with " + parameter);
        } catch (SQLException e) {
            logger.info("Can`t find candy in table " + GIFT_TABLE);
            throw new RuntimeException(e);
        }
        return getValue;
    }

    /**
     * Delete object data from table
     *
     * @param name name of the candy
     */
    public void deleteRow(String name, String table) {
        String delete = "DELETE FROM " + table + " WHERE name = \"" + name + "\"";
        try {
            statement.executeUpdate(delete);
            logger.info("Query deleted successfully candy from table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.info("Can`t delete candy from table " + GIFT_TABLE);
            throw new RuntimeException(e);
        }
    }

    /**
     * Clear table gift after closing the app
     */
    public void clearTable() {
        String drop = "TRUNCATE TABLE " + GIFT_TABLE;
        try {
            statement.executeUpdate(drop);
            logger.info("Query cleared successfully everything from table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.info("Can`t clear table " + GIFT_TABLE);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets new value of amount of candy in the gift
     *
     * @param name   name of the candy
     * @param amount amount of candy
     */
    public void refreshAmount(String name, Integer amount) {
        String replace = "REPLACE INTO " + GIFT_TABLE + "(" + CANDY_NAME + "," + CANDY_AMOUNT + ")" +
                " VALUES(\"" + name + "\"" + "," + amount + ")";
        try {
            statement.executeUpdate(replace);
            logger.info("Query refreshed successfully amount in table " + GIFT_TABLE);
        } catch (SQLException e) {
            logger.info("Can`t refreshed table " + GIFT_TABLE);
            throw new RuntimeException(e);
        }
    }
}
