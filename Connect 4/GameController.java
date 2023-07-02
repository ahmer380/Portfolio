import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Bounds;

/**
 * Write a description of JavaFX class MainController here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GameController implements Initializable
{
    private GameEngine engine;

    @FXML
    private GridPane boardDisplay;

    @FXML
    private GridPane insertPieceRow;

    @Override
    public void initialize(URL url, ResourceBundle bundle){
    }

    /**
     * Initialise the board, and determine which game engine to use
     * @param player1 the player who goes first
     * @param player2 the player who goes second, or the AI if set to null
     */
    public void setup(Player player1, Player player2) {
        if(player2 == null) {engine = new GameEngine1P(this,player1);}
        else {engine = new GameEngine2P(this,player1,player2);}
        initializeBoardDisplay();
        Platform.runLater(this::resizeScreen);
    }

    public void initializeBoardDisplay() {
        //set the dimensions of each cell in the board to 80x80
        for (int col = 0; col < Constants.COLS; col++) {
            boardDisplay.getColumnConstraints().add(new ColumnConstraints(80));
        }
        for (int row = 0; row < Constants.ROWS; row++) {
            boardDisplay.getRowConstraints().add(new RowConstraints(80));
        }

        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLS; col++) {
                boardDisplay.add(makeCircle(Color.DARKGREY), col, row);
            }
        }

        //Initialising functionality to the insertPieceRow
        for(int col = 0; col < Constants.COLS; col++) {
            Circle circle = makeCircle(Color.PALEGREEN); //Colour irrelevant
            circle.setOnMouseClicked(this::initiateMove);
            insertPieceRow.add(circle, col, 0);
        }
        setInsertPieceRowColours();
    }

    private Circle makeCircle(Color colour) {
        Circle circle = new Circle(38);
        circle.setFill(colour);
        return circle;
    }

    /**
     * Called when a user clicks on a circle on the top row in order to initiate a move
     */
    @FXML
    private void initiateMove(MouseEvent event) {
        int colPos = GridPane.getColumnIndex((Circle) event.getSource());
        engine.makeMove(colPos);
    }

    /**
     * Called by the gameEngine, display the piece being inserted, and change the top row colours accordingly
     * row and col position is of the start/end cell on the board
     */
    public void updateDisplay(int rowPos, int colPos, boolean isUndo) {
        Circle pieceRowNode = null;
        Circle boardNode = null;
        for(Node node: insertPieceRow.getChildren()) {
            if(GridPane.getColumnIndex(node) == colPos) {
                pieceRowNode = (Circle) node;
                break;
            }
        }
        for(Node node: boardDisplay.getChildren()) {
            if(GridPane.getRowIndex(node) == rowPos && GridPane.getColumnIndex(node) == colPos) {
                boardNode = (Circle) node;
                break;
            }
        }
        simulatePieceTranslation(pieceRowNode,boardNode,isUndo);
        setInsertPieceRowColours();
    }

    /**
     * Drop the piece into the column when inserting, raise the piece from the column when undoing
     * @param pieceRowNode The node at the pieceInsertNode
     * @param boardNode The cell at the boardDisplay
     * @param isUndo Determines if we need the piece to rise/fall
     */
    private void simulatePieceTranslation(Circle pieceRowNode, Circle boardNode, boolean isUndo) {
        //Play piece drop sound throughout
        Media sound = new Media(new File("resources/piece_drop_sound.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> mediaPlayer.play()));
        if(!isUndo) {timeline.play();}
        Circle startingNode;
        Circle destinationNode;
        Color mockCircleColour;
        if(isUndo) {startingNode = boardNode; destinationNode = pieceRowNode; mockCircleColour = engine.getCurrentTurn().getPiecePaint(); startingNode.setFill(Color.DARKGREY);}
        else {startingNode = pieceRowNode; destinationNode = boardNode; mockCircleColour = engine.getNonPlayerTurn().getPiecePaint();}
        Circle mockCircle = makeCircle(mockCircleColour); //Not practically used outside this method
        Bounds startBounds = startingNode.localToScene(startingNode.getBoundsInLocal());
        Bounds endBounds = destinationNode.localToScene(destinationNode.getBoundsInLocal());
        mockCircle.setTranslateX(startBounds.getCenterX());
        mockCircle.setTranslateY(startBounds.getCenterY());
        Pane root = (Pane) boardDisplay.getScene().getRoot();
        root.getChildren().add(mockCircle);
        TranslateTransition pieceTranslation = new TranslateTransition(Duration.seconds(1.6));
        pieceTranslation.setNode(mockCircle);
        pieceTranslation.setToX(endBounds.getCenterX());
        pieceTranslation.setToY(endBounds.getCenterY());
        if(isUndo) {pieceTranslation.setOnFinished(event -> {
            setWindowDisable(false);
            root.getChildren().remove(mockCircle);
        });}
        else {pieceTranslation.setOnFinished(event -> {
            setWindowDisable(false);
            root.getChildren().remove(mockCircle);
            destinationNode.setFill(mockCircleColour);
        });}
        setWindowDisable(true);
        pieceTranslation.play();
    }

    public void setWindowDisable(boolean flag) {boardDisplay.getScene().getRoot().setDisable(flag);}

    private void setInsertPieceRowColours() {
        for(Node node: insertPieceRow.getChildren()) {
            Circle circle = (Circle) node;
            circle.setFill(engine.getCurrentTurn().getPreviewPaint());
            circle.setOnMouseEntered(mouseEvent -> circle.setFill(engine.getCurrentTurn().getPiecePaint()));
            circle.setOnMouseExited(mouseEvent -> circle.setFill(engine.getCurrentTurn().getPreviewPaint()));
            circle.setVisible(engine.checkValidMove(GridPane.getColumnIndex(circle))); //Blocks move if column is full
        }
    }

    public void handleGameEnd(boolean isDraw) {
        insertPieceRow.setDisable(true);
        Alert gameInfo = new Alert(Alert.AlertType.INFORMATION);
        gameInfo.setTitle("GAME OVER");
        if(isDraw) {gameInfo.setContentText("Draw!");}
        else {gameInfo.setContentText(engine.getNonPlayerTurn().getName() + " wins!");}
        Platform.runLater(gameInfo::showAndWait);
    }

    public void resumeGame() {insertPieceRow.setDisable(false);}

    @FXML
    private void undoMove() {
        insertPieceRow.setDisable(false); //Undo from winning scenario
        if(engine.getBoard().isBoardEmpty()) {
            Alert undoInvalid = new Alert(Alert.AlertType.ERROR);
            undoInvalid.setTitle("UNDO INVALID");
            undoInvalid.setContentText("Cannot undo a move from an empty board!");
            undoInvalid.showAndWait();
        }
        else {engine.undoMove();}
    }

    @FXML
    private void resizeScreen() {
        Stage stage = (Stage) boardDisplay.getScene().getWindow();
        stage.sizeToScene();
    }

    @FXML
    private void saveGame() {
        if(engine.getBoard().isBoardEmpty()) {
            Alert gameNotBegun = new Alert(Alert.AlertType.ERROR);
            gameNotBegun.setTitle("ERROR SAVING GAME");
            gameNotBegun.setContentText("Cannot save a game which hasn't yet begun!");
            gameNotBegun.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("resources"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());
        if(file != null && file.getName().toLowerCase().endsWith(".txt")) {
            try(FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(engine.getGameDataAsString());
                Alert gameSaved = new Alert(Alert.AlertType.INFORMATION);
                gameSaved.setTitle("GAME SAVED");
                gameSaved.setContentText("File has successfully saved!");
                gameSaved.showAndWait();
            }
            catch(IOException e) {
                Alert gameNotSaved = new Alert(Alert.AlertType.ERROR);
                gameNotSaved.setTitle("ERROR SAVING GAME");
                gameNotSaved.setContentText("Something went wrong..." + "\n" + "Ensure that the file chosen is a txt file.");
                gameNotSaved.showAndWait();
            }
        }
    }

    @FXML
    private void loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("resources"));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null && file.getName().toLowerCase().endsWith(".txt")) {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder gameData = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    gameData.append(line).append("\n");
                }
                engine.loadGame(gameData.toString().split("\n"),true);

                Alert gameLoaded = new Alert(Alert.AlertType.INFORMATION);
                gameLoaded.setTitle("GAME LOADED");
                gameLoaded.setContentText("Game file has successfully been loaded onto the board!");
                gameLoaded.showAndWait();
                return; //error message code not executed
            }
            catch(Exception ignored) {} //Don't return here, and execute final part of code (i.e. error message)
        }
        Alert gameNotLoaded = new Alert(Alert.AlertType.ERROR);
        gameNotLoaded.setTitle("ERROR LOADING GAME");
        gameNotLoaded.setContentText("""
                        Something went wrong...
                        Ensure that the file chosen is a txt file that has been
                        generated by this application before when saving a previous game""");
        gameNotLoaded.showAndWait();
    }

    public void copyCurrentBoard() {
        for(Node node: boardDisplay.getChildren()) {
            Circle circle = (Circle) node;
            if(engine.getBoard().getColourAtSquare(GridPane.getRowIndex(circle),GridPane.getColumnIndex(circle)) == ' ') {
                circle.setFill(Color.DARKGREY);
            }
            else if(engine.getCurrentTurn().getColour() == engine.getBoard().getColourAtSquare(GridPane.getRowIndex(circle),GridPane.getColumnIndex(circle))) {
                circle.setFill(engine.getCurrentTurn().getPiecePaint());
            }
            else {circle.setFill(engine.getNonPlayerTurn().getPiecePaint());}
        }
        setInsertPieceRowColours();
    }
}