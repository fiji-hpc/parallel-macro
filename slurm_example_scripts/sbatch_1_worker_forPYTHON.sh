#!/bin/bash

# crank up some additional pythonic env
source /home/xulman/miniconda3/start_conda_env.cmd
conda activate ctc-with-mpi4py_v2

# run the worker (which can consider the OMPI_COMM_WORLD_RANK, etc.)
echo "calling Python for script that uses MPI variables"
python sbatch_2_worker_PYTHON.py

echo "calling Python for script that uses MPI API"
python sbatch_2_worker_PYTHON2.py
