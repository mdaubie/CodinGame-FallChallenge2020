import java.io.IOException;

import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Fall2020Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        //Choose league level
        gameRunner.setLeagueLevel(3);

        //Add players
        gameRunner.addAgent(BasicAgent.class, "Kotake");
        gameRunner.addAgent(Player.class, "Pakme");

        //Set game seed
        gameRunner.setSeed(5842184981578562716L);

        //Run game and start viewer on 'http://localhost:8888/'
        gameRunner.start(8888);
    }
}
