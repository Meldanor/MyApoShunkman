import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.entity.ApoSkunkmanFire;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */
public class TrollMessageEntity extends ApoSkunkmanFire {

    private final String message;

    private int time;

    private static final int DISPLAY_MESSAGE_TIMER = 2000;

    public TrollMessageEntity(String message) {
        super(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), (ApoSkunkmanConstants.LEVEL_WIDTH / 2) * ApoSkunkmanConstants.TILE_SIZE, (ApoSkunkmanConstants.LEVEL_HEIGHT / 2) * ApoSkunkmanConstants.TILE_SIZE, 1, 1);
        this.message = message;
    }

    @Override
    public void init() {
        super.init();
        setBVisible(true);
        time = DISPLAY_MESSAGE_TIMER;
    }

    public void think(int delta) {
        this.setBVisible((isBVisible() && ((time -= delta) >= 0)));
    }

    private final static Font DISPLAY_FONT = new Font("Serif", Font.BOLD, 20);

    @Override
    public void render(Graphics2D g, int x, int y) {
        if (isBVisible()) {
            g.setColor(Color.RED);
            g.setFont(DISPLAY_FONT);
            g.drawString(message, x, y);
        }
    }
}
