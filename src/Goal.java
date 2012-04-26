/*
 * Copyright (C) 2012 Kilian Gaertner
 * 
 * Dieser Quelltext ist Open Source und kann von jedem verwendet werden, der 
 * folgende Bedingung einhält:
 * Jeder, der den Quelltext, ob in Teilen oder komplett,nutzt, muss dem Inhabenden
 * des Copyrights eine Pizza spendieren, sollte derjenige dem Inhabenden des Copyrights
 * begegnen.
 */

public abstract class Goal implements Comparable<Goal> {

    /** Is the goal canceled? Maybe it it obsolete*/
    private boolean isCancelled = false;

    /**
     * @return Is this goal achieved?
     */
    public abstract boolean isFinished();

    /**
     * Work on this goal
     */
    public abstract void process();

    /**
     * @return Is this goal up to date or old?
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Cancel the goal
     */
    public void setCancelled() {
        isCancelled = true;
    }

    /**
     * @return The priority of this goal
     */
    public abstract GoalPriority getPriority();

    @Override
    /**
     * The priority is important
     */
    public int compareTo(Goal that) {
        return this.getPriority().compareTo(that.getPriority());
    }

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
}
