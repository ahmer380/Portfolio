import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Write a description of JavaFX class MainController here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class MenuController implements Initializable
{
    @FXML
    private Pane p1;
    @FXML
    private Pane p2;
    private Circle player1Colour;
    private Circle player2Colour;

    @Override
    public void initialize(URL url, ResourceBundle bundle){
        p1.setVisible(false);
        p2.setVisible(false);
    } 

    private void beginGame(String player1Name, String player2Name) {
        playClickSound();
        Player player2 = null; //If null, then we play 1P
        Player player1 = Player.createPlayer(player1Name,((Color) player1Colour.getFill()));
        if(!player2Name.equals("")) {player2 = Player.createPlayer(player2Name,((Color) player2Colour.getFill()));}
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GamePage.fxml"));
            p1.getScene().setRoot(loader.load());
            ((GameController) loader.getController()).setup(player1, player2);
        }
        catch(java.io.IOException ioe) {
            ioe.printStackTrace(); 
        }
    }

    @FXML
    private void handlePlayButtonClick() {
        String player1Name = ((TextField) p1.lookup("#p1TextField")).getText();
        String player2Name = ((TextField) p2.lookup("#p2TextField")).getText();
        if("".equals(player1Name) || player1Colour == null) {displayMissingDataErrorPrompt("player1 details missing!") ;return;}
        if(p2.isVisible() && ("".equals(player2Name) || player2Colour == null)) {displayMissingDataErrorPrompt("player2 details missing!"); return;}
        if(player1Name.equals(player2Name)) {displayMissingDataErrorPrompt("player1 and player2 cannot have the same names!"); return;}
        beginGame(player1Name, player2Name);

    }

    private void displayMissingDataErrorPrompt(String reason) {
        Alert missingDataAlert = new Alert(Alert.AlertType.ERROR);
        missingDataAlert.setTitle("MISSING DATA ERROR");
        missingDataAlert.setContentText(reason);
        missingDataAlert.showAndWait();
    }

    private void playClickSound() {
        Media sound = new Media(new File("resources/click_sound.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    /**
     * Called when a user clicks on a colour
     * @param event The type of circle clicked
     */
    @FXML
    private void handleColourPick(MouseEvent event) {
        playClickSound();
        Circle clickedCircle = (Circle) event.getSource();
        if("p1".equals((clickedCircle).getParent().getId())) {
            if(player1Colour != null) {resetColour(player1Colour);}
            player1Colour = clickedCircle;
            player1Colour.setStroke(Color.TOMATO);
            player1Colour.setStrokeWidth(3);
            if(player2Colour != null && player1Colour.getFill() == player2Colour.getFill()) {resetColour(player2Colour);}
        }
        else {
            if(player2Colour != null) {resetColour(player2Colour);}
            player2Colour = clickedCircle;
            player2Colour.setStroke(Color.TOMATO);
            player2Colour.setStrokeWidth(3);
            if(player1Colour != null && player1Colour.getFill() == player2Colour.getFill()) {resetColour(player1Colour);}
        }
    }

    private void resetColour(Circle circle) {
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);
        if(circle == player1Colour) {player1Colour = null;}
        else {player2Colour = null;}
    }

    @FXML
    private void handleColourHover(MouseEvent event) {
        Circle clickedCircle = (Circle) event.getSource();
        if(clickedCircle != player1Colour && clickedCircle != player2Colour) {
            clickedCircle.setStrokeWidth(3);
        }
    }

    @FXML
    private void handleColourUnhover(MouseEvent event) {
        Circle clickedCircle = (Circle) event.getSource();
        if(clickedCircle != player1Colour && clickedCircle != player2Colour) {
            clickedCircle.setStrokeWidth(1);
        }
    }

    @FXML
    private void handleGameModePick(ActionEvent event) {
        playClickSound();
        p1.setVisible(true);
        Button gameModeButtonClicked = (Button) event.getSource();
        gameModeButtonClicked.setStyle("-fx-border-color: tomato red; -fx-border-width: 3px;");
        if("oneP".equals(gameModeButtonClicked.getId())) {
            gameModeButtonClicked.getParent().lookup("#twoP").setStyle("-fx-border-width: 0px");
            ((TextField) p1.lookup("#p1TextField")).setPromptText("insert player name"); //"player" instead of "player1"
            //Remove data stored on pane p2, and make it invisible
            p2.setVisible(false);
            ((TextField) p2.lookup("#p2TextField")).setText("");
            if(player2Colour != null) {resetColour(player2Colour);}
        }
        else {
            gameModeButtonClicked.getParent().lookup("#oneP").setStyle("-fx-border-width: 0px");
            ((TextField) p1.lookup("#p1TextField")).setPromptText("insert player1 name"); //"player1" instead of "player"
            p2.setVisible(true);
        }
    }
}  