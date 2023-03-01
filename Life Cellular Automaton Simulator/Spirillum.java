import java.util.List;
import java.awt.Color;

/**
 * Spirillum Complex form of life that changes behaviour with Time. 
 *
 * @author Ahmer Alam & Abu-Bakarr Jalloh
 * @version 2023.02.24
 */
public class Spirillum extends Cell
{
    /**
     * Create a new Spirillum.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Spirillum(Field field, Location location) {
        super(field, location, Color.magenta);
    }

    /**
     * This is how the Spirillum decides if it's alive or not
     */
    public void act() {
        setNextState(false);

        super.act();
        if(isStateDetermined()) {
            return;
        }

        if(isAlive()) {
            if(getNeighbours().size() >= getAge() || cellSearchQuantity(Lactobacillus.class) > 1){
                //still be alive if bordering at least 2 Lactobacillus cells, even if not meeting time requirements
                setNextState(true);
            }
        }
        else {
            if(getNeighbours().size() > 5 || cellSearchQuantity(Lactobacillus.class) > 1) {
                //returns alive if it has more than 5 neighbours, or if at least 2 neighbours are Lactobacillus cells
                setNextState(true);
            }
        }
    }
}
