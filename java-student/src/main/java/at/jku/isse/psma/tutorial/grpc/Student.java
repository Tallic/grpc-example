package at.jku.isse.psma.tutorial.grpc;

import com.google.common.base.Joiner;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Student {

    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    private final ProfessorServiceGrpc.ProfessorServiceStub asyncProfessor;

    public Student() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true) // no ssl
                .build();
        asyncProfessor = ProfessorServiceGrpc.newStub(channel);
    }

    private class LoggerStreamObserver implements StreamObserver<Answer> {

        final CountDownLatch finishLath = new CountDownLatch(1);

        @Override
        public void onNext(final Answer value) {
            logger.info("Professor: {}", value.getText());
        }

        @Override
        public void onError(final Throwable t) {
            logger.error("Something disturbed the classroom.", t);
            finishLath.countDown();
        }

        @Override
        public void onCompleted() {
            logger.info("Java Student: Thank you professor.");
            finishLath.countDown();
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
        logger.info("Java Student:{}", question);

        Question questionProto = newQuestion(question);

        LoggerStreamObserver responseObserver = new LoggerStreamObserver();
        asyncProfessor.askQuestionWithAnswer(questionProto, responseObserver);

        try {
            responseObserver.finishLath.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.warn("The professor did not answer within 1 minute.");
        }

        logger.info("Finished asking.");
    }

    public void askMultipleSimpleQuestions(List<String> questions) {
        logger.info("Asking the professor multiple simple questions.");
        logger.info("Java Student:{}", Joiner.on(" AND ").join(questions));

        final LoggerStreamObserver responseObserver = new LoggerStreamObserver();

        StreamObserver<Question> requestStreamObserver = asyncProfessor.askQuestionsWithAnswer(responseObserver);

        questions.stream()
                .map(this::newQuestion)
                .forEach(requestStreamObserver::onNext);

        requestStreamObserver.onCompleted();

        try {
            responseObserver.finishLath.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.warn("The professor did not answer within 1 minute.");
        }


        logger.info("Finished asking.");
    }

    public void askComplexQuestion(String question) {
        logger.info("Asking the professor a complex question.");
        logger.info(Marker."Java Student:{}", question);

        Question questionProto = newQuestion(question);

        final LoggerStreamObserver responseObserver = new LoggerStreamObserver();

        asyncProfessor.askQuestionWithAnswer(questionProto, responseObserver);

        try {
            responseObserver.finishLath.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.warn("The professor did not answer within 1 minute.");
        }

        logger.info("Finished asking.");
    }

    public void askMultipleComplexQuestions(List<String> questions) {
        logger.info("Asking the professor multiple complex questions.");
        logger.info("Java Student:{}", Joiner.on(" AND ").join(questions));

        final LoggerStreamObserver responseObserver = new LoggerStreamObserver();

        StreamObserver<Question> requestStreamObserver = asyncProfessor.askQuestionsWithAnswer(responseObserver);

        questions.stream()
                .map(this::newQuestion)
                .forEach(requestStreamObserver::onNext);

        requestStreamObserver.onCompleted();

        try {
            responseObserver.finishLath.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.warn("The professor did not answer within 1 minute.");
        }

        logger.info("Finished asking.");
    }
}
