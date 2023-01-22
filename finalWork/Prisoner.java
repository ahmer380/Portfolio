import java.util.ArrayList;
import java.util.Random;

/**
 * Class Prisoner - a prisoner in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Prisoner" is a friendly NPC whom can be spoken to by the player in order to help
 * them in their journey of escape.
 * They possess an ID number and a collection of available dialogues to say
 * 
 * @author  Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class Prisoner
{
    // a constant array that holds all rooms a prisoner can locate at
    private static final String[] locations = {
        "cafeteria", "kitchen", "corridor"
    };
    private String ID;
    private ArrayList<String> dialogues;
    private int currentDialogueIndex;

    /**
     * Constructor for objects of class Prisoner
     */
    public Prisoner(String ID)
    {
        dialogues = new ArrayList<>();
        this.ID = ID;
        currentDialogueIndex = 0;
    }

    /** 
     * Add a string of dialogues to the collection of dialogues the prisoner can say
     * @param dialogue The string of dialogue to be added
     */
    public void setDialogue(String dialogue) {
        dialogues.add(dialogue);
    }
    
    /**
     * Get the ID number (as a string) of the prisoner
     * @return The ID number (as a string) of the prisoner
     */
    public String getID() {
        return ID;
    }
    
    /**
     * Get the current dialogue the prisoner is to say to user user as
     * Directed by the currentDialogueIndex pointer
     * @return The current dialogue the prisoner is to say to the user
     */
    public String getCurrentDialogue() {
        return dialogues.get(currentDialogueIndex);
    }
    
    /**
     * Increment the currentDialogueIndex pointer by one
     * If the prisoner still yet has new dialogue to say
     */
    public void advanceDialogue() {
        if(currentDialogueIndex < dialogues.size() -1) {
            currentDialogueIndex++;
        }
    }

    /**
     * Choose a random room for the prisoner to move to
     * @return The name of the new room the prisoner will move to
     */
    public String selectNewRoom() {
        Random randomGenerator = new Random();
        return locations[randomGenerator.nextInt(locations.length)];
    }
      
}
