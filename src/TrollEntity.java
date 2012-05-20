import java.awt.image.BufferedImage;

import apoSkunkman.ApoSkunkmanConstants;
import apoSkunkman.entity.ApoSkunkmanGoodie;

/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public class TrollEntity extends ApoSkunkmanGoodie {

    public TrollEntity(BufferedImage animation, float x, float y) {
        super(animation, x * ApoSkunkmanConstants.TILE_SIZE, y * ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE, ApoSkunkmanConstants.TILE_SIZE, 1);
        // TODO Auto-generated constructor stub
    }

//    @Override
//    public int getTimeLeftVisible() {
//        return -;
//    }

}
