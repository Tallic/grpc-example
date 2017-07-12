import grpc
import uuid
import logging
import Classroom_pb2_grpc
from typing import List

from Classroom_pb2 import Question


class Student:
    def __init__(self) -> None:
        super().__init__()
        channel = grpc.insecure_channel('localhost:50051')
        self.asyncProfessor = Classroom_pb2_grpc.ProfessorServiceStub(channel)

    def ask_simple_question(self, question: str):
        logging.info("Asking the professor a simple question.")

        question_proto = Question(id=str(uuid.uuid1()), text=question)

        answer_future = self.asyncProfessor.askQuestionWithAnswer(question_proto)
        answer = answer_future.result()
        logging.info("Professor: {}".format(answer.text))

        logging.info("Python Student: Thank you professor.")

    def ask_multiple_simple_questions(self, questions: List[str]):
        logging.info("Asking the professor multiple simple questions.")

        question_protos = [Question(id=str(uuid.uuid1()), text=question)
                           for question in questions]

        answers = self.asyncProfessor.askQuestionsWithAnswer(question_protos)

        for answer in answers:
            logging.info("Professor: {}".format(answer.text))

        logging.info("Python Student: Thank you professor.")

    def ask_complex_question(self, question: str):
        logging.info("Asking the professor a complex question.")

        question_proto = Question(id=str(uuid.uuid1()), text=question)

        answers = self.asyncProfessor.askQuestionsWithAnswer(question_proto)

        for answer in answers:
            logging.info("Professor: {}".format(answer.text))

        logging.info("Python Student: Thank you professor.")

    def ask_multiple_complex_questions(self, questions: List[str]):
        logging.info("Asking the professor multiple complex questions.")

        question_protos = [Question(id=str(uuid.uuid1()), text=question)
                           for question in questions]

        answers = self.asyncProfessor.askQuestionsWithAnswer(question_protos)

        for answer in answers:
            logging.info("Professor: {}".format(answer.text))

        logging.info("Python Student: Thank you professor.")
