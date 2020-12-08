package ch.zhaw.it.pm3.unipoly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Config {

    //start and end field indexes of the board.
    public static final int FIELD_MIN = 0;
    public static final int FIELD_MAX = 35;
    public static final int AMOUNT_OF_MODULES = 7;
    public static final int RANSOM = 100;
    public static final int COST_FOR_JUMP = 100;
    public static final int GO_MONEY = 400;
    public static final int BANK_START_MONEY = 2000000;


    /***
     * FieldLabel enum to define labels
     */
    public enum FieldLabel {
        GO("go"), PROPERTY("property"), CHANCE("chance"), JUMP("jump"), DETENTION("detention"), RECESS("recess"),
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

    /**
     * Modes for the game, singleplayer or multiPlayer.
     */
    enum Gamemode {
        SINGLE, MULTI
    }

    /**
     * Labels for the token icon.
     */
    public enum TokenType {
        ATOM, ONEPLUS, EINSTEIN, CRADLE, EQUATION, HELIUM, LAB, LIGHTBULB, BANK, NPCI, NPCII, NPCIII
    }

    /**
     * Types of cards for different actions.
     */
    public enum ChanceCardType {
        TODETENTION, PAYMONEY, RECEIVEMONEY, DETENTIONFREECARD
    }

    /***
     * UnipolyPhase to define which phase in the game is
     */
    public enum UnipolyPhase {
        SHOWANDSWITCH, WAITING, ROLLING, BUY_PROPERTY, DETENTION,
        SHOWCARD, QUIZTIME, JUMP, INDEBT, GAMEOVER, ERROR
    }

    /***
     * Initialize the Board
     *
     * @return the board
     */
    public static Map<Integer, Field> getInitialBoard() {
        Map<Integer, Field> assignment = new HashMap<>();

        assignment.put(0, new Field(FieldLabel.GO, "Das Los-Feld"));
        assignment.put(9, new Field(FieldLabel.VISIT, "Zu Besuch beim Nachsitzen"));
        assignment.put(18, new Field(FieldLabel.RECESS, "Mach eine Pause"));
        assignment.put(27, new Field(FieldLabel.DETENTION, "Spieler muss zum Nachsitzen"));

        assignment.put(2, new Field(FieldLabel.CHANCE, "Ziehe eine Chance-Karte"));
        assignment.put(4, new Field(FieldLabel.CHANCE, "Ziehe eine Chance-Karte"));
        assignment.put(11, new Field(FieldLabel.CHANCE, "Ziehe eine Chance-Karte"));
        assignment.put(20, new Field(FieldLabel.CHANCE, "Ziehe eine Chance-Karte"));
        assignment.put(29, new Field(FieldLabel.CHANCE, "Ziehe eine Chance-Karte"));
        assignment.put(32, new Field(FieldLabel.CHANCE, "Ziehe eine Chance-Karte"));

        assignment.put(7, new Field(FieldLabel.JUMP, "Spring auf ein anderes Feld"));
        assignment.put(16, new Field(FieldLabel.JUMP, "Spring auf ein anderes Feld"));
        assignment.put(25, new Field(FieldLabel.JUMP, "Spring auf ein anderes Feld"));
        assignment.put(34, new Field(FieldLabel.JUMP, "Spring auf ein anderes Feld"));

        assignment.put(1, new FieldProperty("Geschichte", FieldLabel.PROPERTY, 60, 10, 30, 90, 160, 250, 0));
        assignment.put(3, new FieldProperty("Geographie", FieldLabel.PROPERTY, 60, 20, 60, 180, 320, 450, 0));
        assignment.put(5, new FieldProperty("Theologie", FieldLabel.PROPERTY, 100, 30, 90, 270, 400, 550, 1));
        assignment.put(6, new FieldProperty("Geisteswissenschaften", FieldLabel.PROPERTY, 100, 30, 90, 270, 400, 550, 1));
        assignment.put(8, new FieldProperty("Psychologie", FieldLabel.PROPERTY, 120, 40, 100, 300, 450, 600, 1));
        assignment.put(10, new FieldProperty("Biologie", FieldLabel.PROPERTY, 140, 50, 150, 450, 625, 750, 2));
        assignment.put(12, new FieldProperty("Physik", FieldLabel.PROPERTY, 140, 50, 150, 450, 625, 750, 2));
        assignment.put(13, new FieldProperty("Chemie", FieldLabel.PROPERTY, 160, 60, 180, 500, 700, 900, 2));
        assignment.put(14, new FieldProperty("Umweltingenieurwissensschaften", FieldLabel.PROPERTY, 180, 70, 200, 550, 750, 950, 3));
        assignment.put(15, new FieldProperty("Bauingenieurwesen", FieldLabel.PROPERTY, 180, 70, 200, 550, 750, 950, 3));
        assignment.put(17, new FieldProperty("Architektur", FieldLabel.PROPERTY, 200, 80, 220, 600, 800, 1000, 3));
        assignment.put(19, new FieldProperty("Betriebswirtschaftslehre", FieldLabel.PROPERTY, 220, 90, 250, 700, 875, 1050, 4));
        assignment.put(21, new FieldProperty("Volkswirtschaftslehre", FieldLabel.PROPERTY, 220, 90, 250, 700, 875, 1050, 4));
        assignment.put(22, new FieldProperty("Wirtschaftsinformatik", FieldLabel.PROPERTY, 240, 100, 300, 750, 925, 1100, 4));
        assignment.put(23, new FieldProperty("Elektrotechnik", FieldLabel.PROPERTY, 260, 110, 330, 800, 975, 1150, 5));
        assignment.put(24, new FieldProperty("Maschienenbau", FieldLabel.PROPERTY, 260, 110, 330, 800, 975, 1150, 5));
        assignment.put(26, new FieldProperty("Informatik", FieldLabel.PROPERTY, 280, 120, 360, 850, 1025, 1200, 5));
        assignment.put(28, new FieldProperty("Veterinärmedizin", FieldLabel.PROPERTY, 300, 130, 390, 900, 1100, 1275, 6));
        assignment.put(30, new FieldProperty("Biomedizin", FieldLabel.PROPERTY, 300, 130, 390, 900, 1100, 1275, 6));
        assignment.put(31, new FieldProperty("Humanmedizin", FieldLabel.PROPERTY, 320, 150, 450, 1000, 1200, 1400, 6));
        assignment.put(33, new FieldProperty("Aviatik", FieldLabel.PROPERTY, 350, 175, 500, 1100, 1300, 1500, 7));
        assignment.put(35, new FieldProperty("Rechtswissenschaften", FieldLabel.PROPERTY, 400, 200, 600, 1400, 1700, 2000, 7));

        return Collections.unmodifiableMap(assignment);
    }

    /***
     * get the chance Cards
     * 
     * @return the cards
     */
    public static ArrayList<ChanceCards> getChanceCards() {
        ArrayList<ChanceCards> assignment = new ArrayList<>();

        assignment.add(new ChanceCards("	Du musst deinen Laptop reparieren lassen, weil du Kaffe darüber verschüttet hast. Zahle CHF 80.	", ChanceCardType.PAYMONEY, 80));
        assignment.add(new ChanceCards("	Du hast ein Buch aus der Bibliothek verloren. Zahle CHF 100.	", ChanceCardType.PAYMONEY, 100));
        assignment.add(new ChanceCards("	Zahle deine Semestergebühr von CHF 700.	", ChanceCardType.PAYMONEY, 700));
        assignment.add(new ChanceCards("	Du gehst Mittagessen mit deiner Projektgruppe. Zahle CHF 20.	", ChanceCardType.PAYMONEY, 20));
        assignment.add(new ChanceCards("	Du hilfst bei einem Event der Universität aus. Ziehe CHF 200 ein.	", ChanceCardType.RECEIVEMONEY, 200));
        assignment.add(new ChanceCards("	Du gibts Nachhilfeunterricht. Ziehe CHF 100 ein.	", ChanceCardType.RECEIVEMONEY, 100));
        assignment.add(new ChanceCards("	Du hast den ersten Preis in einem Wettbewerb gewonnen. Ziehe CHF 200 ein.	", ChanceCardType.RECEIVEMONEY, 200));
        assignment.add(new ChanceCards("	Von deinem Nebenjob verdienst du CHF 400.	", ChanceCardType.RECEIVEMONEY, 400));
        assignment.add(new ChanceCards("	Du wurdest beim Spicken erwischt. Gehe in das Nachsitzen. 	", ChanceCardType.TODETENTION, 0));
        assignment.add(new ChanceCards("	Du kommst aus dem Nachsitzen frei.	", ChanceCardType.DETENTIONFREECARD, 0));

        return assignment;
    }

    public static HashMap<Integer, Question> getQuestionCards() {
        HashMap<Integer, Question> assignment = new HashMap<>();
        assignment.put(1, new Question("Wann fing der Erste Weltkrieg an?", 1, "1914", "1897", "1935 "));
        assignment.put(3, new Question("Welcher ist der grösste Kontinent?", 2, "Amerika", "Asien", "Australien "));
        assignment.put(5, new Question("Was bedeutet katholisch?", 3, "Heilig", "Auserwählt", "Allumfassend "));
        assignment.put(6, new Question("Was ist das Objekt der Geisteswissenschaften?", 1, "Der Mensch", "Der Körper", "Die Seele"));
        assignment.put(8, new Question("Der russische Verhaltensforscher Iwan Pawlow entdeckte das Prinzip der Klassischen Konditionierung durch Experimenten an welchem Tier?", 2, "Maus", "Hund", "Katze "));
        assignment.put(10, new Question("Die Biologie ist dem altgriechischen Wortursprung βίος (bíos) nach die Lehre wovon? ", 3, "Welt", "Natur", "Leben "));
        assignment.put(12, new Question("Welcher englische Forscher hätte der Legende nach den Fall eines Apfels vom Apfelbaum beobachtet, was ihn zur Formulierung seines Gravitationsgesetzes inspiriert hätte?", 1, "Isaac Newton", "Ernest Rutherford", "Michael Faraday "));
        assignment.put(13, new Question("Welches Element ist mit ungefähr 78 Prozent Volumsanteil der Hauptbestandteil unserer Atmosphäre?", 2, "Sauerstoff", "Stickstoff", "Wasserstoff "));
        assignment.put(14, new Question("Wie nennt man schmutzige Luft, die besonders in Großstädten durch Autoabgase auftritt?", 3, "Rauch", "Nebel", "Smog "));
        assignment.put(15, new Question("Welcher der genannten Steine ist kein Naturstein?", 1, "Kalksandstein", "Basalt", "Marmor "));
        assignment.put(17, new Question("Was gilt als ein typisches Kennzeichen romanischer Bauten?", 2, "Flachdächer", "Rundbögen", "Volutengiebel "));
        assignment.put(19, new Question("Wie viele Bedürfnisse gibt es nach der sogenannten Maslow'sche Bedürfnispyramide?", 3, "3", "4", "5 "));
        assignment.put(21, new Question("Welches der folgenden Wirtschaftsgüter ist durch Konsumausschluss und fehlende Konsumrivalität charakterisiert?", 1, "Clubgut", "Allmendegut", "Öffentliches Gut "));
        assignment.put(22, new Question("Welche Software braucht ein Unternehmen für die Planung und -Kontrolle?", 2, "Screensaver", "Buchaltungssoftware", "Wireless-LAN "));
        assignment.put(23, new Question("Wie heißt der nach einem Österreicher benannte Effekt, der die Frequenzveränderung eines Signals bei sich verändernder Distanz zwischen Sender und Empfänger beschreibt? Ein typisches Beispiel ist ein vorbeifahrender Rettungswagen mit eingeschaltetem Folgetonhorn.", 3, "Spritzer-Effekt", "Hülsen-Effekt", "Doppler-Effekt "));
        assignment.put(24, new Question("Was wandelt ein elektrischer Motor um?", 1, "Elektrische Energie in mechanische Energie", "Mechanische Energie in elektrische Energie", "Magnetische Energie in elektrische Energie "));
        assignment.put(26, new Question("Was ist Java?", 2, "Content Management System", "Objektorientierte Programmiersprache", "Shopsystem "));
        assignment.put(28, new Question("Balaenoptera musculus gilt als das größte und schwerste Tier aller Zeiten – unter welchem Namen kennen wir diesen Riesen?", 3, "Megalodon", "Diplodocus", "Blauwal "));
        assignment.put(30, new Question("Wie viele Knochen besitzt der Mensch?", 1, "210", "175", "305 "));
        assignment.put(31, new Question("Welche Krankheit wurde früher auch als Schwindsucht bezeichnet ? ", 2, "Malaria", "Tuberkulose", "Lepra "));
        assignment.put(33, new Question("Welchen Wolkentyp mögen Segelflieger tendenziell am meisten?", 3, "Cumulonimbuswolken mit großer Höhe.", "Cirruswolken", "Dicke Cumuluswolken mit sichtbarem Abstand. "));
        assignment.put(35, new Question("Das öffentliches Recht umfasst …", 1, "Bundesverfassung, gerichtlicher Nachlassvertrag, Zivilprozessrecht, Völkerrecht", " Steuerrecht, aussergerichtlicher Nachalssvertrag, Strafgesetz, Krankenversicherungsgesetz KVG", " Kantonsverfassung, Planung und Baurecht, Sachenrecht Europäische Menschenrechtskonvention "));
        return assignment;
    }
}