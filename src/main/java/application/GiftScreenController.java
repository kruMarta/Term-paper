package application;

import database.DatabaseManager;
import emailSender.SenderEmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static global.GlobalVariables.*;

/**
 * This class controls screen, where user can see his gift info and do different sorting actions with immediate result
 */
public class GiftScreenController implements Initializable {

    static DatabaseManager manager;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private ListView<String> giftList;
    @FXML
    private Label summary;
    @FXML
    private RadioButton rWeight, rPrice, rRating;
    @FXML
    private Label actionLabel;
    @FXML
    private ListView<String> sortedList;
    @FXML
    private Spinner<Integer> minSpinner;
    @FXML
    private Spinner<Integer> maxSpinner;
    @FXML
    private CheckBox emailCheckBox;
    @FXML
    private ImageView trashIcon;
    @FXML
    private Label weightLabel;
    private int currentMinValue;
    private int currentMaxValue;
    private static String candyList = "";
    private String selectedCandy;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshListViewWithCandies();

        giftList.setOnMouseClicked(e -> {
            selectedCandy = giftList.getSelectionModel().getSelectedItem();
        });

        Image giftIcon = new Image(BIN_ICON);
        trashIcon.setImage(giftIcon);

        int minimumSugar = 0, maxSugar = 0, value;

        for (String candy : candiesInsideGift.keySet()) {
            value = (int) Double.parseDouble(manager.findOneParameter(candy, CANDY_SUGAR));
            if (minimumSugar == 0 && maxSugar == 0) {
                minimumSugar = value;
                maxSugar = value;
            } else if (value < minimumSugar) {
                minimumSugar = value;
            } else if (value > maxSugar) {
                maxSugar = value;
            }
        }

        SpinnerValueFactory<Integer> spinnerValueFactory = new
                SpinnerValueFactory.IntegerSpinnerValueFactory(minimumSugar, maxSugar);
        SpinnerValueFactory<Integer> spinnerValueFactory2 = new
                SpinnerValueFactory.IntegerSpinnerValueFactory(minimumSugar, maxSugar);
        spinnerValueFactory.setValue(1);
        spinnerValueFactory2.setValue(1);
        maxSpinner.setValueFactory(spinnerValueFactory);
        minSpinner.setValueFactory(spinnerValueFactory2);
    }

    public void refreshListViewWithCandies() {
        try {
            manager = new DatabaseManager();
            ResultSet insideGift = manager.showCandiesInsideGift();
            candyList = "";
            while (insideGift.next()) {
                String candy = insideGift.getString(CANDY_NAME);
                candiesInsideGift.put(insideGift.getString(CANDY_NAME), Integer.valueOf(insideGift.getString(CANDY_AMOUNT)));
                candy += " x";
                candy += insideGift.getString(CANDY_AMOUNT);
                giftList.getItems().add(candy);
                candyList += candy + "\n";
            }
            ResultSet candySum = manager.getAmountOfCandies();
            while (candySum.next()) {
                String amount = candySum.getString("SUM(amount)");
                summary.setText("Total: " + amount);
            }
            weightLabel.setText(getGiftWeight());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can`t get info about the gift");
            throw new RuntimeException(e);
        }
    }

    private String getGiftWeight() {
        manager = new DatabaseManager();
        double weight = 0;
        String result = "";
        for (Map.Entry<String, Integer> candy : candiesInsideGift.entrySet()) {
            double gram = Double.parseDouble(manager.findOneParameter(candy.getKey(), CANDY_WEIGHT));
            weight += gram * candy.getValue();
        }
        if (weight >= 1000) {
            result += (int)weight/1000 + "." + (int)(weight % (int) Math.pow(10, (int) Math.log10(weight))) + " kg";
        } else{
            result += (int)weight + " g";
        }
        return result;
    }

    @FXML
    private void deleteCandy() {
        logger.info("Clicked delete candy button");
        manager = new DatabaseManager();
        manager.deleteRow(selectedCandy.substring(0, selectedCandy.indexOf(" x")), GIFT_TABLE);
        candiesInsideGift.remove(selectedCandy.substring(0, selectedCandy.indexOf(" x")));
        giftList.getItems().clear();
        refreshListViewWithCandies();
    }

    @FXML
    private void findInRange() {
        logger.info("Clicked find sugar in range button");
        manager = new DatabaseManager();
        sortedList.getItems().clear();
        int sugarContent;
        currentMinValue = minSpinner.getValue();
        currentMaxValue = maxSpinner.getValue();

        minSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
            currentMinValue = minSpinner.getValue();
        });

        maxSpinner.valueProperty().addListener((observableValue, integer, t1) -> {
            currentMaxValue = maxSpinner.getValue();
        });

        actionLabel.setText("Candies in range " + currentMinValue + " - " + currentMaxValue);
        for (String candy : candiesInsideGift.keySet()) {
            sugarContent = (int) Double.parseDouble(manager.findOneParameter(candy, CANDY_SUGAR));
            if (sugarContent >= currentMinValue && sugarContent <= currentMaxValue) {
                sortedList.getItems().add(candy + " - " + sugarContent);
            }
        }
    }

    @FXML
    private void sortCandies() throws SQLException {
        logger.info("Clicked sort candies button");
        manager = new DatabaseManager();
        sortedList.getItems().clear();
        ResultSet sortBy;
        String parameter;
        if (rWeight.isSelected()) {
            sortBy = manager.sortBy(CANDY_WEIGHT);
            actionLabel.setText("Sorted by weight:");
            parameter = CANDY_WEIGHT;
        } else if (rPrice.isSelected()) {
            sortBy = manager.sortBy(CANDY_PRICE);
            actionLabel.setText("Sorted by price:");
            parameter = CANDY_PRICE;
        } else if (rRating.isSelected()) {
            sortBy = manager.sortBy(CANDY_RATING);
            actionLabel.setText("Sorted by rating:");
            parameter = CANDY_RATING;
        } else {
            sortBy = manager.sortBy("calories");
            actionLabel.setText("Sorted by calories:");
            parameter = CANDY_CALORIES;
        }

        ArrayList<String> candiesQueue = new ArrayList<>();
        while (sortBy.next()) {
            candiesQueue.add(sortBy.getString(CANDY_NAME));
        }
        sortBy.close();

        for (String name : candiesQueue) {
            sortedList.getItems().add(name + " - " + manager.findOneParameter(name, parameter));
        }
    }

    public void exit() throws SQLException {
        logger.info("Clicked exit button");
        manager = new DatabaseManager();
        ResultSet amountResult = manager.getAmountOfCandies();
        String amount = "";
        while (amountResult.next()) {
            amount = amountResult.getString("SUM(amount)");
        }
        double sum = getTotalPrice();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("An exit");
        alert.setHeaderText("You`re about to close the app!");
        alert.setContentText("Are you sure tou want to exit?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            manager.clearTable();
            Stage stage = (Stage) scenePane.getScene().getWindow();
            stage.close();
        }

        if (emailCheckBox.isSelected()) {
            mailMessage += "  Dear customer,\n" +
                    "thanks for using our service \"New Year's gift\" in our Candy shop!\n" +
                    "\n" +
                    "Here is your receipt:\n";
            mailMessage += candyList;
            mailMessage += "\nTotal amount of candies: " + amount;
            mailMessage += "\nSummary: " + sum + " UAN";
            SenderEmail.main(new String[]{""});
        }
    }

    private double getTotalPrice() {
        manager = new DatabaseManager();
        double sum = 0;
        for (Map.Entry<String, Integer> candy : candiesInsideGift.entrySet()) {
            double price = Double.parseDouble(manager.findOneParameter(candy.getKey(), CANDY_PRICE));
            sum += price * candy.getValue();
        }
        return sum;
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
