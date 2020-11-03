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
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static Map<Integer, Field> getInitialBoard() {
        Map<Integer, Field> assignment = new HashMap<>();

        assignment.put(0, new Field(FieldLabel.GO));
        assignment.put(9, new Field(FieldLabel.VISIT));
        assignment.put(18, new Field(FieldLabel.RECESS));
        assignment.put(27, new Field(FieldLabel.DETENTION));

        assignment.put(2, new Field(FieldLabel.CHANCE));
        assignment.put(4, new Field(FieldLabel.CHANCE));
        assignment.put(11, new Field(FieldLabel.CHANCE));
        assignment.put(20, new Field(FieldLabel.CHANCE));
        assignment.put(29, new Field(FieldLabel.CHANCE));
        assignment.put(32, new Field(FieldLabel.CHANCE));

        assignment.put(7, new Field(FieldLabel.JUMP));
        assignment.put(16, new Field(FieldLabel.JUMP));
        assignment.put(25, new Field(FieldLabel.JUMP));
        assignment.put(34, new Field(FieldLabel.JUMP));

        assignment.put(1, new FieldProperty("Geschichte", FieldLabel.PROPERTY, 60, 10, 30, 90, 160, 250));
        assignment.put(3, new FieldProperty("Geogreaphie", FieldLabel.PROPERTY, 60, 20, 60, 180, 320, 450));
        assignment.put(5, new FieldProperty("Theologie", FieldLabel.PROPERTY, 100, 30, 90, 270, 400, 550));
        assignment.put(6, new FieldProperty("Geisteswissenschaften", FieldLabel.PROPERTY, 100, 30, 90, 270, 400, 550));
        assignment.put(8, new FieldProperty("Psychologie", FieldLabel.PROPERTY, 120, 40, 100, 300, 450, 600));
        assignment.put(10, new FieldProperty("Biologie", FieldLabel.PROPERTY, 140, 50, 150, 450, 625, 750));
        assignment.put(12, new FieldProperty("Physik", FieldLabel.PROPERTY, 140, 50, 150, 450, 625, 750));
        assignment.put(13, new FieldProperty("Chemie", FieldLabel.PROPERTY, 160, 60, 180, 500, 700, 900));
        assignment.put(14, new FieldProperty("Umweltwissensschaften", FieldLabel.PROPERTY, 180, 70, 200, 550, 750, 950));
        assignment.put(15, new FieldProperty("Bauingenieurwesen", FieldLabel.PROPERTY, 180, 70, 200, 550, 750, 950));
        assignment.put(17, new FieldProperty("Architektur", FieldLabel.PROPERTY, 200, 80, 220, 600, 800, 1000));
        assignment.put(19, new FieldProperty("Betriebswirtschaftslehre", FieldLabel.PROPERTY, 220, 90, 250, 700, 875, 1050));
        assignment.put(21, new FieldProperty("Volkswirtschaftslehre", FieldLabel.PROPERTY, 220, 90, 250, 700, 875, 1050));
        assignment.put(22, new FieldProperty("Wirtschaftsinformatik", FieldLabel.PROPERTY, 240, 100, 300, 750, 925, 1100));
        assignment.put(23, new FieldProperty("Elektrotechnik", FieldLabel.PROPERTY, 260, 110, 330, 800, 975, 1150));
        assignment.put(24, new FieldProperty("Maschienenbau", FieldLabel.PROPERTY, 260, 110, 330, 800, 975, 1150));
        assignment.put(26, new FieldProperty("Informatik", FieldLabel.PROPERTY, 280, 120, 360, 850, 1025, 1200));
        assignment.put(28, new FieldProperty("Veterinärmedizin", FieldLabel.PROPERTY, 300, 130, 390, 900, 1100, 1275));
        assignment.put(30, new FieldProperty("Biomedizin", FieldLabel.PROPERTY, 300, 130, 390, 900, 1100, 1275));
        assignment.put(31, new FieldProperty("Humanmedizin", FieldLabel.PROPERTY, 320, 150, 450, 1000, 1200, 1400));
        assignment.put(33, new FieldProperty("Aviatik", FieldLabel.PROPERTY, 350, 175, 500, 1100, 1300, 1500));
        assignment.put(35, new FieldProperty("Rechtswissenschaften", FieldLabel.PROPERTY, 400, 200, 600, 1400, 1700, 2000));

        return Collections.unmodifiableMap(assignment);
    }
}
