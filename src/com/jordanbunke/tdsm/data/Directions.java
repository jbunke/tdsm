package com.jordanbunke.tdsm.data;

import com.jordanbunke.tdsm.util.EnumUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public record Directions(NumDirs numDirs, boolean orientation, Dir... order) {

    public enum NumDirs {
        FOUR(Dir.UP, Dir.RIGHT, Dir.DOWN, Dir.LEFT),
        SIX(Dir.UP, Dir.NE, Dir.SE, Dir.DOWN, Dir.SW, Dir.NW),
        EIGHT(Dir.UP, Dir.NE, Dir.RIGHT, Dir.SE, Dir.DOWN, Dir.SW, Dir.LEFT, Dir.NW);

        NumDirs(final Dir... dirs) {
            included = new HashSet<>();
            included.addAll(Arrays.asList(dirs));
        }

        private final Set<Dir> included;

        // scripting inclusion
        @SuppressWarnings("unused")
        public Set<Dir> getIncluded() {
            return new HashSet<>(included);
        }

        @Override
        public String toString() {
            return switch (this) {
                case FOUR -> "4";
                case SIX -> "6";
                case EIGHT -> "8";
            };
        }
    }

    public enum Dir {
        UP, NE, RIGHT, SE, DOWN, SW, LEFT, NW, INVALID;

        public Dir cw(final NumDirs numDirs) {
            Dir next = this;

            do {
                next = EnumUtils.next(next);
            } while (!numDirs.included.contains(next));

            return next;
        }

        public Dir ccw(final NumDirs numDirs) {
            Dir previous = this;

            do {
                previous = EnumUtils.previous(previous);
            } while (!numDirs.included.contains(previous));

            return previous;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public String name(final Dir dir) {
        if (numDirs == NumDirs.FOUR)
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
}
