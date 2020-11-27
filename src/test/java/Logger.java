class Logger {
    //TODO clean logger, only returns action (actually delete the logger)
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