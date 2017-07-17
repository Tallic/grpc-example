package at.jku.isse.psma.tutorial.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory


class Classroom(private val port: Int,
                service: ProfessorService) {
    companion object {
        private val logger = LoggerFactory.getLogger(Classroom::class.java)
    }

}