from mpi4py import MPI

mpi_lib = MPI.COMM_WORLD
mpi_rank = mpi_lib.Get_rank()
mpi_size = mpi_lib.Get_size()

import subprocess
hostname = subprocess.check_output(['hostname'])

print(f"PYTHON ========= {hostname} is {mpi_rank} / {mpi_size} =========")
