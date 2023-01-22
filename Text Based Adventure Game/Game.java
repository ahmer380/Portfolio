import java.util.Stack;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, characters, items, creates the parser and starts the game.  
 *  It also records a log of all the rooms the player has visited 
 *  It also evaluates and executes the commands that the parser returns.
 * 
 * @author Michael Kölling and David J. Barnes and Ahmer Alam
 * @version 2016.02.29
 */
public class Game 
{
    private boolean gameOver;
    private Parser parser;
    private Player player;
    private Room currentRoom;
    private Stack<Room> roomLog; //records all the rooms the user has visited in order (including repeats)
    private HashMap<String,Room> rooms;

    /**
     * Create the game and initialise its characters, items and internal map.
     */
    public Game() 
    {
        gameOver = false;
        createRooms();
        createCharacters();
        createItems();
        roomLog = new Stack();
        parser = new Parser();
        player = new Player();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        rooms = new HashMap<>();

        // create each room, and add them to the 'rooms' collection
        rooms.put("cell", new Room("inside your own personal cell"));
        rooms.put("corridor", new Room("inside the prison corridor"));
        rooms.put("cafeteria", new Room("inside the cafeteria"));
        rooms.put("kitchen", new Room("inside the kitchen"));
        rooms.put("main entrance", new Room("inside the main entrance"));
        rooms.put("armoury", new Room("Inside the weapon armoury"));
        rooms.put("outside", new Room("You have escaped, congratulations!")); //N/A: Seperate text displayed to user
        rooms.put("teleporter", new Room("Wow! Inside this room you see what appears to be a teleporter..."));

        // initialise room exits
        rooms.get("cell").setExit("north",rooms.get("corridor"));

        rooms.get("corridor").setExit("south", rooms.get("cell"));
        rooms.get("corridor").setExit("east", rooms.get("cafeteria"));
        rooms.get("corridor").setExit("north", rooms.get("main entrance"));
        rooms.get("corridor").setExit("west", rooms.get("armoury"));

        rooms.get("armoury").setExit("east", rooms.get("corridor"));

        rooms.get("cafeteria").setExit("west", rooms.get("corridor"));
        rooms.get("cafeteria").setExit("down", rooms.get("kitchen"));

        rooms.get("kitchen").setExit("up", rooms.get("cafeteria"));
        rooms.get("kitchen").setExit("down", rooms.get("teleporter"));

        rooms.get("main entrance").setExit("south", rooms.get("corridor"));
        rooms.get("main entrance").setExit("north", rooms.get("outside"));

        currentRoom = rooms.get("cell");  // start the game in the user's jail cell
    }

    /**
     * Create all characters inside the game and locate them to their respective rooms
     * Prisoners = Friendly characters
     * Guards/wardens = Enemy characters
     */
    private void createCharacters() {
        Prisoner placeholderPrisoner = new Prisoner("A331");
        placeholderPrisoner.setDialogue("Psst!" + ".\n" + "can you give me some food?" +
            ".\n" + "do so and I can tell you a little secret...");
        placeholderPrisoner.setDialogue("Yum yum, that was delicious! Well I am a man of my word" +
            ".\n" + "You may have stumbled by a key in this prison..." + ".\n" + 
            "If you come across it, pick it up and use it inside your own jail cell" +
            ".\n" + "and something magical will happen!");
        rooms.get("corridor").setPrisoner(placeholderPrisoner);

        placeholderPrisoner = new Prisoner("D143");
        placeholderPrisoner.setDialogue("make sure you SCAN rooms before entering them. These guards and wardens are everywhere it seems!");
        rooms.get("corridor").setPrisoner(placeholderPrisoner);

        // adding the enemies (guard/warden), constructor arguments: isWarden
        rooms.get("armoury").setEnemy(new Enemy(false));
        rooms.get("main entrance").setEnemy(new Enemy(false));
        rooms.get("main entrance").setEnemy(new Enemy(true));
    }

    /**
     * Create all items inside the game and locate them to their respective rooms
     */
    private void createItems() {
        //constructor arguments: name, isEdible, isWeapon, weight
    
        // add takeable items here
        rooms.get("cafeteria").setItem(new Item("pizza",true,false,1));
        rooms.get("cafeteria").setItem(new Item("cake",true,false,2));
        rooms.get("kitchen").setItem(new Item("knife",false,true,3));
        rooms.get("armoury").setItem(new Item("key",false,false,10));
        rooms.get("armoury").setItem(new Item("machete",false,true,2));
        rooms.get("armoury").setItem(new Item("pistol",false,true,3));
        rooms.get("armoury").setItem(new Item("shotgun",false,true,5));

        // add non-takeable items here (weight > player.maxWeight)
        rooms.get("cell").setItem(new Item("bed",false,false,11));
        rooms.get("cafeteria").setItem(new Item("table",false,false,11));
        
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        while (!gameOver) {
            Command command = parser.getCommand();
            processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("You are in the infamous Abashiri Prison, located in the mist-laden lands of Hokkaido, Japan.");
        System.out.println("Suddenly you hear the alarm bells ringing, and your jail cell unlocks. The prison is in anararchy!");
        System.out.println("Is this your moment to finally escape?");
        System.out.println();
        System.out.println("Use items and your fellow prisoners found in rooms to help you plot your escape!");
        System.out.println("BEWARE: Extra guards/wardens are now on patrol around the prison, and are ordered to shoot any prisoners on sight!");
        System.out.println("Prisoners can move around certain rooms, guards and wardens always remain in their outposts");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        printStatus();
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     */
    private void processCommand(Command command) 
    {
        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return;
        }

        String commandWord = command.getCommandWord();
        if(commandWord.equals("help")) {
            printHelp();
        }
        else if(commandWord.equals("go")) {
            goRoom(command);
        }
        else if(commandWord.equals("quit")) {
            quit(command);
        }
        else if(commandWord.equals("back")) {
            back();
        }
        else if(commandWord.equals("talk")) {
            talk(command);
        }
        else if(commandWord.equals("take")) {
            takeItem(command);
        }
        else if(commandWord.equals("drop")) {
            dropItem(command);
        }
        else if(commandWord.equals("scan")) {
            scanEnemies(command);
        }
        else if(commandWord.equals("feed")) {
            feedPrisoner(command);
        }
        else if(commandWord.equals("UNLOCK")) {
            unlockCellExit();
        }
    }

    /**
     * Print out some help information includingthe mechanics of the game
     * And the commands words availalble for the player
     */
    private void printHelp() 
    {
        System.out.println("The prison you are in has erupted into complete chaos!");
        System.out.println("It is your job to find a way to escape, without being caught by the enemies.");
        System.out.println("Prisoners can move around certain rooms, enemies always remain in their outposts.");
        System.out.println();
        System.out.println("If you encounter an enemy when entering a room, weapon items inside "
                           + "your inventory are automatically used to eliminate ALL enemies");
        System.out.println("There are two types of enemies: guards (HP = 1) and wardens (HP = 2)");
        System.out.println("Each weapon item you find (implicit by name) can be used to decrement the HP of an enemy by 1");
        System.out.println("If during an enemy encounter, no.weapons inside inventory < total HP of enemies, you die and the game ends");
        System.out.println();
        System.out.println("Use items and your fellow prisoners found in rooms to help you plot your escape!");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /**
     * Print out information regarding the current state of the game
     * Including the state of the player and the current room the player is inside
     */
    private void printStatus() {
        System.out.println(currentRoom.getLongDescription());
        System.out.println(player.getDescription());
    }
    
    /** 
     * Performs a sequence of processes whenever the player is transferring to another room
     * Whether it is by the player using the go command, back command or otherwise
     * Depending on the what the next room is
     * Also checks if the next room contains any enemies
     * @param nextRoom A room which the user will visit next 
     */
    private void processNextRoom(Room nextRoom) {
        currentRoom = nextRoom;
        movePrisoners();
        if(currentRoom == rooms.get("teleporter")) {
            processTeleporterRoom();
        }
        else if(currentRoom == rooms.get("outside")) {
            processOutsideRoom();
        }
        else {
            printStatus();
            if(currentRoom.getEnemyTypeQuantity("guard") > 0 || currentRoom.getEnemyTypeQuantity("warden") > 0) {
                processGuardEncounter();
            }
        }
    }

    /**
     * For each prisoner in each room, move the prisoner to another
     * Random valid room (deduced from the 'locations' field in the prisoner class)
     */
    private void movePrisoners() {
        for(Room room: new ArrayList<Room>(rooms.values())) {
            ArrayList<Prisoner> prisonersInsideRoom = room.returnAndRemoveAllPrisoners();
            for(Prisoner prisonerToMove: prisonersInsideRoom) {
                Room newRoom = rooms.get(prisonerToMove.selectNewRoom());
                newRoom.setPrisoner(prisonerToMove);
            }
        }
    }
    
    /**
     * Ends the game if the player does not have sufficient weapons to eliminate the enemies
     * Otherwise, the game deletes the enemies from the room and the weapon items used to 
     * Eliminate the enemies
     */
    private void processGuardEncounter() {
        System.out.println("\n *** ENEMY ALERT ***");
        System.out.println("There are " + currentRoom.getEnemyTypeQuantity("guard")
        + " enemy guard(s) and " + currentRoom.getEnemyTypeQuantity("warden")
        + " enemy warden(s) inside the room");
        
        if(currentRoom.getTotalEnemyHealth() > player.getTotalWeaponsInInventory()) {
            System.out.println("Oops, the enemies have overpowered you...");
            System.out.println("You have died.");
            gameOver = true;
            return;
        }
        
        //if we get here, then it is possible that all enemies can be removed with the items in the player's inventory
        String weaponsLost = "";
        while(currentRoom.getEnemyTypeQuantity("guard") > 0 || currentRoom.getEnemyTypeQuantity("warden") > 0) {
            Enemy nextEnemy = currentRoom.getEnemy();
            while(nextEnemy.getHealth() != 0) {
                Item weaponToUse = player.getWeaponItem();
                player.removeItem(weaponToUse);
                weaponsLost += weaponToUse.getName() + " ";
                nextEnemy.decrementHealth();
            }
            currentRoom.removeEnemy(nextEnemy);
        }
        
        System.out.println("Wow! You managed to eliminate all enemies inside the room!");
        System.out.println("Weapons lost: " + weaponsLost);
        System.out.println(player.getDescription());
    }

    /**
     * If the player is currently in the teleporter room
     * Visit any other room in the game
     */
    private void processTeleporterRoom() {
        System.out.println(currentRoom.getShortDescription() + ".\n" +
            "Enter anything to enter!");
        Command notInUse = parser.getCommand();
        System.out.println("Teleporting...");

        //teleport to another random room
        Random randomGenerator = new Random();
        ArrayList<Room> roomValues = new ArrayList<Room>(rooms.values());
        Room randomRoom = roomValues.get(randomGenerator.nextInt(roomValues.size()));
        processNextRoom(randomRoom);
    }
    
    /**
     * If the player is in the 'outside' room, then he/she has won and the game ends
     */
    private void processOutsideRoom() {
        System.out.println("You have escaped Abashiri Prison, congratulations!");
        gameOver = true;
    }
    
    //user commands
    /** 
     * "go" was entered. Try to go in to one direction. If there is an exit, enter the new
     * Room, otherwise print an error message.
     * @param command The command which includes the direction the player wants to go
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave the current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            roomLog.push(currentRoom); // add the old room to the room log
            processNextRoom(nextRoom);
        }
    }

    /**
     * "back" was entered. Check whether there is such room available
     * To re-visit, and do so if true
     */
    private void back() {
        if(roomLog.empty()) {
            System.out.println("You are already at the starting room!");
        }
        else {
            currentRoom = roomLog.pop();
            processNextRoom(currentRoom);
        }
    }

    /**
     * "talk" was entered. Search if the second word (if given) is an ID of a
     * Prisoner located in the current room, and if so, exhaust their current dialogue
     * @param command The command which includes who the player would like to speak to
     */
    private void talk(Command command) {
        String prisonerID = command.getSecondWord();
        if(currentRoom.getPrisoner(prisonerID) == null) {
            // if there is no second word or the prisoner with the input ID
            // is not inside the room, we don't know who to talk to...
            System.out.println("Talk to who?");
            return;
        }

        String dialogue = currentRoom.getPrisoner(prisonerID).getCurrentDialogue();
        System.out.println(dialogue);
    }

    /**
     * "take" was entered. Check if the second word (if given) is an existing item inside
     * The current room and, if the player has sufficient inventory space, transfer the item to the player's inventory
     * @param command The command which includes the item the player would like to take
     */
    private void takeItem(Command command) {
        String itemName = command.getSecondWord();
        Item itemToTake = currentRoom.getItem(itemName);
        if(itemToTake == null) {
            // if there is no second word or the item with the input name
            // is not inside the room, we don't know what to take...
            System.out.println("Pick what up?");
            return;
        }

        if(player.getWeightOfInventory() + itemToTake.getWeight() > player.getMaxWeight()) {
            System.out.println("This item cannot be picked up!");
            if(itemToTake.getWeight() <= player.getMaxWeight()) {
                System.out.println("Perhaps try dropping some items?");
            }
            return;
        }

        currentRoom.removeItem(itemToTake);
        player.setItem(itemToTake);

        System.out.println(itemName + " added into inventory.");
        System.out.println(player.getDescription());
        System.out.println(currentRoom.getItemString());
    }

    /**
     * "scan" was entered. Check that the second and third words have been entered
     * And that the third word is an existing exit for the current room 
     * And if so, scan all enemies in that direction of the type indicated by the second word
     * @param command The command which includes the enemyType and direction the player would like to scan for
     */
    private void scanEnemies(Command command) {
        String enemyType = command.getSecondWord();
        String direction = command.getThirdWord();
        if(enemyType == null) {
            System.out.println("Scan for what?");
            return;
        }

        Room roomToScan = currentRoom.getExit(direction);
        if(roomToScan == null) {
            System.out.println("No such exit exists!");
            return;
        }

        int enemyTypeQuantity = roomToScan.getEnemyTypeQuantity(enemyType);
        System.out.println("There are " + enemyTypeQuantity + " enemy " + enemyType + 
            "(s) inside the room " + direction + " relative to your current location");
    }
    
    /**
     *"feed" was entered. Check that the second and third words have been entered
     * And that the second word is the ID an existing prisoner currently inside the current room 
     * And that the third word is an edible item inside the player's inventory
     * And if so, feed the specified prisoner the specified food item
     * @param command The command which includes the prisonerID and food the player would like feed
     */
    private void feedPrisoner(Command command) {
        String prisonerID = command.getSecondWord();
        String foodName = command.getThirdWord();
        Prisoner prisonerToFeed = currentRoom.getPrisoner(prisonerID);
        Item foodItem = player.getItem(foodName);
        if(prisonerToFeed == null) {
            System.out.println("Feed who?");
            return;
        }
        if(foodItem == null) {
            System.out.println("Feed prisoner " + prisonerID + " what?");
            return;
        }
        if(!foodItem.getIsEdible()) {
            System.out.println("This item is not edible!!!");
            return;
        }
        
        System.out.println("Prisoner " + prisonerID + " is fed! Talk to him again and see what happens..."); 
        prisonerToFeed.advanceDialogue();
        player.removeItem(foodItem);
        System.out.println(foodName + " removed from inventory.");
        System.out.println(player.getDescription());
    }

    /**
     * "drop" was entered. Check if the second word (if given) is an item inside
     * The player's inventory, and if so, transfer the item to the current room
     * @param command The command which includes the item the player would like to drop
     */
    private void dropItem(Command command) {
        String itemName = command.getSecondWord();
        Item itemToDrop = player.getItem(itemName);
        if(itemToDrop == null) {
            // if there is no second word or the item with the input name
            // is not inside the player's inventory, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }
        
        player.removeItem(itemToDrop);
        currentRoom.setItem(itemToDrop);

        System.out.println(itemName + " removed from inventory.");
        System.out.println(player.getDescription());
    }
    
    /**
     * "UNLOCK" was entered. If the key is inside the player's inventory and the player
     * Is currently inside his jail cell, unlock a new exit to the outside room
     * (thus resulting in escape)
     */
    private void unlockCellExit() {
        //need to be in jailcell and have key
        if(currentRoom != rooms.get("cell") || player.getItem("key") == null) {
            System.out.println("What are you trying to do?");
            return;
        }
        
        rooms.get("cell").setExit("up",rooms.get("outside"));
        System.out.println("Wow! A new opening has appeared inside you jail cell");
        printStatus();
    }

    /** 
     * "quit" was entered. Check the rest of the command to see
     * whether we really quit the game and if so, end the game
     */
    private void quit(Command command) {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
        }
        else {
            gameOver = true;  // signal that we want to quit
        }
    }
}
