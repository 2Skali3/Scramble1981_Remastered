/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package scramble;

import scramble.controller.input.impl.InputControlImpl;
import scramble.view.compact.GameView;

/**
 * Main class for the game. It instantiate the Game View and the controller,
 * that handles the game inputs.
 */
public class App {

    /**
     * Auto-generated test.
     * 
     * @return a std greeting
     */
    public String getGreeting() {
        return "Hello World!";
    }

    /**
     * Main method.
     * 
     * @param args standard main args
     */
    public static void main(final String[] args) {

        final GameView gameView = new GameView();
        final InputControlImpl controller = new InputControlImpl(gameView);
        gameView.addKeyListener(controller);
    }
}
