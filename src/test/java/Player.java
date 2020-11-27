import java.util.*;
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    private static final Game game = new Game();
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            game.updateData(in);
            String command = game.getCommand();
            String message = game.getMessage();
            System.out.println(command+message);
            //TODO replace LOGGER by command
        }
    }
}







