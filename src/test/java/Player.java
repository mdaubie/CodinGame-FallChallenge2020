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







