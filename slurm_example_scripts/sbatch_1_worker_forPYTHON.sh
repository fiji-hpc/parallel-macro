#!/bin/bash

# crank up some additional pythonic env
source /home/xulman/miniconda3/start_conda_env.cmd
conda activate ctc-metrics

# run the worker (which can consider the OMPI_COMM_WORLD_RANK, etc.)
python sbatch_2_worker_PYTHON.py
