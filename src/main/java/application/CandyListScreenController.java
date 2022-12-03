package application;

import candy.Candy;
import database.DatabaseManager;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static global.GlobalVariables.*;

/**
 * This class works with screen, where user can pick candies from list and add them to the gift
 */
public class CandyListScreenController implements Initializable {

    @FXML
    private ListView<String> candiesList;
    @FXML
    private Label candyInfo;
    @FXML
    private Label addCandyLabel;
    @FXML
    private Spinner<Integer> amountSpinner;
    private Stage stage;
    private DatabaseManager manager;
    private int currentValue;
    private SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
    private Candy candy;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        addOrRefreshCandiesInHashMap();

        spinnerValueFactory.setValue(1);
        amountSpinner.setValueFactory(spinnerValueFactory);

        try {
            manager = new DatabaseManager();
            ResultSet names = manager.showCandiesNames();
            while (names.next()) {
                String name = names.getString(CANDY_NAME);
                candiesList.getItems().add(name);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can`t show candies from the table candies");
            throw new RuntimeException(e);
        }

        candiesList.setOnMouseClicked(e -> {
            Candy candy = getSelectedCandyInfo();
            candyInfo.setText("Weight: " + candy.getWeight() +
                    "\nPrice: " + candy.getPrice() + "\nConsumer rating: " + candy.getConsumerRating() +
                    "\nCalorie content: " + candy.getCaloricContent() + "\nSugar content: " + candy.getSugarContent() +
                    "\nWrapping color: " + candy.getWrapperColor());

            currentValue = amountSpinner.getValue();
            addCandyLabel.setText("Add candy \"" + candy.getName() + "\"" + " x" + currentValue + " to the gift?");

            amountSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
                currentValue = amountSpinner.getValue();
                addCandyLabel.setText("Add candy \"" + candy.getName() + "\"" + " x" + currentValue + " to the gift?");
            });
        });
    }

    private void addOrRefreshCandiesInHashMap() {
        try {
            manager = new DatabaseManager();
            ResultSet insideGift = manager.showCandiesInsideGift();
            while (insideGift.next()) {
                candiesInsideGift.put(insideGift.getString(CANDY_NAME), Integer.valueOf(insideGift.getString(CANDY_AMOUNT)));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can`t add candies from the candies table to HashMap");
            throw new RuntimeException(e);
        }
    }

    private Candy getSelectedCandyInfo() {
        String selected = candiesList.getSelectionModel().getSelectedItem();
        ResultSet selectedCandy = manager.findByName(selected);
        candy = new Candy();

        try {
            while (selectedCandy.next()) {
                candy = new Candy(selectedCandy.getString(CANDY_NAME), selectedCandy.getString(CANDY_WEIGHT),
                        selectedCandy.getString(CANDY_PRICE), selectedCandy.getString(CANDY_RATING), selectedCandy.getString(CANDY_CALORIES),
                        selectedCandy.getString(CANDY_SUGAR), selectedCandy.getString(CANDY_COLOR));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can`t get data about selected candy");
            throw new RuntimeException(e);
        }
        return candy;
    }

    @FXML
    public void addCandyToGift(ActionEvent event) {
        logger.info("Clicked add candy to the gift button");
        if (candiesInsideGift.containsKey(candy.getName())) {
            manager.refreshAmount(candy.getName(), currentValue);
        } else {
            manager.addCandyToGiftDB(candy.getName(), currentValue);
        }
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SequentialTransition blinkThenFade = showNewTextWindow("Added successfully to the gift!", stage);

        blinkThenFade.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Parent root;
                try {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("candiesListScreen.fxml")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
    }

    @FXML
    protected void returnToMenu(ActionEvent event) throws IOException {
        logger.info("Clicked return button");
        Parent root = FXMLLoader.load(getClass().getResource("screen1.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

