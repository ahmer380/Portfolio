import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

/**
 * Write a description of class GameEngine1P here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GameEngine1P extends GameEngine
{
    private final Random random;

    public GameEngine1P(GameController gamePage, Player user)
    {
        //player1 = user, player2 = AI
        super(gamePage,user,Player.createRandomPlayer("AI",user.getColour()));
        random = new Random();
    }

    public void makeMove(int colPos) {
        super.makeMove(colPos);
        //A.I response move after
        if(getBoard().checkWin()|| getBoard().checkDraw()) {return;}
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.6), event -> super.makeMove(generateMove(getValidMoves(),Constants.MAX_DEPTH))));
        timeline.play();
    }

    public void undoMove() {
        super.undoMove();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.6), event -> super.undoMove()));
        timeline.play();
    }

    private int generateMove(ArrayList<Integer> candidateMoves, int depth) {
        if(candidateMoves.size() == 1) {return candidateMoves.get(0);}
        if(depth == 1) {return candidateMoves.get(random.nextInt(candidateMoves.size()));}
        ArrayList<Integer> shortlistedCandidateMoves = new ArrayList<>();
        int bestMoveScore = Constants.MIN_SCORE;
        for(Integer colPos: candidateMoves) {
            updateBoard(colPos);
            int score = minimax(depth-1,false, Constants.MIN_SCORE, Constants.MAX_SCORE); //False as we already perform move in first layer
            if(score > bestMoveScore) {
                bestMoveScore = score;
                shortlistedCandidateMoves.clear();
                shortlistedCandidateMoves.add(colPos);
            }
            else if(score == bestMoveScore) {
                shortlistedCandidateMoves.add(colPos);
            }
            updateBoard(-1); //Undo the previous move
        }
        return generateMove(shortlistedCandidateMoves,depth-1);
    }

    private int minimax(int depth, boolean isMaximising, int alpha, int beta) {
        if(getBoard().checkWin()) {
            if(getPlayer2().getName().equals(getCurrentTurn().getName())) { //player2's (i.e. the AI) turn after player1 (i.e. the non-AI user) moved, player1 won
                return Constants.MIN_SCORE;
            }
            else { //Player's turn after AI moved, AI won
                return Constants.MAX_SCORE;
            }
        }
        if(depth == 0 || getBoard().checkDraw()) {return 0;}

        int bestMoveScore;
        if(isMaximising) {
            bestMoveScore = Constants.MIN_SCORE;
            for(Integer colPos: getValidMoves()) {
                updateBoard(colPos);
                bestMoveScore = Math.max(bestMoveScore, minimax(depth-1,false,alpha,beta));
                updateBoard(-1);
                alpha = Math.max(alpha, bestMoveScore);
                if(beta <= alpha) {break;}
            }
        }
        else {
            bestMoveScore = Constants.MAX_SCORE;
            for(Integer colPos: getValidMoves()) {
                updateBoard(colPos);
                bestMoveScore = Math.min(bestMoveScore, minimax(depth-1,true,alpha,beta));
                updateBoard(-1);
                beta = Math.min(beta, bestMoveScore);
                if(beta <= alpha) {break;}
            }
        }
        return bestMoveScore;
    }
}