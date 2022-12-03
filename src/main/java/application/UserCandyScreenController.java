package application;

import candy.Candy;
import database.DatabaseManager;
import global.GlobalVariables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * This class controls screen, where user can input his own candy
 */
public class UserCandyScreenController {

    @FXML
    private TextField calorieField;
    @FXML
    private TextField colorField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField ratingField;
    @FXML
    private TextField sugarField;
    @FXML
    private Label successAddLabel;
    @FXML
    private TextField weightField;

    @FXML
    protected void returnToScreen1(ActionEvent event) throws IOException {
        GlobalVariables.logger.info("Clicked on return button");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("screen1.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void addCandyAction(){
        GlobalVariables.logger.info("Clicked on add user candy to table candies");
        DatabaseManager manager = new DatabaseManager();

        String name = nameField.getText();
        String weight = weightField.getText();
        String price = priceField.getText();
        String calorie = calorieField.getText();
        String rating= ratingField.getText();
        String sugar = sugarField.getText();
        String color = colorField.getText();

        Candy newCandy = new Candy(name, weight,price,rating,calorie,sugar,color);
        manager.addCandyToDB(newCandy);

        successAddLabel.setText("Candy  \"" +newCandy.getName()+ "\""  +"\nwas successfully added to the list!\n" +
                "To find updated list of candies \ngo to first menu option - \"Fill the gift!\"\n" +
                "or add one more candy!");

        nameField.clear();
        weightField.clear();
        priceField.clear();
        calorieField.clear();
        ratingField.clear();
        sugarField.clear();
        colorField.clear();
    }
}
