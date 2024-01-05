import os

mpi_rank = os.environ["OMPI_COMM_WORLD_RANK"]
mpi_size = os.environ["OMPI_COMM_WORLD_SIZE"]

import subprocess
hostname = subprocess.check_output(['hostname'])

print(f"PYTHON ========= {hostname} is {mpi_rank} / {mpi_size} =========")
