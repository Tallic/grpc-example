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
            Pair("pi", "Pi is 3.12 and so forth..."),
            Pair("my mark", "A 1 but don't get ahead of yourself."))
            .withDefault { "I cannot help you with that." }

    private fun findAnswers(question: String): List<String> {
        return knowledge.entries
                .filter { (key, _) -> question.toLowerCase().contains(key) }
                .map { it.value }
    }

    override fun askQuestionWithAnswer(request: Question, responseObserver: StreamObserver<Answer>) {
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

    override fun askQuestionWithAnswers(request: Question, responseObserver: StreamObserver<Answer>) {
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

    override fun askQuestionsWithAnswer(responseObserver: StreamObserver<Answer>): StreamObserver<Question> {
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

                answers.add(
                        findAnswers(question.text).joinToString(" ")
                )
            }

            override fun onCompleted() {
                val answer = Answer.newBuilder()
                        .setId(id)
                        .setText(answers.joinToString("\n"))
                        .build()

                responseObserver.onNext(answer)

                responseObserver.onCompleted()

                logger.info("Answered question $id")
            }
        }
    }

    override fun askQuestionsWithAnswers(responseObserver: StreamObserver<Answer>): StreamObserver<Question> {
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