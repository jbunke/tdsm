package com.jordanbunke.tdsm.data;

public record Directions(NumDirs numDirs, boolean horizontal, Dir... order) {

    public enum NumDirs {
        FOUR, EIGHT;

        public boolean is4() {
            return this == FOUR;
        }

        @Override
        public String toString() {
            return switch (this) {
                case FOUR -> "4";
                case EIGHT -> "8";
            };
        }
    }

    public enum Dir {
        LEFT, UP, RIGHT, DOWN,
        UL, UR, DR, DL;

        public Dir cw(final NumDirs numDirs) {
            return switch (this) {
                case LEFT -> numDirs.is4() ? UP : UL;
                case UP -> numDirs.is4() ? RIGHT : UR;
                case RIGHT -> numDirs.is4() ? DOWN : DR;
                case DOWN -> numDirs.is4() ? LEFT : DL;
                case DL -> LEFT;
                case UL -> UP;
                case UR -> RIGHT;
                case DR -> DOWN;
            };
        }

        public Dir ccw(final NumDirs numDirs) {
            return switch (this) {
                case LEFT -> numDirs.is4() ? DOWN : DL;
                case UP -> numDirs.is4() ? LEFT : UL;
                case RIGHT -> numDirs.is4() ? UP : UR;
                case DOWN -> numDirs.is4() ? RIGHT : DR;
                case DL -> DOWN;
                case UL -> LEFT;
                case UR -> UP;
                case DR -> RIGHT;
            };
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
