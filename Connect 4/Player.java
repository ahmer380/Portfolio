import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Player {
    private static final HashMap<Color,Player> colourMap = new HashMap<>() {{
        put(Color.GREEN,createGreenPlayer());
        put(Color.GOLD,createYellowPlayer());
        put(Color.ROYALBLUE,createBluePlayer());
        put(Color.HOTPINK,createPinkPlayer());
    }};
    private static final Random random = new Random();
    private String name;
    private final char colour;
    private final Color piecePaint;
    private final Color previewPaint;

    private Player(char colour, Color piecePaint, Color previewPaint) {
        this.name = "PLACEHOLDER"; //Initialised later
        this.colour = colour;
        this.piecePaint = piecePaint;
        this.previewPaint = previewPaint;
    }

    public static Player createPlayer(String name, Color colour) {
        Player player = colourMap.get(colour);
        player.name = name;
        return player;
    }

    //Colour is randomly assigned
    public static Player createRandomPlayer(String name, char occupiedColour) {
        ArrayList<Player> playerTypes = new ArrayList<>(colourMap.values());
        Player player = playerTypes.get(random.nextInt(playerTypes.size()));
        while(player.colour == occupiedColour) {player = playerTypes.get(random.nextInt(playerTypes.size()));}
        player.name = name;
        return player;
    }

    private static Player createGreenPlayer() {
        return new Player('G', Color.GREEN, Color.PALEGREEN);
    }
    private static Player createYellowPlayer() {
        return new Player('Y', Color.GOLD, Color.rgb(253,234,120));
    }
    private static Player createBluePlayer() {
        return new Player('B', Color.ROYALBLUE, Color.LIGHTBLUE);
    }
    private static Player createPinkPlayer() {
        return new Player('P', Color.HOTPINK, Color.LIGHTPINK);
    }

    public String toString() {return name+","+colour;}

    public String getName() {return name;}
    public char getColour() {return colour;}
    public Color getPiecePaint() {return piecePaint;}
    public Color getPreviewPaint() {return previewPaint;}
}