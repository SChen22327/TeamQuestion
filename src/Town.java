/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private String[] treasureList;
    private String treasure;
    private boolean toughTown;
    private boolean dugGold;
    private static boolean isEasy;
    private boolean searched;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        treasureList = new String[]{"a crown", "a trophy", "a gem", "dust"};
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        dugGold = false;
        isEasy = false;

        treasure = treasureList[(int) (Math.random() * 3)];

        searched = false;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + Colors.PURPLE + item + Colors.RESET + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you have lost your " + Colors.PURPLE + item + Colors.RESET + ".";
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = "You have left the shop.";
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        if (hunter.hasSword()) {
            printMessage = Colors.RED + "The brawler, seeing your sword, realizes he picked a losing fight and gives you his gold" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
            hunter.changeGold(goldDiff);
        } else {
            double noTroubleChance;
            if (toughTown) {
                noTroubleChance = 0.66;
                if (isEasy) {
                    noTroubleChance = 0.4;
                }
            } else {
                noTroubleChance = 0.33;
                if (isEasy) {
                    noTroubleChance = 0.2;
                }
            }

            if (Math.random() > noTroubleChance) {
                printMessage = "You couldn't find any trouble";
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                    hunter.changeGold(-goldDiff);
                }
            }
        }
    }

    public void huntForTreasure() {
        boolean found = hunter.addTreasure(treasure);
        if (searched) {
            printMessage = "You have already searched this town.";
        } else if (!found){
            searched = true;
            if (treasure.equals("dust")) {
                printMessage = "You found dust. Pure garbage, you threw it out as soon as you could.";
            } else {
                printMessage = "You already have " + treasure + " in your inventory.";
            }
        } else {
            searched = true;
            printMessage = "You searched the town and found " + treasure;
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        int rnd = (int) (Math.random() * 6) + 1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        } else if (rnd == 5){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (isEasy) {
            return false;
        }
        double rand = Math.random();
        return (rand < 0.5);
    }

    public void setDugGold() {
        printMessage = "";
        dugGold = true;
    }

    public boolean isDugGold() {
        return dugGold;
    }

    public static void setEasy() {
        isEasy = true;
    }
}