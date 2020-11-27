class Action implements helperInterface {
    private final int actionId;
    private String actionType;
    private final int[] delta;
    private final int price;
    private final int tomeIndex;
    private final int taxCount;
    private boolean castable;
    private final boolean repeatable;
    private final int repeatAction;

    Action(int actionId, String actionType, int[] delta, int price, int tomeIndex, int taxCount, boolean castable, boolean repeatable) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.delta = delta;
        this.price = price;
        this.tomeIndex = tomeIndex;
        this.taxCount = taxCount;
        this.castable = castable;
        this.repeatable = repeatable;
        this.repeatAction = 1;
    }

    Action(Action action) {
        this(action,action.getRepeatAction());
    }

    Action(Action action, int repeatAction) {
        this.actionId = action.getActionId();
        this.actionType = action.getActionType();
        this.delta = helperInterface.copyIntArray(action.getDelta());
        this.price = action.getPrice();
        this.tomeIndex = action.getTomeIndex();
        this.taxCount = action.getTaxCount();
        this.castable = action.isCastable();
        this.repeatable = action.isRepeatable();
        this.repeatAction = repeatAction;
    }

    public Action(int actionId, String actionType) {
        this (actionId,actionType,new int[4],0,0,0,false,false);
    }

    public Action(String actionType) {
        this(actionType.equals("REST") ? 0 : -1, actionType.equals("REST") ? "REST" : "WAIT");
    }

    public String getCommand() {
        if (actionId==0) {return "REST";}
        String command = actionType+" "+actionId;
        if (actionType.equals("CAST")&&repeatable) { command+=" "+repeatAction; }
        return command;
        //TODO take repeatActionCount in count -> move to another class or add it back to action by some way
    }

    public int getActionId() { return actionId; }
    public String getActionType() { return actionType; }
    public int[] getDelta() { return helperInterface.multiplyIntArray(delta,repeatAction); }
    public int getPrice() { return price; }
    public int getTomeIndex() { return tomeIndex; }
    public int getTaxCount() { return taxCount; }
    public boolean isCastable() { return castable; }
    public boolean isRepeatable() { return repeatable; }
    public void setCastable(boolean castable) { this.castable = castable; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public int getRepeatAction() { return repeatAction; }
}