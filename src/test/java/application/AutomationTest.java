package application;

import static global.GlobalVariables.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import database.DatabaseManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.ListViewMatchers;

import java.sql.SQLException;

@ExtendWith(ApplicationExtension.class)
class AutomationTest {

    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("screen1.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image(LOGO);
        stage.getIcons().add(icon);
        stage.setTitle("Candy shop");

        DatabaseManager.connectToDB();

        stage.setScene(scene);
        stage.show();
    }

    @Test
    void checkNames() {
        verifyThat("#candiesListButton", hasText("Fill the gift!"));
        verifyThat("#newCandyButton", hasText("Add my candy!"));
        verifyThat("#exitButton", hasText("Exit"));
    }

    @Test
    void addNewCandy(FxRobot robot) {
        robot.clickOn("#newCandyButton");
        robot.write("Hello");
        robot.clickOn("#weightField");
        robot.write("10");
        robot.clickOn("#priceField");
        robot.write("10");
        robot.clickOn("#sugarField");
        robot.write("10");
        robot.clickOn("#ratingField");
        robot.write("10");
        robot.clickOn("#calorieField");
        robot.write("10");
        robot.clickOn("#colorField");
        robot.write("Red");
        robot.clickOn("#addButton");
        assertTrue(DBContainsCandy("Hello"));
    }

    private boolean DBContainsCandy(String name) {
        String contains = "SELECT * FROM " + CANDIES_TABLE + " WHERE " + CANDY_NAME + " = \"" + name + "\"";
        boolean queryResult;
        try {
            queryResult = DatabaseManager.statement.execute(contains);
            if (queryResult) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void checkGiftWeight(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }
        robot.clickOn("#giftButton");
        verifyThat("#weightLabel", LabeledMatchers.hasText("114 g"));
    }

    @Test
    void checkCandiesAmountInGift(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }
        robot.clickOn("#giftButton");
        verifyThat("#summary", LabeledMatchers.hasText("Total: 9"));
    }

    private void addCandiesToGift() {
        DatabaseManager manager = new DatabaseManager();
        manager.addCandyToGiftDB("Milky splash", 3);
        manager.addCandyToGiftDB("Mont Blanc", 5);
        manager.addCandyToGiftDB("Florens", 1);
    }

    @Test
    void checkNameInListView(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }
        robot.clickOn("#giftButton");
        verifyThat("#giftList", ListViewMatchers.hasListCell("Florens x1"));
    }

    @Test
    void sortByWeight(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }
        robot.clickOn("#giftButton");
        robot.clickOn("#rWeight");
        robot.clickOn("#sortButton");
        verifyThat("#actionLabel", LabeledMatchers.hasText("Sorted by weight:"));
        verifyThat("#sortedList", ListViewMatchers.hasListCell("Mont Blanc - 12.0"));
    }

    @Test
    void sortByPrice(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }

        robot.clickOn("#giftButton");
        robot.clickOn("#rPrice");
        robot.clickOn("#sortButton");
        verifyThat("#actionLabel", LabeledMatchers.hasText("Sorted by price:"));
        verifyThat("#sortedList", ListViewMatchers.hasListCell("Milky splash - 14.0"));
    }

    @Test
    void sortByRating(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }

        robot.clickOn("#giftButton");
        robot.clickOn("#rRating");
        robot.clickOn("#sortButton");
        verifyThat("#actionLabel", LabeledMatchers.hasText("Sorted by rating:"));
        verifyThat("#sortedList", ListViewMatchers.hasListCell("Mont Blanc - 21"));
    }

    @Test
    void sortByCalories(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }

        robot.clickOn("#giftButton");
        robot.clickOn("#rCalories");
        robot.clickOn("#sortButton");
        verifyThat("#actionLabel", LabeledMatchers.hasText("Sorted by calories:"));
        verifyThat("#sortedList", ListViewMatchers.hasListCell("Mont Blanc - 7.0"));
    }

    @Test
    void rangeCandies(FxRobot robot) {
        DatabaseManager manager = new DatabaseManager();
        if (manager.giftIsEmpty()) {
            addCandiesToGift();
        }

        robot.clickOn("#giftButton");
        robot.doubleClickOn("#minSpinner");
        robot.write("2");
        robot.doubleClickOn("#maxSpinner");
        robot.write("3");
        robot.clickOn("#rangeButton");
        verifyThat("#actionLabel", LabeledMatchers.hasText("Candies in range 2 - 3"));
        verifyThat("#sortedList", ListViewMatchers.hasListCell("Mont Blanc - 2"));
    }

    @AfterAll
    static void cleanEverything() {
        DatabaseManager manager = new DatabaseManager();
        manager.deleteRow("Hello", CANDIES_TABLE);

        manager.clearTable();
    }

}