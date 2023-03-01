import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * Lactobacillus. A type of bacteria responsible for the "souring" of milk.
 *
 * @author Ahmer Alam & Abu-Bakarr Jalloh
 * @version 2023.02.24
 */
public class Lactobacillus extends Cell
{
    private static final double INCREMENT_AGE_LIMIT_PROB = 0.35; 
    private static final double DECREMENT_AGE_LIMIT_PROB = 0.15;
    private static final int INITIAL_AGE_LIMIT = 5;

    private int ageLimit; //The number of generations this cell can live before dying
    private Random rand;

    /**
     * Create a new Lactobacillus.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lactobacillus(Field field, Location location) {
        super(field, location, Color.cyan);
        rand = Randomizer.getRandom();
    }

    /**
     * This is how the Lactobacillus decides if it's alive or not
     */
    public void act() {
        setNextState(false);

        super.act();
        if(isStateDetermined()) {
            return;
        }

        double randomNum = rand.nextDouble();

        if(isAlive()) {
            if(randomNum < INCREMENT_AGE_LIMIT_PROB) {
                ageLimit += getNeighbours().size();
            }
            else if(randomNum < INCREMENT_AGE_LIMIT_PROB + DECREMENT_AGE_LIMIT_PROB) {
                ageLimit -= getNeighbours().size();
            }
            else if(cellSearchQuantity(Spirillum.class) != 0) {
                ageLimit = ageLimit / 2; //ageLimit halves if neighbouring at least 1 Spirillum cell (and actions above do not happen)
            }

            if(getAge() < ageLimit) {
                setNextState(true);
            }
        }
        else {
            if(getNeighbours().size() == 2 && cellSearchQuantity(Spirillum.class) == 0) {
                //returns alive if it has exactly 2 neighbours AND neither are Spirillum cells
                setNextState(true);
                resetAgeLimit();
            }
        }
    }

    /**
     * Resets the number of generations this cell can live before dying to the initial amount 
     */
    public void resetAgeLimit() {
        ageLimit = INITIAL_AGE_LIMIT;
    }
}
