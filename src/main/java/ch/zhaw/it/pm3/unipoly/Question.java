package ch.zhaw.it.pm3.unipoly;

import java.util.Hashtable;

/**
 * This class is for questions, which players have to answer when they land on someones property.
 */
public class Question {
    String question;
    Hashtable<Integer, String> answerOptions = new Hashtable<>();
    int solution;
    String option1;
    String option2;
    String option3;

    /**
     * The constructor sets the datafields of the class and puts the quiz options into a hashmap
     *
     * @param question is the string of the question
     * @param solution is the number of which option is correct
     * @param option1  is the first option
     * @param option2  is the second option
     * @param option3  is the third option
     */
    public Question(String question, int solution, String option1, String option2, String option3) {
        this.question = question;
        this.solution = solution;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        answerOptions.put(1, this.option1);
        answerOptions.put(2, this.option2);
        answerOptions.put(3, this.option3);
    }

    public String getQuestion() {
        return question;
    }

    public Hashtable<Integer, String> getAnswerOptions() {
        return answerOptions;
    }

    public int getSolution() {
        return solution;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }
}
