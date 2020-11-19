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