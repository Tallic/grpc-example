package at.jku.isse.psma.tutorial.grpc;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

public class StudentTest {


    @Test
    public void askSimpleQuestion_name_success() {
        Student student = new Student();
        student.askSimpleQuestion("What is your name?");
    }

    @Test
    public void askSimpleQuestion_name_fail() {
        Student student = new Student();
        student.askSimpleQuestion("What is my name?");
    }

    @Test
    public void askSimpleQuestion_nameAge_onlyName() {
        Student student = new Student();
        student.askSimpleQuestion("What is your name and your age?");
    }

    @Test
    public void askComplexQuestion_nameAge_success() {
        Student student = new Student();
        student.askComplexQuestion("What is your name and your age?");
    }

    @Test
    public void askMultipleSimpleQuestions_nameAgeMark_success() {
        Student student = new Student();
        student.askMultipleSimpleQuestions(
                ImmutableList.of(
                        "What is your name? What is MDE?",
                        "What is your age?",
                        "What's my mark in this course?"
                )
        );
    }

    @Test
    public void askMultipleComplexQuestions_nameAgeMarkTopic_success() {
        Student student = new Student();
        student.askMultipleComplexQuestions(
                ImmutableList.of(
                        "What is your name and what is your age?",
                        "What's currently my mark in this course and what's the next topic?"
                )
        );
    }

}
