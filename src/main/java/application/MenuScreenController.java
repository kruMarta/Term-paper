package application;

import database.DatabaseManager;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static global.GlobalVariables.*;

/**
 * This class works with starting user interface screen
 */
public class MenuScreenController implements Initializable{
    @FXML
    private AnchorPane scenePane;
    @FXML
    private ImageView iconImage;
    private Stage stage;
    private Scene scene;
    private Parent root;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image giftIcon = new Image(GIFT_ICON);
        iconImage.setImage(giftIcon);
    }

    @FXML
    private void giftClick(ActionEvent event) throws IOException {
        logger.info("Clicked on the gift button");
        DatabaseManager manager = new DatabaseManager();

        if(manager.giftIsEmpty()){
            logger.info("Gift is empty");
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            SequentialTransition blinkThenFade = showNewTextWindow("Gift is empty", stage);

            blinkThenFade.setOnFinished(new EventHandler<>() {
                @Override
                public void handle(ActionEvent event) {
                    Parent root;
                    try {
                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("screen1.fxml")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            });
        } else
        {
            logger.info("Gift isn`t empty");
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("giftScreen.fxml")));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    protected void clickFillFromList(ActionEvent event) throws IOException {
        logger.info("Clicked on candies from list button");
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("candiesListScreen.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    protected void clickNewCandy(ActionEvent event) throws IOException {
        logger.info("Clicked on new user candy button");
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("userInputScreen.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void exit() {
        logger.info("Clicked on exit button");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("An exit");
        alert.setHeaderText("You`re about to close the app!");
        alert.setContentText("Are you sure tou want to exit?");
        if(alert.showAndWait().get()== ButtonType.OK) {
            DatabaseManager manager = new DatabaseManager();
            manager.clearTable();
            stage = (Stage)scenePane.getScene().getWindow();
            stage.close();
        }
    }
}