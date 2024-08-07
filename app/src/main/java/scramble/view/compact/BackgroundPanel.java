package scramble.view.compact;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import scramble.model.common.api.Pair;
import scramble.model.common.impl.PairImpl;

/**
 * Class for the rappresentation of the Background Panel.
 * 
 * @see GamePanel
 * @see JPanel
 */
public class BackgroundPanel extends GamePanel {

    private static final long serialVersionUID = 1L;
    private static final int NUMBER_OF_STAR_IN_BACKGROUND = 100;

    private final Random rand;
    private transient List<Pair<Integer, Integer>> starPositionXY;

    /** Costructor of the BackgroundPanel class. */
    public BackgroundPanel() {
        this.newStartPositionXY();
        this.rand = new Random();
    }

    private void newStartPositionXY() {
        this.starPositionXY = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawPanel(final Graphics g) {
        if (this.isPanelRepeintable() || starPositionXY.isEmpty()) {
            randomizeStarsPositionXY();
        }

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameView.WINDOW_WIDTH, GameView.WINDOW_WIDTH);

        g.setColor(Color.WHITE);
        for (int i = 0; i < NUMBER_OF_STAR_IN_BACKGROUND; i++) {
            final int x = starPositionXY.get(i).getFirstElement();
            final int y = starPositionXY.get(i).getSecondElement();
            g.fillRect(x, y, 2, 2);
        }
        this.canNotBeRepaint();
    }

    private void randomizeStarsPositionXY() {
        starPositionXY.clear();
        for (int i = 0; i < NUMBER_OF_STAR_IN_BACKGROUND; i++) {
            final int x = rand.nextInt(GameView.WINDOW_WIDTH);
            final int y = rand.nextInt(GameView.WINDOW_WIDTH);
            this.starPositionXY.add(new PairImpl<Integer, Integer>(x, y));
        }
    }

}
