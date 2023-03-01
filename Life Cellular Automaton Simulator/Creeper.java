import java.util.List;
import java.awt.Color;

/**
 * Write a description of class Creeper here.
 *
 * @author Ahmer Alam & Abu-Bakarr Jalloh
 * @version 2023.02.24
 */
public class Creeper extends Cell
{
    //The maximum number of generations the creeper can be in a dormant state for (before becoming aggro/dead) 
    private static final int MAX_GENERATIONS_DORMANT = 3;
    
    //The minimum number of generations the creeper must be in a dead state for (cannot come back alive instantly)
    private static final int MIN_GENERATIONS_DEAD = 3;
    
    //The number of generations the creeper has been dead for
    private int generationsDead;
    
    //Whether the creeper is in its aggro state or not. A creeper is dormant if aggro = false
    private boolean aggro;
    
    // Whether the creeper will be in its aggro state in the next generation.
    private boolean nextAggro;

    /**
     * Constructor for objects of class CreeperCell
     */

    public Creeper(Field field, Location location) {
        super(field, location, Color.green);
        aggro = false;
    }

    /**
     * 
     * If a creeper is near another aggro creeper, the creeper does NOT die but become aggro too
     * Creating a chain reaction
     * Creeper immune
     */
    public void act() {
        setNeighbours();
        setNextState(false);

        if(isAlive()) {
            if(aggro) {
                nextAggro = false;
            }
            else {
                if(getNeighbours().size() > 0 || aggroCreeperSearch()) {
                    nextAggro = true;
                    setColor(Color.red); //The creeper becomes red when it turns aggro
                    setNextState(true);
                }
                else if(getAge() < MAX_GENERATIONS_DORMANT) {
                    setNextState(true);
                }
            }
        }
        else {
            if(generationsDead >= MIN_GENERATIONS_DEAD && getNeighbours().size() == 0) {
                setNextState(true);
            }
        }
    }
    
    /**
     * Update state of creeper cell 
     * important to update active attribute here and not before
     */
    public void updateState() {
        generationsDead++;
        if(isAlive()) {
            generationsDead = 0;
        }
        super.updateState();
        aggro = nextAggro;
    }
    
    /**
     * Checks if the creeper cell is in its aggro state or not
     * @return true if the creeper cell is in its aggro state
     */
    public boolean isAggro() {
        return aggro;
    }
    
    /**
     * Overrides the superclass method, as creepers are immune to the disease
     */
    public void setInfected() {
        return; //do nothing
    }
}
