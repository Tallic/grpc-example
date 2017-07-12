import os

os.system('python -m grpc_tools.protoc '
          '-I ../kotlin-professor/src/main/proto/ '
          '--python_out=. '
          '--grpc_python_out=. '
          '../kotlin-professor/src/main/proto/Classroom.proto')
