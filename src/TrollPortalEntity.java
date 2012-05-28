import java.awt.image.BufferedImage;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.entity.ApoSkunkmanEntity;
import apoSkunkman.level.ApoSkunkmanLevel;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class TrollPortalEntity extends ApoSkunkmanEntity {

    private static final long PORTAL_VISIBLE_TIME = 500L;

    private long visibleTimer = PORTAL_VISIBLE_TIME;
    private ApoSkunkmanLevel level;

    public TrollPortalEntity(BufferedImage image, float x, float y, ApoSkunkmanLevel level) {
        super(image, x * ApoSkunkmanConstants.TILE_SIZE, y * ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE);
        this.level = level;
        setBVisible(true);
    }

    @Override
    public void think(int time) {
        if ((visibleTimer -= time) <= 0) {
            setBVisible(false);
            int y = (int) (getY() / ApoSkunkmanConstants.TILE_SIZE);
            int x = (int) (getX() / ApoSkunkmanConstants.TILE_SIZE);
            level.getLevel()[y][x] = null;
        }
    }

}
