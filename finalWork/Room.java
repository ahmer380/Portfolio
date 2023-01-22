import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * A room also stores collections of enemies, prisoners, and items within it
 * 
 * @author  Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class Room 
{
    private String description;
    private ArrayList<Enemy> enemies;
    private ArrayList<Prisoner> prisoners;
    private ArrayList<Item> items;
    private HashMap<String, Room> exits; // stores exits of this room.

    /**
     * Create a room described "description". Initially, it has
     * No exits. "description" is something like "inside the cafeteria" or
     * "inside the prison corridor".
     * @param description The room's description.
     */
    public Room(String description) 
    {
        this.description = description;
        prisoners = new ArrayList<>();
        enemies = new ArrayList();
        exits = new HashMap<>();
        items = new ArrayList<>();
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    /**
     * Return the field 'description' of the room only
     * @return The short description of the room
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Room exits: north west
     *     Room prisoners: A331
     *     Room items: knife(3)
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "\n" + "You are " + description + ".\n" + getExitString()
        + "\n" + getPrisonerString() + "\n" + getItemString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Room exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Room exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }
    
    /**
     * Return a string describing the room's prisoners, for example
     * "Room prisoners: A331 D143".
     * @return Details of the room's exits
     */
    private String getPrisonerString() {
        String returnString = "Room prisoners:";
        for(Prisoner prisoner : prisoners) {
            returnString += " " + prisoner.getID();
        }
        return returnString;
    }
    
    /**
     * Return a string describing the room's items and their weights, for example
     * "Room items: knife(3) key(10)".
     * @return Details of the room's items.
     */
    public String getItemString()
    {
        String returnString = "Room items:";
        for(Item item : items) {
            returnString += " " + item.getName() + "(" + item.getWeight() + ")";
        }
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
    
    /**
     * Add a prisoner to the collection of prisoners
     * @param prisoner The prisoner object to be added
     */
    public void setPrisoner(Prisoner prisoner) {
        prisoners.add(prisoner);
    }
    
    /**
     * Get the prisoner object, given its ID
     * @param ID the prisoner ID
     * @return The person who identifies with the ID number
     */
    public Prisoner getPrisoner(String ID) {
        for(Prisoner prisoner: prisoners) {
            if(prisoner.getID().equals(ID)) {
                return prisoner;
            }
        }
        return null;
    }
    
    /**
     * Get all prisoners inside the room, and empty the collection of prisoners
     * @return All of the prisoners inside the room (before emptied)
     */
    public ArrayList<Prisoner> returnAndRemoveAllPrisoners() {
        ArrayList<Prisoner> prisonersInsideRoom = new ArrayList<>();
        while(!prisoners.isEmpty()) {
            prisonersInsideRoom.add(prisoners.remove(0));
        }
        return prisonersInsideRoom;
    }
    
    /** 
     * Remove a prisoner from the room
     * @param prisoner The prisoner object to be removed
     */
    public void removePrisoner(Prisoner prisoner) {
        prisoners.remove(prisoner);
    }
    
    /**
     * Add an enemy (guard/warden) to the collection of enemies
     * @param enemy The enemy object to be added
     */
    public void setEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    
    /**
     * Get any enemy (in this case the first enemy) from the collection of enemies
     * @return An enemy object from the collection of enemies
     */
    public Enemy getEnemy() {
        return enemies.get(0);
    }
    
    /**
     * Remove an enemy object from the collection of enemies
     * @param enemy The enemy object to be removed
     */
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }
    
    /**
     * Given a type of enemy, return the number of enemies inside
     * The room which are of the given type 
     * @return The quantity of enemies of the inputted type 
     */
    public int getEnemyTypeQuantity(String enemyType) {
        int enemyTypeQuantity = 0;
        for(Enemy enemy: enemies) {
            if(enemy.getEnemyType().equals(enemyType)) {
                enemyTypeQuantity ++;
            }
        }
        return enemyTypeQuantity;
    }
    
    /**
     * Get the total HP of enemies inside the room
     * @return The total HP of enemies inside the room
     */
    public int getTotalEnemyHealth() {
        int totalEnemyHealth = 0;
        for(Enemy enemy: enemies) {
            totalEnemyHealth += enemy.getHealth();
        }
        return totalEnemyHealth;
    }
    
    /**
     * Add an item to the collection of items
     * @param item The item object to be added
     */
    public void setItem(Item item) {
        items.add(item);
    }
    
    /**
     * Get the item object, given its name
     * @param name The name of the item
     * @return The item which identifies with the name
     */
    public Item getItem(String name) {
        for(Item item: items) {
            if(item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
    
    /** Remove an item from the collection of items
     *  @param item The item object to be removed
     */
    public void removeItem(Item item) {
        items.remove(item);
    }
}

