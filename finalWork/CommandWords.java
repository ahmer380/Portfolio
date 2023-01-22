import java.util.HashMap;
import java.util.Set;

/**
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.
 * 
 * This class holds an enumeration of all command words known to the game.
 * It is used to recognise commands as they are typed in.
 *
 * @author  Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class CommandWords
{
    // a hashMap that holds all valid command words as the key, and their descriptions as values
    private HashMap<String,String> validCommands;

    /**
     * Constructor - initialise the command words.
     */
    public CommandWords()
    {
        validCommands = new HashMap<>();
        validCommands.put("quit", ": end the game");
        validCommands.put("help", ": receive information on what to do/available commands");
        validCommands.put("back", ": visit the previous room you are at");
        validCommands.put("go", ": <direction>, visit an adjacent room");
        validCommands.put("talk", ": <prisonerID>, you can only talk to prisoners inside the same room as you");
        validCommands.put("take", ": <itemName>, pick up an item inside a room if you have enough inventory space");
        validCommands.put("drop", ": <itemName>, release an item from your inventory into the room");
        validCommands.put("scan", ": <enemyType> <direction>, search for enemy guard/warden characters ");
        validCommands.put("feed", ": <prisonerID> <foodItem>, give a prisoner food in exchange for a secret");
        validCommands.put("UNLOCK", ": ??????????");
    }

    /**
     * Check whether a given String is a valid command word. 
     * @return true if it is, false if it isn't.
     */
    public boolean isCommand(String aString)
    {
        if(validCommands.get(aString) != null) {
            return true;
        }
        // if we get here, the string was not found in the commands
        return false;
    }

    /**
     * Print all valid commands to System.out.
     */
    public void showAll() 
    {
        Set<String> commands = validCommands.keySet();
        for(String command: commands) {
            String description = validCommands.get(command);
            System.out.print(command + description + "\n");
        }
        System.out.println();
    }
}
