from Student import Student


def test__simple_question__name__success():
    student = Student()
    student.ask_simple_question("What is your name?")


def test__simple_question__name__fail():
    student = Student()
    student.ask_simple_question("What is my name?")


def test__simple_question__name_age__only_name():
    student = Student()
    student.ask_simple_question("What is your name and your age?")


def test__complex_question__name_age__success():
    student = Student()
    student.ask_complex_question("What is your name and your age?")


def test__multiple_simple_question__name_age_mark__success():
    student = Student()
    student.ask_multiple_simple_questions([
        "What is your name? What is MDE?",
        "What is your age?",
        "What's my mark in this course?"
    ])


def test__multiple_complex_questions__name_age_mark_topic__success():
    student = Student()
    student.ask_multiple_complex_questions([
        "What is your name and what is your age?",
        "What's currently my mark in this course and what's the next topic?"
    ])
