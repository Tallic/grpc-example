package at.jku.isse.psma.tutorial.grpc

import org.testng.annotations.Test


class ProfessorServiceTest {

    @Test(groups = arrayOf("run"))
    fun runClass() {
        val professor = ProfessorService("Alexander Egyed")
        val classroom = Classroom(50051, professor)
        classroom.start()
        classroom.blockUnitShutdown()
    }
}