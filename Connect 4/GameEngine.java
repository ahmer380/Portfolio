import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Write a description of class GameEngine here (singleton).
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class GameEngine
{
    private final GameController gamePage;
    private Board board;

    private Player player1;

    private Player player2;

    private Player currentTurn;

    private ArrayList<Integer> validMoves;


    protected GameEngine(GameController gamePage, Player player1, Player player2) {
        this.gamePage = gamePage;
        this.player1 = player1;
        this.player2 = player2;
        currentTurn = player1;
        board = new Board();
        setValidMoves();
    }

    /**
    Default implementation for a 2P engine, called by the gameController only (directly/indirectly)
     */
    protected void makeMove(int colPos)
    {
        updateBoard(colPos);
        gamePage.updateDisplay(board.getMostRecentPos()[0],board.getMostRecentPos()[1],false);
        if(board.checkWin()) {gamePage.handleGameEnd(false);}
        else if(board.checkDraw()) {gamePage.handleGameEnd(true);}
        //1P engine insert response here
    }

    /**
     * Responsible for modifying the internal representation of the game, whenever a move is made/unmade
     * @param colPos undo the previous move if colPos is negative, otherwise perform the move
     */
    protected void updateBoard(int colPos) {
        if(colPos < 0) {board.undoMove();}
        else {board.makeMove(colPos,currentTurn.getColour());}
        setValidMoves();
        switchTurn();
    }

    protected void undoMove() {
        updateBoard(-1);
        gamePage.updateDisplay(board.getMostRecentPos()[0],board.getMostRecentPos()[1],true);
    }

    public Player getCurrentTurn() {return currentTurn;}

    public Player getNonPlayerTurn() {
        if(currentTurn == player1) {return player2;}
        else {return player1;}
    }

    private void switchTurn() {
        if(currentTurn == player1) {currentTurn = player2;}
        else {currentTurn = player1;}
    }

    private void setValidMoves() {
        validMoves = new ArrayList<>();
        for(int colPos = 0; colPos < Constants.COLS; colPos++) {
            if(board.checkColFull(colPos)) {
                validMoves.add(colPos);
            }
        }
    }

    /**
     * line1: Player1Name
     * line2: Player1Colour
     * line3: player2Name
     * line4: player2Colour
     * line5: Move order (csv of colPos)
     */
    public String getGameDataAsString() {
        StringBuilder gameData = new StringBuilder();
        gameData.append("ahmerconnect4file").append("\n");
        gameData.append(player1.getName()).append("\n");
        gameData.append(player1.getPiecePaint()).append("\n");
        gameData.append(player2.getName()).append("\n");
        gameData.append(player2.getPiecePaint()).append("\n");
        gameData.append(board.getMoveOrderAsString()).append("\n");
        return gameData.toString();
    }

    public void loadGame(String[] gameData, boolean usePlayerDetails) throws Exception {
        if(!"ahmerconnect4file".equals(gameData[0])) {throw new Exception();}
        if(usePlayerDetails) {
            player1 = Player.createPlayer(gameData[1],Color.valueOf(gameData[2]));
            player2 = Player.createPlayer(gameData[3],Color.valueOf(gameData[4]));
        }
        currentTurn = player1;
        board = new Board();
        setValidMoves();
        String[] moveData = gameData[5].split(",");
        for(String data: moveData) {
            int colPos = Integer.parseInt(data);
            updateBoard(colPos);
        }
        //Check if loaded file is at end of game win
        if(board.checkWin()) {gamePage.handleGameEnd(false);}
        else if(board.checkDraw()) {gamePage.handleGameEnd(true);}
        else {gamePage.resumeGame();} //Use to transition from an ending game to a non-ending game
        gamePage.copyCurrentBoard();
    }

    public boolean checkValidMove(int colPos) {return validMoves.contains(colPos);}
    protected ArrayList<Integer> getValidMoves() {return validMoves;}
    
    protected Board getBoard() {return board;}

    protected Player getPlayer2() {return player2;}

}
