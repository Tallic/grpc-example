package at.jku.isse.psma.tutorial.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory


class Classroom(private val port: Int,
                service: ProfessorService) {
    companion object {
        private val logger = LoggerFactory.getLogger(Classroom::class.java)
    }

    private val server: Server = ServerBuilder.forPort(port)
            .addService(service)
            .build()

    fun start() {
        server.start()
        logger.info("Server started, listening on $port")

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            logger.error("shutting down gRpc server since JVM is shutting down")
            stop()
            logger.error("server shut down")
        }))
    }

    fun stop() {
        server.shutdown()
    }

    fun blockUnitShutdown() {
        server.awaitTermination()
    }
}