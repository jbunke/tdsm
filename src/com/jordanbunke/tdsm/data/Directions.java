package com.jordanbunke.tdsm.data;

public record Directions(NumDirs numDirs, boolean horizontal, Dir... order) {

    public enum NumDirs {
        FOUR, SIX, EIGHT;

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
        LEFT, UP, RIGHT, DOWN,
        NW, NE, SE, SW;

        public Dir cw(final NumDirs numDirs) {
            return switch (numDirs) {
                case FOUR -> switch (this) {
                    case LEFT -> UP;
                    case UP -> RIGHT;
                    case RIGHT -> DOWN;
                    default -> LEFT;
                };
                case SIX -> switch (this) {
                    case DOWN -> SW;
                    case SW -> NW;
                    case NW -> UP;
                    case UP -> NE;
                    case NE -> SE;
                    default -> DOWN;
                };
                case EIGHT -> switch (this) {
                    case LEFT -> NW;
                    case UP -> NE;
                    case RIGHT -> SE;
                    case DOWN -> SW;
                    case SW -> LEFT;
                    case NW -> UP;
                    case NE -> RIGHT;
                    case SE -> DOWN;
                };
            };
        }

        public Dir ccw(final NumDirs numDirs) {
            return switch (numDirs) {
                case FOUR -> switch (this) {
                    case LEFT -> DOWN;
                    case DOWN -> RIGHT;
                    case RIGHT -> UP;
                    default -> LEFT;
                };
                case SIX -> switch (this) {
                    case DOWN -> SE;
                    case SE -> NE;
                    case NE -> UP;
                    case UP -> NW;
                    case NW -> SW;
                    default -> DOWN;
                };
                case EIGHT -> switch (this) {
                    case LEFT -> SW;
                    case UP -> NW;
                    case RIGHT -> NE;
                    case DOWN -> SE;
                    case SW -> DOWN;
                    case NW -> LEFT;
                    case NE -> UP;
                    case SE -> RIGHT;
                };
            };
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
            return switch (id) {
                case "w" -> Dir.LEFT;
                case "e" -> Dir.RIGHT;
                case "n" -> Dir.UP;
                default -> Dir.DOWN;
            };
        }
    }
}
