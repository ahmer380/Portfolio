import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Responsible for storing the state of the board, and providing functionality to manipulating the board.
 * board[0][0] is the top-left of the board, we go down as row increases, and right as col increases
 */
public class Board {
    private class Move {
        private final int colPos;
        private final char colour; //May not be needed
        private Move(int colPos, char colour) {
            this.colPos = colPos;
            this.colour = colour;
        }
    }
    private char[][] board = new char[Constants.ROWS][Constants.COLS];
    private int[] mostRecentPos = new int[2]; //Used to check for wins
    private Stack<Move> moveLog; //Each move is an integer, of the column number the piece is placed

    public Board() {
        moveLog = new Stack<>();
        emptyBoard();
    }

    public void makeMove(int colPos, char colour) {
        moveLog.add(new Move(colPos, colour));
        int rowPos = 0;
        while(rowPos < Constants.ROWS-1 && board[rowPos+1][colPos] == ' ') {
            rowPos += 1;
        }
        board[rowPos][colPos] = colour;
        mostRecentPos[0] = rowPos;
        mostRecentPos[1] = colPos;
    }

    public void undoMove() throws EmptyStackException {
        if(moveLog.isEmpty()) {
            throw new EmptyStackException();
        }
        Move move = moveLog.pop();
        int rowPos = 0;
        while(board[rowPos][move.colPos] == ' ') {
            rowPos += 1;
        }
        mostRecentPos[0] = rowPos;
        mostRecentPos[1] = move.colPos;
        board[rowPos][move.colPos] = ' ';
    }

    //Called every time a new piece is added
    public boolean checkWin() {
        for(int[] vector: Constants.VECTORS) {
            if(checkLine(vector)) {
                return true;
            }
        }
        return false;
    }

    //It may be assumed that checkWin is always called prior to this
    public boolean checkDraw() {return moveLog.size() == Constants.ROWS * Constants.COLS;}

    //Checks both forwards and backwards directions of the vector, called by checkWin 4 times
    private boolean checkLine(int[] vector) {
        int consecutiveColoursInLine = 0;
        consecutiveColoursInLine += checkDirection(vector[0],vector[1]);
        consecutiveColoursInLine += checkDirection(vector[0]*-1,vector[1]*-1);
        return consecutiveColoursInLine + 1 >= 4;
    }

    //Checks a single direction, called by checkLine twice
    private int checkDirection(int rowVector, int colVector) {
        int consecutiveColoursInDirection = 0;
        int[] currentPos = mostRecentPos.clone();
        while(0 <= currentPos[0] && currentPos[0] < Constants.ROWS
                && 0 <= currentPos[1] && currentPos[1] < Constants.COLS
                && board[currentPos[0]][currentPos[1]] == moveLog.peek().colour) {
            currentPos[0] += rowVector;
            currentPos[1] += colVector;
            consecutiveColoursInDirection ++;
        }
        return consecutiveColoursInDirection - 1;
    }

    public int[] getMostRecentPos() {return mostRecentPos;}

    private void emptyBoard() {
        for(int row = 0; row < Constants.ROWS; row++) {
            for(int col = 0; col < Constants.COLS; col++) {
                board[row][col] = ' ';
            }
        }
    }

    public boolean checkColFull(int colPos) {return board[0][colPos] == ' ';}

    public boolean isBoardEmpty() {return moveLog.isEmpty();}

    public char getColourAtSquare(int rowPos, int colPos) {return board[rowPos][colPos];}

    public void printBoard() {
        for (char[] row : board) {
            System.out.print("["); // Opening bracket for the row

            // Iterate over the elements in the row
            for (int i = 0; i < row.length; i++) {
                System.out.print(row[i]);

                if (i < row.length - 1) {
                    System.out.print(", "); // Separator between elements
                }
            }

            System.out.println("]"); // Closing bracket for the row
        }
        System.out.println();
        System.out.println();
    }

    /**
     * We just reverse the moveLog stack to get the order of the moves
     * Assume that at least one move has been played at the time of saving
     */
    public String getMoveOrderAsString() {
        StringBuilder moveOrder = new StringBuilder();
        for(Move move: moveLog) {
            moveOrder.append(move.colPos).append(",");
        }
        moveOrder.deleteCharAt(moveOrder.length()-1);
        return moveOrder.toString();
    }
}
