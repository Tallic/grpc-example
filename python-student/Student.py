import grpc
import uuid
import logging
import Classroom_pb2_grpc
from typing import List

from Classroom_pb2 import Question

logger = logging.getLogger('Student')
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setFormatter(logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s'))
logger.addHandler(ch)


class Student:
    def __init__(self) -> None:
        super().__init__()
        channel = grpc.insecure_channel('localhost:50051')  # no ssl
        self.asyncProfessor = Classroom_pb2_grpc.ProfessorServiceStub(channel)

    def ask_simple_question(self, question: str):
        logger.info("[Python Student]: {}".format(question))

        question_proto = Question(id=str(uuid.uuid1()), text=question)

        answer = self.asyncProfessor.askSimpleQuestion(question_proto)

        logger.info("[Professor]: {}".format(answer.text))

        logger.info("[Python Student]: Thank you professor.")

    def ask_multiple_simple_questions(self, questions: List[str]):
        logger.info("[Python Student]: {}".format(' | '.join(questions)))

        question_protos = (Question(id=str(uuid.uuid1()), text=question) for question in questions)

        answer = self.asyncProfessor.askMultipleSimpleQuestions(question_protos)

        logger.info("[Professor]: {}".format(answer.text))

        logger.info("[Python Student]: Thank you professor.")

    def ask_complex_question(self, question: str):
        logger.info("[Python Student]: {}".format(question))

        question_proto = Question(id=str(uuid.uuid1()), text=question)

        answers = self.asyncProfessor.askComplexQuestion(question_proto)

        for answer in answers:
            logger.info("[Professor]: {}".format(answer.text))

        logger.info("[Python Student]: Thank you professor.")

    def ask_multiple_complex_questions(self, questions: List[str]):
        logger.info("[Python Student]: {}".format(' | '.join(questions)))

        question_protos = (Question(id=str(uuid.uuid1()), text=question) for question in questions)

        answers = self.asyncProfessor.askMultipleComplexQuestions(question_protos)

        for answer in answers:
            logger.info("[Professor]: {}".format(answer.text))

        logger.info("[Python Student]: Thank you professor.")
