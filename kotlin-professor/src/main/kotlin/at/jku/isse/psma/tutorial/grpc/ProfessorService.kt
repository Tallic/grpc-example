package at.jku.isse.psma.tutorial.grpc

import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory

class ProfessorService(val name: String) : ProfessorServiceGrpc.ProfessorServiceImplBase() {

    companion object {
        private val logger = LoggerFactory.getLogger(ProfessorService::class.java)
    }

    private val knowledge: Map<String, String> = mapOf(
            Pair("your name", "My name is $name."),
            Pair("age", "I am 45 years old."),
            Pair("mde", "MDE means model driven engineering."),
            Pair("my mark", "An A but don't get ahead of yourself."),
            Pair("next topic", "We will learn about gRpc."))
            .withDefault { "I cannot help you with that." }

    private fun findAnswers(question: String): List<String> {

        val answer = knowledge.entries
                .filter { (key, _) -> question.toLowerCase().contains(key) }
                .map { it.value }

        return if (answer.isNotEmpty()) answer else listOf("I cannot help you with that.")
    }

    override fun askSimpleQuestion(request: Question, responseObserver: StreamObserver<Answer>) {
        logger.info("Receiving new question from ${request.name} (${request.id})")

        val answerText = findAnswers(request.text).first()

        val answer = Answer.newBuilder()
                .setId(request.id)
                .setText(answerText)
                .build()

        responseObserver.onNext(answer)

        responseObserver.onCompleted()

        logger.info("Answered question ${request.id}")
    }

    override fun askMultipleSimpleQuestions(responseObserver: StreamObserver<Answer>): StreamObserver<Question> {
        logger.info("Receiving multiple questions ... i will provide only one answer.")

        return object : StreamObserver<Question> {
            private var id = ""
            private var answers = mutableListOf<String>()

            override fun onError(t: Throwable) {
                val status = Status.fromThrowable(t)
                logger.error("Something disturbing happened in the classroom. ($status)", t)
            }

            override fun onNext(question: Question) {
                if (id.isEmpty()) id = question.id

                answers.add(findAnswers(question.text).first())
            }

            override fun onCompleted() {
                val answer = Answer.newBuilder()
                        .setId(id)
                        .setText(answers.joinToString(" "))
                        .build()

                responseObserver.onNext(answer)

                responseObserver.onCompleted()

                logger.info("Answered question $id")
            }
        }
    }

    override fun askComplexQuestion(request: Question, responseObserver: StreamObserver<Answer>) {
        logger.info("Receiving new question from ${request.name} (${request.id})")

        findAnswers(request.text)
                .forEach {
                    val answer = Answer.newBuilder()
                            .setId(request.id)
                            .setText(it)
                            .build()
                    responseObserver.onNext(answer)
                }

        responseObserver.onCompleted()

        logger.info("Answered question ${request.id}")
    }

    override fun askMultipleComplexQuestions(responseObserver: StreamObserver<Answer>): StreamObserver<Question> {
        logger.info("Receiving multiple questions ... i will provide multiple answers.")

        return object : StreamObserver<Question> {
            override fun onError(t: Throwable) {
                val status = Status.fromThrowable(t)
                logger.error("Something disturbing happened in the classroom. ($status)", t)
            }

            override fun onNext(question: Question) {

                val answer = Answer.newBuilder()
                        .setId(question.id)
                        .setText(findAnswers(question.text).joinToString(" "))
                        .build()

                responseObserver.onNext(answer)
            }

            override fun onCompleted() {
                responseObserver.onCompleted()

                logger.info("Answered all questions")
            }
        }
    }
}