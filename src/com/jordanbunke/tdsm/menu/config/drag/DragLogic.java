package com.jordanbunke.tdsm.menu.config.drag;

import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.tdsm.menu.config.SequenceEntry;
import com.jordanbunke.tdsm.menu.config.Sequencer;

public final class DragLogic<T> {
    private static final int NONE = -1;

    private boolean moving;
    private int movingIndex, wouldBeIndex;
    private Coord2D onClickPos, lastMousePos;

    private final Sequencer<T> sequencer;

    public DragLogic(final Sequencer<T> sequencer) {
        this.sequencer = sequencer;

        moving = false;
        movingIndex = NONE;
        wouldBeIndex = NONE;
    }

    public void process(
            final InputEventLogger eventLogger, final int index
    ) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (isMoving()) {
            lastMousePos = mousePos;

            for (GameEvent e : eventLogger.getUnprocessedEvents()) {
                if (e instanceof GameMouseEvent me &&
                        me.matchesAction(GameMouseEvent.Action.UP)) {
                    me.markAsProcessed();

                    release();
                    return;
                }
            }

            sequencer.getElement(index).drag();
        } else if (movingIndex != NONE) {
            for (GameEvent e : eventLogger.getUnprocessedEvents()) {
                if (e instanceof GameMouseEvent me &&
                        me.matchesAction(GameMouseEvent.Action.UP)) {
                    me.markAsProcessed();

                    cancel();

                    return;
                }
            }

            if (onClickPos != mousePos)
                startMoving();
        }
    }

    public void prepare(final Coord2D mousePos, final int elementIndex) {
        onClickPos = mousePos;
        movingIndex = elementIndex;
        wouldBeIndex = elementIndex;

        for (SequenceEntry<T> e : sequencer.getElements())
            e.lockPosition();
    }

    private void startMoving() {
        moving = true;
    }

    private void cancel() {
        movingIndex = NONE;
        wouldBeIndex = NONE;
    }

    private void release() {
        final int sourceIndex = movingIndex, destinationIndex = wouldBeIndex;

        moving = false;
        movingIndex = NONE;
        wouldBeIndex = NONE;

        if (destinationIndex != sourceIndex) {
            final SequenceEntry<T> moved = sequencer.getElement(sourceIndex);

            moved.releasedAt(destinationIndex);
            sequencer.refreshScrollBox();
        }
    }

    public boolean isMoving() {
        return moving;
    }

    public Coord2D deltaMousePosition() {
        return lastMousePos.displace(onClickPos.scale(-1));
    }

    public int getMovingIndex() {
        return movingIndex;
    }

    public int getWouldBeIndex() {
        return wouldBeIndex;
    }

    public void setWouldBeIndex(final int wouldBeIndex) {
        this.wouldBeIndex = MathPlus.bounded(0, wouldBeIndex,
                sequencer.getElements().size() - 1);
    }
}
