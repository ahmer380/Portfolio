/**
 * Class Item - an item in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * An "Item" an object, and are inside a "Room". Some items can be picked up
 * by the player whereas others cannot, determined by their weight
 * Also, an item falls under the category of being edible (can be fed to prisoners),
 * being a weapon (can be used to eliminate enemies), or neither
 * 
 * @author  Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class Item
{
    private String name;
    private boolean isEdible;
    private boolean isWeapon;
    private int weight;
    
    /**
     * Constructor for objects of class Items
     */
    public Item(String name, boolean isEdible, boolean isWeapon, int weight)
    {
        this.name = name;
        this.isEdible = isEdible;
        this.isWeapon = isWeapon;
        this.weight = weight;
    }
    
    /**
     * Get the name of the item 
     * @return The name of the item 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the weight of the item
     * @return The weight of the item
     */
    public int getWeight() {
        return weight;
    }
    
    /**
     * Determine whether the item is edible or not
     * @return true if the item is edible, false if not
     */
    public boolean getIsEdible() {
        return isEdible;
    }
    
    /**
     * Determine whether the item is a weapon or not
     * @return true if the item is a weapon, false if not
     */
    public boolean getIsWeapon() {
        return isWeapon;
    }
}
