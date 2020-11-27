import java.util.ArrayList;
import java.util.Scanner;

public class Game implements helperInterface {
    private final Witch myWitch;
    private final Witch enemyWitch;
    private final ArrayList<Action> tome;
    private final ArrayList<Action> deliveries;
    private final ArrayList<Action> newDeliveries;
    private Action playedAction;
    private float score;
    private int round;


    public String getCommand() {
        boolean endGame=false; //TODO
        Action nextAction;
        LOGGER.log(2,"round "+round);
        if (round<7) {
            nextAction=getLearnAction();
        } else {
            nextAction= getNextAction(endGame);
        }
        String command=nextAction.getCommand();
        setPlayedAction(nextAction);
        executeAction();
        return command;

    }

    public Action getLearnAction() {
        // have to learn one for each ingredient (ing+>2)
        // get most taxed each time
        Action bestTomeSpell=null;
        int bestTomeSpellValue=0;
        ArrayList<Action> learnableTomeSpell=helperInterface.copyActionArray(tome);
        learnableTomeSpell.removeIf(tomeSpell->!myWitch.canExecuteAction(tomeSpell));
        for (int i=0;i<learnableTomeSpell.size();i++) {
            Action tomeSpell=learnableTomeSpell.get(i);
            int tomeSpellValue=helperInterface.valueOfDelta(tomeSpell.getDelta())-i;
            if (tomeSpellValue>bestTomeSpellValue) {
                bestTomeSpell=tomeSpell;
                bestTomeSpellValue=tomeSpellValue;
            }
        }
        if (learnableTomeSpell.size()==0) {
            bestTomeSpell=myWitch.getSpellForIngredient(0);
        }
        return bestTomeSpell;
    }

    //TODO change parameters
    public Action getNextAction(boolean endGame) {
        return getBestPath(endGame).get(0).getPlayedAction();
    }

    public ArrayList<Game> getBestPath(boolean endGame) {
        return getBestPath(new Game(this), endGame,2);
    }

    //TODO targetscore = actionPrice+initValueOfInventory
    //TODO maybe change deepness to branchValue to match with targetValue (calculated from deepness
    // actionPrice and initValueOfInventory, myWitch score ? idk
    //TODO add endgame -> quickest path to potion
    //TODO actionToPlay = clone(executedAction) so that it's inchanged
    public ArrayList<Game> getBestPath(Game gameState, boolean endGame, int deepness) {
        ArrayList<ArrayList<Game>> branches=new ArrayList<>();
        ArrayList<Action> playableActions = gameState.getAllActions();
        playableActions.removeIf(action -> !myWitch.canExecuteAction(action));
        for (Action action:playableActions) {
            Game newGameState = new Game(gameState);
            newGameState.setPlayedAction(action);
            newGameState.executeAction();
            if (newGameState.isGoodBranch(deepness)) {
                ArrayList<Game> newBranch=new ArrayList<>();
                newBranch.add(newGameState);
                if (deepness>0) {
                    newBranch.addAll(getBestPath(newGameState,endGame,deepness-1));
                }
                branches.add(newBranch);
            }
        }
        return getBestBranchIn(branches);
    }

    public boolean isGoodBranch(int deepness) {
        //TODO implement and rename
        return true;
    }

    public ArrayList<Game> getBestBranchIn(ArrayList<ArrayList<Game>> actionPaths) {
        float bestScore=-10;
        ArrayList<Game> bestPath = null;
        for (ArrayList<Game> actionPath:actionPaths) {
            float actionPathScore=actionPath.get(actionPath.size()-1).getScore();
            if (actionPathScore>bestScore) {bestPath=actionPath;bestScore=actionPathScore;}
        }
        return bestPath;
    }

    public void executeAction() {
        Action actionToPlay = new Action(playedAction);
        float actionScore;
        int[] delta=actionToPlay.getDelta();
        switch (actionToPlay.getActionType()) {
            case "BREW":
                int price=actionToPlay.getPrice();
                deliveries.remove(actionToPlay);
                if (!myWitch.updateInventory(delta)) {
                    int x=0;
                }
                myWitch.addScore(price);
                actionScore=helperInterface.valueOfDelta(delta)+price+3;
                break;
            case "LEARN":
                int[] myWitchInventory = myWitch.getInventory();
                int inventorySize=helperInterface.sumIntArray(myWitchInventory);
                int earnedTaxCount=Math.min(actionToPlay.getTaxCount()-tome.indexOf(actionToPlay),10-inventorySize);
                myWitchInventory[0]+=earnedTaxCount;
                actionToPlay.setActionType("CAST");
                tome.remove(actionToPlay);
                myWitch.addSpell(actionToPlay);
                actionScore=earnedTaxCount+1;
                //TODO implement taxCount add
                // for (int i=0;i<tome.getIndex(action);i++){tome.get(i).addTaxCount(1);}
                // tome.remove(action)
                break;
            case "CAST":
                actionToPlay.setCastable(false);
                if (!myWitch.updateInventory(delta)) {
                    int x=0;
                }
                float valueOfDelta=helperInterface.valueOfDelta(delta);
                actionScore= valueOfDelta-(float)0.5;
                break;
            case "REST":
                actionScore=myWitch.rest();
                break;
            default:
                actionScore=0;
        }
        score+=actionScore;
    }

    public int[] getScoreRange() {
        int scoreDifference=myWitch.getScore()-enemyWitch.getScore();
        int scoreBase;
        if (scoreDifference<0) {
            scoreBase=-scoreDifference;
            if (scoreBase>18) {scoreBase=18;}
        } else {
            scoreBase=6*(Math.max(myWitch.getSoldPotions(),enemyWitch.getSoldPotions())+1);
        }
        return new int[]{scoreBase, scoreBase + 5};
    }

    public ArrayList<Action> getTargetDeliveriesForScoreRange(int min, int max) {
        ArrayList<Action> targetDeliveries =  helperInterface.copyActionArray(deliveries);
        targetDeliveries.removeIf(action -> action.getPrice() > max || action.getPrice() < min);
        if (targetDeliveries.size()==0) { LOGGER.log(0,"No Actions found for range"+min+" "+max);}
        return targetDeliveries;
    }

    public ArrayList<Action> getAllActions() {
        ArrayList<Action> allActions = new ArrayList<>();
        allActions.addAll(tome);
        allActions.addAll(deliveries);
        ArrayList<Action> spells = myWitch.getSpells();
        allActions.addAll(spells);
        for (Action spell:spells) {
            if (spell.isRepeatable()) {
                for (int i=1;i<helperInterface.repeatableTimes(spell);i++) {
                    allActions.add(new Action(spell,i));
                }
            }
        }
        allActions.add(new Action("REST"));
        return allActions;
    }

    public void updateData(Scanner in) {
        round++;
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
            Action action = new Action(actionId, actionType, helperInterface.copyIntArray(delta), price, tomeIndex, taxCount, castable, repeatable);
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

    public void updateEnemyScore() {
        for (int i=0;i<deliveries.size();i++) {
            if (newDeliveries.get(i).getActionId()!=deliveries.get(i).getActionId()) {
                //this delivery index was completed by enemyWitch last round
                enemyWitch.addScore(deliveries.get(i).getPrice());
                return;
            }
        }
    }

    Game() {
        myWitch = new Witch();
        enemyWitch = new Witch();
        tome = new ArrayList<>();
        deliveries = new ArrayList<>();
        newDeliveries = new ArrayList<>();
        score=0;
        round=0;
    }

    Game(Game game) {
        myWitch = new Witch(game.getMyWitch());
        enemyWitch = new Witch(game.getEnemyWitch());
        tome = helperInterface.copyActionArray(game.getTome());
        deliveries = helperInterface.copyActionArray(game.getDeliveries());
        newDeliveries = helperInterface.copyActionArray(game.getNewDeliveries());
        score=game.getScore();
        round=game.getRound();
    }

    public Witch getMyWitch() { return myWitch; }
    public Witch getEnemyWitch() { return enemyWitch; }
    public ArrayList<Action> getTome() { return tome; }
    public ArrayList<Action> getDeliveries() { return deliveries; }
    public ArrayList<Action> getNewDeliveries() { return newDeliveries; }
    public Action getPlayedAction() { return playedAction; }
    public void setPlayedAction(Action playedAction) { this.playedAction = playedAction; }
    public String getMessage() {return LOGGER.getMessage();}
    public float getScore() { return score; }
    public int getRound() { return round; }
}
