package at.jku.isse.psma.tutorial.grpc;

import com.google.common.base.Joiner;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("Duplicates")
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
            logger.info("*** [Professor]: {}", value.getText());
        }

        @Override
        public void onError(final Throwable t) {
            logger.error("*** [Storyteller] Something disturbed the classroom.", t);
            finishLath.countDown();
        }

        @Override
        public void onCompleted() {
            logger.info("*** [Java Student]: Thank you professor.");
            finishLath.countDown();
        }
    }

    private Question newQuestion(String question) {
        return Question.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setText(question)
                .build();
    }

    private void completeAsk(final LoggerStreamObserver responseObserver) {
        try {
            responseObserver.finishLath.await(1, TimeUnit.MINUTES);
            logger.info("*** [Storyteller]: Finished asking.");
        } catch (InterruptedException e) {
            MDC.put("speaker", "Storyteller");
            logger.warn("*** [Storyteller]: The professor did not answer within 1 minute.");
        }
    }

    // Point to Point Communication
    public void askSimpleQuestion(String question) {
        logger.info("*** [Java Student]: {}", question);

        Question questionProto = newQuestion(question);

        LoggerStreamObserver responseObserver = new LoggerStreamObserver();
        asyncProfessor.askSimpleQuestion(questionProto, responseObserver);

        completeAsk(responseObserver);
    }

    // Stream to Point Communication
    public void askMultipleSimpleQuestions(List<String> questions) {
        logger.info("*** [Java Student]: {}", Joiner.on(" | ").join(questions));

        final LoggerStreamObserver responseObserver = new LoggerStreamObserver();

        StreamObserver<Question> requestStreamObserver = asyncProfessor.askMultipleSimpleQuestions(responseObserver);

        questions.stream()
                .map(this::newQuestion)
                .forEach(requestStreamObserver::onNext);

        requestStreamObserver.onCompleted();

        completeAsk(responseObserver);
    }

    // Point to Stream Communication
    public void askComplexQuestion(String question) {
        logger.info("*** [Java Student]: {}", question);

        Question questionProto = newQuestion(question);

        final LoggerStreamObserver responseObserver = new LoggerStreamObserver();

        asyncProfessor.askComplexQuestion(questionProto, responseObserver);

        completeAsk(responseObserver);
    }

    // Stream to Stream Communication
    public void askMultipleComplexQuestions(List<String> questions) {
        logger.info("*** [Java Student]: {}", Joiner.on(" AND ").join(questions));

        final LoggerStreamObserver responseObserver = new LoggerStreamObserver();

        StreamObserver<Question> requestStreamObserver = asyncProfessor.askMultipleComplexQuestions(responseObserver);

        questions.stream()
                .map(this::newQuestion)
                .forEach(requestStreamObserver::onNext);

        requestStreamObserver.onCompleted();

        completeAsk(responseObserver);
    }
}
