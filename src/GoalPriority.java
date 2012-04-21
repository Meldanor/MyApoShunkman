/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public enum GoalPriority {

    // @formatter:off
    /** Walking to something*/
    LOW,
    /** Normal like waiting for a bomb exploding*/
    NORMAL,
    /** Important like getting a goodie*/
    HIGH,
    /** Higher than everything. Only one goal with this should exist once*/
    CRITICAL;
    // @formatter:on

}
