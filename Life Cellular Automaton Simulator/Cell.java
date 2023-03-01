import java.awt.Color;
import java.util.List;

/**
 * A class representing the shared characteristics of all forms of life
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael
 * @version 2022.01.06
 */

public abstract class Cell {
    // The cell's default colour
    private Color defaultColour;

    // The cell's current color
    private Color color;

    // Whether the cell is alive or not.
    private boolean alive;

    // Whether the cell will be alive in the next generation.
    private boolean nextAlive;

    // Whether the cell is infected with the disease or not
    private boolean infected;

    // Whether the cell will be infected in the next generation.
    private boolean nextInfected;

    // The cell's field.
    private Field field;

    // The cell's position in the field.
    private Location location;

    //The number of generations the cell has lived
    private int age;

    //A list of all current adjacent neighbours 
    private List<Cell> neighbours;

    //If the cell's next state is already determined from the super act method
    private boolean stateDetermined;

    /**
     * Create a new cell at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param defaultColour the default color of the cell.
     */
    public Cell(Field field, Location location, Color defaultColour) {
        alive = true;
        nextAlive = false;
        infected = false;
        nextInfected = false;
        this.field = field;
        this.defaultColour = defaultColour;
        setColor(defaultColour);
        setLocation(location);
        setAge();
    }

    /**
     * Make this cell act - that is: the cell decides it's status in the next generation
     * This super method consists of actions required to be checked by all cells (except the creeper)
     * Called by all cells except the Creeper (therefore creeper is immune to the disease)
     * If stateDetermined = true, the cell's state is definately determined
     * Else, the cell still needs to be determined (by its own conditions), may still be dead/alive
     * Order of precedence: creeper, infected, cell-specific rules
     */
    public void act() {
        stateDetermined = false;
        setNeighbours();

        if(aggroCreeperSearch()) {
            stateDetermined = true; //definately dead
        }
        else if(infected) {
            actIfInfected();
            stateDetermined = true;
        } 
        else if(canBecomeInfected()) {
            setInfected();
            stateDetermined = true;
        }
    }

    /**
     * Changes the behaviour of all cells that have been infected by the disease.
     */
    private void actIfInfected() {
        nextInfected = true; //any infected cell remains infected (unless/until it dies)
        if(DiseaseInfo.doDie()) {
            setColor(defaultColour); //before dying, set the cell to it's initial color
        }
        else if(DiseaseInfo.doImplementSymptom()) {
            setNextState(true); //cell survives for one more generation
            setColor(DiseaseInfo.SYMPTOMATIC_COLOUR);
        }
        else {
            setNextState(true); //cell survives for more generation
        }
    }

    /**
     * A cell can only become infected if adjacent to another infected cell AND alive AND before they experience symptoms
     * @return true if it is possible for the cell to become infected trhough one of its neighbours
     */
    private boolean canBecomeInfected() {
        if(!alive || DiseaseInfo.doImplementSymptom()) {
            return false;
        }
        for(Cell aliveCell: neighbours) {
            if(aliveCell.isInfected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search if any neighbour is an active creeper
     * @return true if a neighbour is a creeper cell in its aggro state
     */
    protected boolean aggroCreeperSearch() {
        for(Cell aliveCell: neighbours) {
            if(aliveCell instanceof Creeper) {
                Creeper creeper = (Creeper) aliveCell;
                if(creeper.isAggro()) {
                    return true; //cell will die
                }
            }
        }
        return false;
    }

    /**
     * Search if any neighbour is of a certain type
     * CellType needs to be in the form of Creeper.class etc.
     * @param cellType specified cell type.
     * @return the amount of neighbours of a specified cell type
     */
    protected int cellSearchQuantity(Class<?> cellType) {
        if (cellType == null) {
            return 0; //error handling
        }
        int cellQuantity = 0;
        for(Cell aliveCell: neighbours) {
            if(cellType.isInstance(aliveCell)) {
                cellQuantity++;
            }
        }
        return cellQuantity;
    }

    /**
     * Check whether the cell is alive or not.
     * @return true if the cell is still alive.
     */
    protected boolean isAlive() {
        return alive;
    }

    /**
     * Indicate that the cell is no longer alive.
     */
    protected void setDead() {
        alive = false;
    }

    /**
     * Indicate that the cell will be alive or dead in the next generation.
     * @param value true if cell will be alive in the next generation.
     */
    public void setNextState(boolean value) {
        nextAlive = value;
    }

    /**
     * Changes the state of the cell
     */
    public void updateState() {
        age++;  
        if(alive != nextAlive) {
            //when the cell dies from being alive i.e. reset attributes
            infected = false;
            nextInfected = false;
            setAge();
            setColor(defaultColour); //restore the cell to its initial colour for when it comes back alive
        }
        alive = nextAlive;
        infected = nextInfected;
    }

    /**
     * Changes the color of the cell
     * @param col colour to be set to
     */
    public void setColor(Color col) {
        color = col;
    }

    /**
     * Returns the cell's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Return the cell's location.
     * @return The cell's location.
     */
    protected Location getLocation() {
        return location;
    }

    /**
     * Place the cell at the new location in the given field.
     * @param location The cell's location.
     */
    protected void setLocation(Location location) {
        this.location = location;
        field.place(this, location);
    }

    /**
     * Return the cell's field.
     * @return The cell's field.
     */
    protected Field getField() {
        return field;
    }

    /**
     * Set the age of a cell to 0
     */
    protected void setAge() {
        age = 0;
    }

    /**
     * Return the amount of generations a cell has lived
     * @return the amount of generations a cell has lived
     */
    protected int getAge() {
        return age;
    }

    /**
     * Checks whether or not the cell is infected
     * @return true if the cell is infected
     */
    public boolean isInfected() {
        return infected;
    }

    /**
     * Sets a cell to be infected
     */
    public void setInfected() {
        nextInfected = true;
        setNextState(true); //must be alive for the next round
        setColor(DiseaseInfo.ASYMPTOMATIC_COLOUR);
    }

    /**
     * Checks whether or not a cell gets infected in the next generation
     * @return true if a cell is infected in the next generation
     */
    public boolean isNextInfected() {
        return nextInfected;
    }

    /**
     * Sets the neighbours list to all of the cells neighbours
     */
    protected void setNeighbours() {
        neighbours = getField().getLivingNeighbours(getLocation());
    }

    /**
     * Just returns the attribute
     * @return the attribute of a neighbour
     */
    protected List<Cell>getNeighbours() {
        return neighbours;
    }

    /**
     * Checks whether or not the cells state has been determined
     * @return true if cells state has been determined
     */
    protected boolean isStateDetermined() {
        return stateDetermined;
    }
}
