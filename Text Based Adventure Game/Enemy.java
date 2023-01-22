/**
 * Class Enemy - an enemy (currently either a guard/warden) in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * An "Enemy" is a hostile NPC whom can fight against the player whenever they are 
 * inside the same room
 * 
 * @author  Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class Enemy
{
    int health;
    String enemyType;

    /**
     * Constructor for objects of class Enemy
     */
    public Enemy(boolean isWarden)
    {
        if(isWarden) {
            enemyType = "warden";
            health = 2;
        }
        else {
            enemyType = "guard";
            health = 1;
        }
    }
    
    /**
     * Get the type (guard/warden) the enemy is
     * @return The type of enemy (guard/warden)
     */
    public String getEnemyType() {
        return enemyType;
    }
    
    /**
     * Get the health of the enemy
     * @return The health of the enemy
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Decrement the health of the enemy by 1
     */
    public void decrementHealth() {
        health --;
    }
}
