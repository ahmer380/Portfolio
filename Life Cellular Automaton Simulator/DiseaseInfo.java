import java.awt.Color;

/**
 * Data on the behaviour of the disease that strikes all cells apart from Creepers (immune)
 *
 * @author Ahmer Alam & Abu-Bakarr Jalloh
 * @version 2023.02.24
 */
public final class DiseaseInfo
{
    //A cell's color during its aysmptomatic state when infected by the disease 
    public static final Color ASYMPTOMATIC_COLOUR = Color.black;
    
    //A cell's color during its symptomatic state when infected by the disease 
    public static final Color SYMPTOMATIC_COLOUR = Color.blue;
    
    //The number of generations between two seperate waves of the disease
    public static final int DORMANT_GENERATIONS = 30; 
    
    //The (maximum) number of cells initially infected by a wave of the disease
    public static final int INITIAL_INFECTED_COUNT = 1000;
    
    //The number of generations a disease will last before ending
    public static final int MAX_AGE = 10;
    
    //The number of generations the current disease has lasted for
    private static int age;

    /**
     * Constructor for objects of class DiseaseInfo //only used for static purposes (won't ever be called)
     */
    private DiseaseInfo()
    {
        return;
    }

    /**
     * Sets the age of the disease to 0
     */
    public static void setAge()
    {
        age = 0;
    }
    
    /**
     * Increments the age of the disease by 1
     */
    public static void incrementAge() {
        age ++;
    }
    
    /**
     * Gets the age of the disease
     * @return the number of generations the disease has lasted for
     */
    public static int getAge() {
        return age;
    }
    
    /**
     * Checks whether all infected cells can transform from asymptomatic state -> symptomatic state
     * @return true if the transformation is permitted
     */
    public static boolean doImplementSymptom() {
        return age >= MAX_AGE;
    }
    
    /**
     * Checks whether or not the disease has ended
     * @return true if the number of generations the disease has lasted for is larger than the set amount of generations the disease is meant to last for
     */
    public static boolean doDie() {
        return age > MAX_AGE;
    }
}
