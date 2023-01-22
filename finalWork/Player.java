import java.util.ArrayList;
import java.util.Set;

/**
 * Class Player - a player in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A single "Player" object is created in the "Game" class, and it primarily stores
 * data of the inventory the playerUser is holding throughout the game
 * 
 * @author  Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class Player
{
    private ArrayList<Item> inventory;
    private int maxWeight;

    /**
     * Constructor for objects of class Player
     */
    public Player()
    {
        inventory = new ArrayList<>();
        maxWeight = 10;
    }
    
    /**
     * Get a desciption of the status of the player
     * Including the contents of their inventory and their current inventory size
     * @return A description of the player's status
     */
    public String getDescription() {
        return getInventoryString() + "\n" + getInventorySizeString();
    }
    
    /**
     * Get a string describing the player's inventory, for example
     * "Inventory: knife(3) key(10)".
     * @return Details of the the player's inventory
     */
    public String getInventoryString() {
        String returnString = "Player inventory:";
        for(Item item : inventory) {
            returnString += " " + item.getName() + "(" + item.getWeight() + ")";
        }
        return returnString;
    }
    
    /**
     * Get a string describing the player's current inventory size, for example
     * "Inventory size: 7/10"
     * @return A string describing the player's current inventory size
     */
    private String getInventorySizeString() {
        return "Player inventory Size: " + getWeightOfInventory() + "/" + maxWeight;
    }
    
    /**
     * Get the current weight of the inventory of items held by the player
     * @return The current weight of the inventory of items held by the player
     */
    public int getWeightOfInventory() {
        int currentWeightOfPlayer = 0;
        for(Item item: inventory) {
            currentWeightOfPlayer += item.getWeight();
        }
        return currentWeightOfPlayer;
    }
    
    /**
     * Get the number of weapons in the player's inventory
     * @return The number of weapons in the player's inventory
     */
    public int getTotalWeaponsInInventory() {
        int totalWeaponsInInventory = 0;
        for(Item item: inventory) {
            if(item.getIsWeapon()) {
                totalWeaponsInInventory ++;
            }
        }
        return totalWeaponsInInventory;
    }
    
    /**
     * Add an item to the player's inventory
     * @param item The item object to be added to the inventory
     */
    public void setItem(Item item) {
        inventory.add(item);
    }
    
    /**
     * Get the item object from the player's inventory, given its name
     * @param name the name of the item
     * @return The item which identifies with the name
     */
    public Item getItem(String name) {
        for(Item item: inventory) {
            if(item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Get any item in the inventory which is a weapon
     * @return Any item in the inventory which is a weapon
     */
    public Item getWeaponItem() {
        for(Item item : inventory) {
            if(item.getIsWeapon()) {
                return item;
            }
        }
        return null; //should never get to here
    }
    
    /** Remove an item from the player's inventory
     *  @param item the item object to be removed
     */
    public void removeItem(Item item) {
        inventory.remove(item);
    }
    
    /**
     * Get the maximum weight of items the player can hold
     * @return the maximum weight of items the player can hold
     */
    public int getMaxWeight() {
        return maxWeight;
    }

}
