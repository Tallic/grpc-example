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

        val answers = knowledge.entries
                .filter { (key, _) -> question.toLowerCase().contains(key) }
                .map { it.value }

        return if (answers.isNotEmpty()) answers else listOf("I cannot help you with that.")
    }

}