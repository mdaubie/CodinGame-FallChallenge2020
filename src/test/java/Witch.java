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

    public boolean canExecuteAction(Action action) {
        if (action==null) { LOGGER.log(0,"gave null action");return false;}
        boolean canExecuteAction;
        switch (action.getActionType()) {
            case "LEARN":
                canExecuteAction=inventory[0]>action.getTomeIndex();
                break;
            case "CAST":
                canExecuteAction = action.isCastable()&&hasIngredientsFor(action)&&hasInventorySpaceForAction(action);
                break;
            case "BREW":
                canExecuteAction = hasIngredientsFor(action);
                break;
            case "REST":
                canExecuteAction = canRest();
                break;
            default:
                LOGGER.log(0,"Unknown actionType "+action.getActionType()+" line 243");
                canExecuteAction = false;
        }
        return canExecuteAction;
    }

    public boolean canRest() {
        for (Action spell: spells) {
            if (!spell.isCastable()) { return true; }
        }
        return false;
    }

    public int rest() {
        int restedSpells=0;
        for (Action spell: spells) {
            if (!spell.isCastable()) {
                spell.setCastable(true);
                restedSpells++;
            }
        }
        return restedSpells;
    }

    public boolean updateInventory(int[] delta) {
        boolean error=false;
        for (int i=0; i<delta.length;i++) {
            if (inventory[i]<-delta[i]) {
                error=true;
                LOGGER.log(0,"Negative inventory slot");
            }
            inventory[i]+=delta[i];

        }
        return error;
    }

    public boolean hasIngredientsFor(Action action) {
        int[] delta = action.getDelta();
        for (int i=0; i<4;i++) { if (inventory[i]<-delta[i]) { return false; } }
        return true;
    }

    public boolean hasInventorySpaceForAction(Action action) {
        //TODO check that casting a spell throws error if inventory to small
        // this may then be deleted or reduced to sum<10
        int[] delta = action.getDelta();
        return !(helperInterface.sumIntArray(delta)+helperInterface.sumIntArray(inventory)>inventorySpace);
    }

    public Action getSpellForIngredient(int ingIndex) {
        ArrayList<Action> possibleSpells=helperInterface.copyActionArray(spells);
        possibleSpells.removeIf(spell->!canExecuteAction(spell)||spell.getDelta()[0]<=0);
        Action bestSpell=null;
        int bestSpellValue=0;
        for (Action spell:possibleSpells) {
            int spellValue=helperInterface.valueOfDelta(spell.getDelta());
            if (spellValue>bestSpellValue) {
                bestSpellValue=spellValue;
                bestSpell=spell;
            }
        }
        return bestSpell;
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