import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.entity.ApoSkunkmanEntity;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class TrollBackgroundEntity extends ApoSkunkmanEntity {
    
    public TrollBackgroundEntity(BufferedImage tiles, float x, float y) {
        super(tiles, x * ApoSkunkmanConstants.TILE_SIZE, y * ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE, 2, 1000L, 1, false);
    }

    @Override
    public void think(int time) {
        super.think(time);
    }

    @Override
    public void render(Graphics2D g, int x, int y) {
        if (isBVisible() && getIBackground() != null) {
            g.drawImage(this.getImages()[0][getFrame()], (int) (this.getX() + x), (int) (this.getY() + y), null);
        }
    }
}
