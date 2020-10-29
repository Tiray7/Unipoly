package com.example.Softwareproject3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Config {
    // minimum number of players
    public static final int MIN_NUMBER_OF_PLAYERS = 2;

    // start and end field indexes of the board
    public static final int FIELD_MIN = 0;
    public static final int FIELD_MAX = 35;

    public enum FieldLabel {
        GO("go"),
        PROPERTY("property"),
        CHANCE("chance"),
        JUMP("jump"),
        DETENTION("detention"),
        RECESS("recess"),
        VISIT("visit");

        private String name;

        FieldLabel(String name) {
            this.name=name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
