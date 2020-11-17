import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player implements helperInterface {
    private static final Witch myWitch = new Witch();
    private static final Witch enemyWitch = new Witch();
    private static final Witch[] witches = {myWitch, enemyWitch};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            updateData(in);
            String command = getCommand();
            System.out.println(command+ LOGGER.getMessage());
        }
    }

    public static String getCommand() {
        Action action = getActionToPlay();
        return action.getCommand();
    }

    public static Action getActionToPlay() {
        Action targetAction = getTargetAction();
        int[] neededIngredients = myWitch.getNeededIngredientsFor(targetAction);
        if (Arrays.equals(neededIngredients, new int[4])) { return targetAction; }
        for (int i=3;i>=0;i--) {
            if (neededIngredients[i]>0) {
                Action action = myWitch.getActionForIngredient(i);
                if (myWitch.hasIngredientsForAction(action)) {
                    if (action.isCastable()) {return action;}
                } else if (i>0) { neededIngredients[i-1]+=1; }
            }
        }
        if (myWitch.canRest()) {return new Action("REST");}
        LOGGER.log(1,"Could not get action to play");
        return new Action("WAIT");
    }

    public static Action getTargetAction() {
        return getActionForScore(enemyWitch.getScore()-myWitch.getScore());
    }

    public static Action getActionForScore(int score) {
        //TODO split score in 2 if needed
        if (score<0) {score=0;}
        int minScore=23;
        Action minScoreAction=new Action("WAIT");
        for (Action action:myWitch.getActions()) {
            int price=action.getPrice();
            if (score<price && price<minScore) {
                minScore=price;
                minScoreAction=action;
            }
        }
        LOGGER.log(2,"s="+minScore+" for id="+minScoreAction.getActionId());
        if (minScore==23) {
            LOGGER.log(0,"Could not get action for score "+score);}
        return minScoreAction;
    }

    public static void updateData(Scanner in) {
        int actionCount = in.nextInt();
        myWitch.resetActions();
        LOGGER.clear();
        for (int i = 0; i < actionCount; i++) {
            boolean isForMyWitch = true;
            boolean isForEnemyWitch = true;
            int actionId = in.nextInt();
            String actionType = in.next();
            int delta0 = in.nextInt();
            int delta1 = in.nextInt();
            int delta2 = in.nextInt();
            int delta3 = in.nextInt();
            int price = in.nextInt();
            int tomeIndex = in.nextInt();
            int taxCount = in.nextInt();
            boolean castable = in.nextInt() != 0;
            boolean repeatable = in.nextInt() != 0;
            int[] delta = {delta0, delta1, delta2, delta3};

            if (actionType.equals("CAST")) {
                isForEnemyWitch = false;
            } else if (actionType.equals("OPPONENT_CAST")) {
                actionType = "CAST";
                isForMyWitch=false;
            }
            Action action = new Action(actionId, actionType, delta, price, tomeIndex, taxCount, castable, repeatable);
            if (isForMyWitch) {myWitch.addAction(action);}
            if (isForEnemyWitch) {enemyWitch.addAction(action);}
        }
        for (int i = 0; i < 2; i++) {
            int inv0 = in.nextInt();
            int inv1 = in.nextInt();
            int inv2 = in.nextInt();
            int inv3 = in.nextInt();
            int[] inv = {inv0, inv1, inv2, inv3};
            int score = in.nextInt();
            witches[i].setInventory(inv);
            witches[i].setScore(score);
        }
    }
}

class Witch implements helperInterface {
    private int[] inventory;
    private int score;
    private final ArrayList<Action> actions;
    private static final int inventorySpace=10;
    private int soldPotions=0;

    Witch() {
        this.inventory = new int[4];
        this.score = 0;
        this.actions = new ArrayList<>();
        this.actions.add(new Action("REST"));
    }

    Witch(Witch witch) {
        this.inventory = helperInterface.copyIntArray(witch.getInventory());
        this.score = witch.getScore();
        this.actions = helperInterface.copyActionArray(witch.getActions());
    }

    public int[] getNeededIngredientsFor(Action action) {
        int[] delta = action.getDelta();
        int[] neededIngredients = new int[4];
        for (int i=0;i<delta.length;i++) {
            if (-delta[i]>inventory[i]) {neededIngredients[i]=-delta[i];}
        }
        return neededIngredients;
    }

    public Action getActionForIngredient(int inventoryIndex) {
        for (Action action:actions) {
            if (action.getDelta()[inventoryIndex]>0) {
                return action;
            }
        }
        LOGGER.log(1,"Could not get action for ingredient");
        return null;
    }

    public boolean canRest() {
        for (Action action:actions) {
            if (action.getActionType().equals("CAST")&&!action.isCastable()) { return true; }
        }
        return false;
    }

    public boolean hasIngredientsForAction(Action action) {
        int[] delta = action.getDelta();
        for (int i=0; i<4;i++) {
            if (inventory[i]<-delta[i]) { return false; }
        }
        return true;
    }

    public boolean hasInventorySpaceForAction(Action action) {
        int[] delta = action.getDelta();
        return !(helperInterface.sumIntArray(delta)+helperInterface.sumIntArray(inventory)>inventorySpace);
    }

    public void resetActions() { actions.clear(); addAction(new Action("REST")); }

    public void setInventory(int[] inventory) { this.inventory = inventory; }
    public void setScore(int score) {
        if (score!=this.score) {
            this.score = score;
            soldPotions+=1;
        }
    }
    public void addAction(Action action) { this.actions.add(action); }
    public int[] getInventory() { return inventory; }
    public ArrayList<Action> getActions() { return actions; }
    public int getScore() { return score; }
    public int getSoldPotions() { return soldPotions; }
}

class Action implements helperInterface {
    private final int actionId;
    private final String actionType;
    private final int[] delta;
    private final int price;
    private final int tomeIndex;
    private final int taxCount;
    private boolean castable;
    private final boolean repeatable;

    Action(int actionId, String actionType, int[] delta, int price, int tomeIndex, int taxCount, boolean castable, boolean repeatable) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.delta = delta;
        this.price = price;
        this.tomeIndex = tomeIndex;
        this.taxCount = taxCount;
        this.castable = castable;
        this.repeatable = repeatable;
    }

    Action(Action action) {
        this.actionId = action.actionId;
        this.actionType = action.actionType;
        this.delta = helperInterface.copyIntArray(action.getDelta());
        this.price = action.price;
        this.tomeIndex = action.tomeIndex;
        this.taxCount = action.taxCount;
        this.castable = action.castable;
        this.repeatable = action.repeatable;
    }

    public Action(int actionId, String actionType) {
        this (actionId,actionType,new int[4],0,0,0,false,false);
    }

    public Action(String actionType) {
        this(actionType.equals("REST") ? 0 : -1, actionType.equals("REST") ? "REST" : "WAIT");
    }

    public String getCommand() {
        if (actionId==0) {return "REST";}
        return actionType+" "+actionId;
    }

    public int getActionId() { return actionId; }
    public String getActionType() { return actionType; }
    public int[] getDelta() { return delta; }
    public int getPrice() { return price; }
    public int getTomeIndex() { return tomeIndex; }
    public int getTaxCount() { return taxCount; }
    public boolean isCastable() { return castable; }
    public boolean isRepeatable() { return repeatable; }
    public void setCastable(boolean castable) { this.castable = castable; }
}

interface helperInterface {
    Logger LOGGER = new Logger();

    static int[] copyIntArray(int[] originalArray) {
        int length = originalArray.length;
        int[] newArray = new int[length];
        System.arraycopy(originalArray, 0, newArray, 0, length);
        return newArray;
    }

    static int sumIntArray(int[] intArray) {
        int sum=0;
        for (int value : intArray) { sum += value; }
        return sum;
    }

    static ArrayList<Action> copyActionArray(ArrayList<Action> originalArray) {
        ArrayList<Action> newArray = new ArrayList<>();
        for(Action action : originalArray) {
            newArray.add(new Action(action));
        }
        return newArray;
    }
}

class Logger {
    private static final String[] typeString={"Error","Warning","Info"};
    private String message=" ";
    public String getMessage() { return message; }
    public void log(int logType, String logMessage) {
        switch (logType) {
            case 0:
                System.err.println(typeString[0] +" "+ logMessage);
            case 1:
            case 2:
                this.message += typeString[logType] + ": " + logMessage + "    ";
                break;
            default:
                this.message += typeString[2] + "Unknown error type " + logType;

        }
    }
    public void clear() { message= " "; }
}