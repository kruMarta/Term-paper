package global;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class GlobalVariables {

    public static final String CANDIES_TABLE = "candies";
    public static final String GIFT_TABLE = "gift";
    public static final String CANDY_NAME = "name";
    public static final String CANDY_WEIGHT = "weight";
    public static final String CANDY_PRICE = "price";
    public static final String CANDY_RATING = "consumer_rating";
    public static final String CANDY_CALORIES = "calories";
    public static final String CANDY_SUGAR = "sugar_content";
    public static final String CANDY_COLOR = "wrapping_color";
    public static final String CANDY_AMOUNT = "amount";
    public static String mailMessage = "";
    public static final String GIFT_ICON = "D:\\University\\2 курс\\ПП\\Term paper\\src\\main\\resources\\application\\Gift_Icon.jpg";
    public static final String LOGO = "D:\\University\\2 курс\\ПП\\Term paper\\src\\main\\resources\\application\\logo.jpg";
    public static final String BIN_ICON = "D:\\University\\2 курс\\ПП\\Term paper\\src\\main\\resources\\application\\trashBin.jpg";
    public static final String EMAIL_ICON = "D:\\University\\2 курс\\ПП\\Term paper\\src\\main\\resources\\application\\emailPhoto.jpg";
    public static final HashMap<String, Integer> candiesInsideGift = new HashMap<>();
    public static Logger logger = Logger.getGlobal();
    public static FileHandler handler;

    public static FadeTransition createFader(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }

    public static SequentialTransition showNewTextWindow(String text, Stage stage){

        Label label = new Label(text);
        label.setStyle("-fx-text-fill:  #000000; -fx-font-size: 25px; -fx-font-family: \"Gabriola\"");

        FadeTransition fader = createFader(label);
        SequentialTransition blinkThenFade = new SequentialTransition(label, fader);

        stage.setScene(new Scene(new StackPane(label), 600, 530));
        stage.show();
        blinkThenFade.play();
        return blinkThenFade;
    }
}
