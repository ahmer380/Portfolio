import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A Life (Game of Life) simulator, first described by British mathematician
 * John Horton Conway in 1970.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael
 * @version 2022.01.06
 * 
 * NOTE: Cell spawning probabilities can be tweaked for experimentation
 */

public class Simulator {
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 125;

    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;

    // The probability that a Mycoplasma is alive
    private static final double MYCOPLASMA_ALIVE_PROB = 0.2; 

    //The probability that a NonDeterministc is alive
    private static final double LACTOBACILLUS_ALIVE_PROB = 0.3; 

    //The probability that a NonDeterministc is alive
    private static final double SPIRILLUM_ALIVE_PROB = 0.2; 

    //The probability that a Creeper is alive
    private static final double CREEPER_ALIVE_PROB = 0.05; 

    // List of cells in the field.
    private List<Cell> cells;

    // The current state of the field.
    private Field field;

    // The current generation of the simulation.
    private int generation;

    // A graphical view of the simulation.
    private SimulatorView view;

    // A random generator from the Randomizer class
    private Random rand;

    /**
     * Execute simulation
     */
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.simulate(100);
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        cells = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);

        rand = Randomizer.getRandom();

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 generations).
     */
    public void runLongSimulation() {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of
     * generations.  Stop before the given number of generations if the
     * simulation ceases to be viable.
     * @param numGenerations The number of generations to run for.
     */
    public void simulate(int numGenerations) {
        for (int gen = 1; gen <= numGenerations && view.isViable(field); gen++) {
            simOneGeneration();
            delay(500);   // comment out to run simulation faster
        }
    }

    /**
     * Run the simulation from its current state for a single generation.
     * Iterate over the whole field updating the state of each life form.
     */
    public void simOneGeneration() {
        generation++;
        initiateDisease();

        for (Iterator<Cell> it = cells.iterator(); it.hasNext(); ) {
            Cell cell = it.next();
            cell.act();
        }

        for (Cell cell : cells) {
            cell.updateState();
        }
        view.showStatus(generation, field);
    }

    /**
     * Randomly pick some cells to be infected by the disease
     * NOTE: Some cells may be picked twice, and some picked may not be alive
     * Therefore, infected cells < DiseaseInfo.INITIAL_INFECTED_COUNT (increase randomness)
     */
    private void initiateDisease() {
        DiseaseInfo.incrementAge();
        if(generation % DiseaseInfo.DORMANT_GENERATIONS == 0) {
            DiseaseInfo.setAge();
            for(int count = 0; count < DiseaseInfo.INITIAL_INFECTED_COUNT; count++) {
                Cell cell = cells.get(rand.nextInt(cells.size()));
                if(cell.isAlive()) {
                    cell.setInfected(); //only alive cells can be infected
                }
            }
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        generation = 0;
        cells.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(generation, field);
    }

    /**
     * Randomly populate the field live/dead life forms
     */
    private void populate() {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                double randomNum = rand.nextDouble();
                if(randomNum <= MYCOPLASMA_ALIVE_PROB) {
                    cells.add(new Mycoplasma(field, location));
                }
                else if(randomNum <= MYCOPLASMA_ALIVE_PROB + LACTOBACILLUS_ALIVE_PROB) {
                    cells.add(new Lactobacillus(field, location));
                }
                else if(randomNum <= MYCOPLASMA_ALIVE_PROB + LACTOBACILLUS_ALIVE_PROB + SPIRILLUM_ALIVE_PROB) {
                    cells.add(new Spirillum(field, location));
                }
                else if(randomNum <= MYCOPLASMA_ALIVE_PROB + LACTOBACILLUS_ALIVE_PROB + SPIRILLUM_ALIVE_PROB + CREEPER_ALIVE_PROB) {
                    cells.add(new Creeper(field, location));
                }
                else {
                    cells.add(new Mycoplasma(field, location));
                    field.getObjectAt(location).setDead(); //empty cells are set as dead myco cells
                }
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
