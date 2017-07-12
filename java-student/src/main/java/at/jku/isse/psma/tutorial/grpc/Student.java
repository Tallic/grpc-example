package at.jku.isse.psma.tutorial.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class Student {

    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    private final ProfessorServiceGrpc.ProfessorServiceStub asyncProfessor;

    public Student() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 50051).build();
        asyncProfessor = ProfessorServiceGrpc.newStub(channel);
    }

    private class LoggerStreamObserver implements StreamObserver<Answer> {
        @Override
        public void onNext(final Answer value) {
            logger.info("Professor: {}", value.getText());
        }

        @Override
        public void onError(final Throwable t) {
            logger.error("Something disturbed the classroom.", t);
        }

        @Override
        public void onCompleted() {
            logger.info("Java Student: Thank you professor.");
        }
    }

    private Question newQuestion(String question) {
        return Question.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setText(question)
                .build();
    }

    public void askSimpleQuestion(String question) {
        logger.info("Asking the professor a simple question.");

        Question questionProto = newQuestion(question);

        asyncProfessor.askQuestionWithAnswer(questionProto, new LoggerStreamObserver());

        logger.info("Finished asking.");
    }

    public void askMultipleSimpleQuestions(List<String> questions) {
        logger.info("Asking the professor multiple simple questions.");

        StreamObserver<Question> requestStreamObserver = asyncProfessor.askQuestionsWithAnswer(new LoggerStreamObserver());

        questions.stream()
                .map(this::newQuestion)
                .forEach(requestStreamObserver::onNext);

        requestStreamObserver.onCompleted();

        logger.info("Finished asking.");
    }

    public void askComplexQuestion(String question) {
        logger.info("Asking the professor a complex question.");

        Question questionProto = newQuestion(question);

        asyncProfessor.askQuestionWithAnswer(questionProto, new LoggerStreamObserver());

        logger.info("Finished asking.");
    }

    public void askMultipleComplexQuestions(List<String> questions) {
        logger.info("Asking the professor multiple complex questions.");

        StreamObserver<Question> requestStreamObserver = asyncProfessor.askQuestionsWithAnswer(new LoggerStreamObserver());

        questions.stream()
                .map(this::newQuestion)
                .forEach(requestStreamObserver::onNext);

        requestStreamObserver.onCompleted();

        logger.info("Finished asking.");
    }
}
