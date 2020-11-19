import java.util.ArrayList;

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