import java.util.*;
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player implements helperInterface {
    private static final Witch myWitch = new Witch();
    private static final Witch enemyWitch = new Witch();
    private static final ArrayList<Action> tome = new ArrayList<>();
    private static final ArrayList<Action> deliveries = new ArrayList<>();
    private static final ArrayList<Action> newDeliveries = new ArrayList<>();

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
        if (Arrays.equals(neededIngredients, new int[4])) {
            //my witch can brew the potion
            myWitch.addScore(targetAction.getPrice());
            deliveries.remove(targetAction);
            return targetAction;
        }
        for (int i=3;i>=0;i--) {
            if (neededIngredients[i]>0) {
                Action spell = myWitch.getSpellForIngredient(i);
                if (myWitch.hasIngredientsFor(spell)) {
                    if (spell.isCastable()) {return spell;}
                } else if (i>0) { neededIngredients[i-1]+=1; }
            }
        }
        if (myWitch.canRest()) {return new Action("REST");}
        LOGGER.log(1,"Could not get action to play");
        return new Action("WAIT");
    }

    public static Action getTargetAction() {
        return getDeliveryForScore(enemyWitch.getScore()-myWitch.getScore());
    }

    public static Action getDeliveryForScore(int score) {
        //TODO split score in 2 if needed
        if (score<0) {score=0;}
        int minScore=23;
        Action minScoreAction=new Action("WAIT");
        for (Action delivery:deliveries) {
            int price=delivery.getPrice();
            if (score<price && price<minScore) {
                minScore=price;
                minScoreAction=delivery;
            }
        }
        LOGGER.log(2,"s="+minScore+" for id="+minScoreAction.getActionId());
        if (minScore==23) {
            LOGGER.log(0,"Could not get action for score "+score);}
        return minScoreAction;
    }

    public static void updateData(Scanner in) {
        int actionCount = in.nextInt();
        myWitch.getSpells().clear();
        tome.clear();
        newDeliveries.clear();
        LOGGER.clear();
        for (int i = 0; i < actionCount; i++) {
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

            Action action = new Action(actionId, actionType, delta, price, tomeIndex, taxCount, castable, repeatable);

            switch (actionType) {
                case "CAST":
                    myWitch.addSpell(action);
                    break;
                case "BREW":
                    newDeliveries.add(action);
                    break;
                case "LEARN":
                    tome.add(action);
                    break;
                case "OPPONENT_CAST":
                    //enemyWitch spell
                    break;
                default:
                    LOGGER.log(0,"unknown action type at updateData "+actionType);
            }
        }
        updateEnemyScore();
        deliveries.clear();
        deliveries.addAll(newDeliveries);
        for (int i = 0; i < 2; i++) {
            int inv0 = in.nextInt();
            int inv1 = in.nextInt();
            int inv2 = in.nextInt();
            int inv3 = in.nextInt();
            int[] inv = {inv0, inv1, inv2, inv3};
            int score = in.nextInt();
            if (i==0) {myWitch.setInventory(inv);
            } else {//enemyWitch inv
            }
        }
    }

    public static void updateEnemyScore() {
        for (int i=0;i<deliveries.size();i++) {
            if (newDeliveries.get(i).getActionId()!=deliveries.get(i).getActionId()) {
                //this delivery index was completed by enemyWitch last round
                enemyWitch.addScore(deliveries.get(i).getPrice());
                return;
            }
        }
    }
}







