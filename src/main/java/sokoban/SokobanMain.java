package sokoban;

import com.codingame.gameengine.runner.SoloGameRunner;

public class SokobanMain {
    public static void main(String[] args) {
        SoloGameRunner gameRunner = new SoloGameRunner();
        gameRunner.setAgent(Agent.class);
        gameRunner.setTestCase("test13.json");
        Convertisseur.ConvertionJsonPddl("test13.json");
        System.out.println("La solution : "+ Convertisseur.getSolution());
        gameRunner.start();
    }
}
