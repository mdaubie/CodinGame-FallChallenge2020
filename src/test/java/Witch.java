import java.util.ArrayList;

class Witch implements helperInterface {
    private int[] inventory;
    private int score;
    private final ArrayList<Action> spells;
    private static final int inventorySpace=10;
    private int soldPotions=0;

    Witch() {
        this.inventory = new int[4];
        this.score = 0;
        this.spells = new ArrayList<>();
    }

    Witch(Witch witch) {
        this.inventory = helperInterface.copyIntArray(witch.getInventory());
        this.score = witch.getScore();
        this.spells = helperInterface.copyActionArray(witch.getSpells());
    }

    public int[] getNeededIngredientsFor(Action action) {
        int[] delta = action.getDelta();
        int[] neededIngredients = new int[4];
        for (int i=0;i<delta.length;i++) {
            if (-delta[i]>inventory[i]) {neededIngredients[i]=-delta[i];}
        }
        return neededIngredients;
    }

    public Action getSpellForIngredient(int ingredientIndex) {
        //TODO should probably remove or improve (include ingredient number)
        // we know have multiple spells for an ingredient
        for (Action spell: spells) {
            if (spell.getDelta()[ingredientIndex]>0) {
                return spell;
            }
        }
        LOGGER.log(1,"Could not get action for ingredient");
        return null;
    }

    public boolean canRest() {
        for (Action spell: spells) {
            if (!spell.isCastable()) { return true; }
        }
        return false;
    }

    public void rest() {
        for (Action spell: spells) {
            spell.setCastable(true);
        }
    }

    public void updateInventory(int[] delta) {
        for (int i=0; i<delta.length;i++) {
            inventory[i]+=delta[i];
            if (inventory[i]<0) { LOGGER.log(0,"Negative inventory slot");}
        }
    }

    public boolean hasIngredientsFor(Action action) {
        int[] delta = action.getDelta();
        delta[0]+=action.getTaxCount();
        for (int i=0; i<4;i++) {
            if (inventory[i]<-delta[i]) { return false; }
        }
        return true;
    }

    public boolean hasInventorySpaceForAction(Action action) {
        int[] delta = action.getDelta();
        return !(helperInterface.sumIntArray(delta)+helperInterface.sumIntArray(inventory)>inventorySpace);
    }

    public void setInventory(int[] inventory) { this.inventory = inventory; }
    public void addScore(int price) {
        score+=price;
        soldPotions+=1;
    }
    public void addSpell(Action action) { this.spells.add(action); }
    public int[] getInventory() { return inventory; }
    public ArrayList<Action> getSpells() { return spells; }
    public int getScore() { return score; }
    public int getSoldPotions() { return soldPotions; }
}