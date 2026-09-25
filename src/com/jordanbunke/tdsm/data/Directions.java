package com.jordanbunke.tdsm.data;

import com.jordanbunke.tdsm.util.EnumUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Directions {
    // scripting inclusion
    @SuppressWarnings("unused")
    public static final int MAX_DIRECTIONS = 8;

    public enum Dir {
        UP, NE, RIGHT, SE, DOWN, SW, LEFT, NW, INVALID;

        public Dir cw(final Directions directions) {
            Dir next = this;

            do {
                next = EnumUtils.next(next);
            } while (!directions.included.contains(next));

            return next;
        }

        public Dir ccw(final Directions directions) {
            Dir previous = this;

            do {
                previous = EnumUtils.previous(previous);
            } while (!directions.included.contains(previous));

            return previous;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public final boolean orientation;
    public final Dir[] order;

    private final Set<Dir> included;
    public final boolean noDuplicates;

    public Directions(
            final boolean orientation,
            final Dir... order
    ) {
        this.orientation = orientation;
        this.order = order;

        included = new HashSet<>();
        included.addAll(Arrays.asList(order));

        noDuplicates = order.length == included.size();
    }

    public String name(final Dir dir) {
        if (order.length <= 4)
            return dir.toString();
        else {
            return switch (dir) {
                case LEFT -> "w";
                case RIGHT -> "e";
                case UP -> "n";
                case DOWN -> "s";
                default -> dir.toString();
            };
        }
    }

    public static Dir get(final String id) {
        try {
            return Dir.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return switch (id.toLowerCase()) {
                case "w" -> Dir.LEFT;
                case "e" -> Dir.RIGHT;
                case "n" -> Dir.UP;
                case "s" -> Dir.DOWN;
                default -> Dir.INVALID;
            };
        }
    }

    // scripting inclusion
    @SuppressWarnings("unused")
    public boolean containsInvalid() {
        for (final Dir dir : order)
            if (dir == Dir.INVALID)
                return true;

        return false;
    }
}
