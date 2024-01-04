#!/bin/bash

#SBATCH -A OPEN-28-13
#SBATCH -o /home/xulman/jobs_logs/job-%x-%j-%N.out.txt
#SBATCH -e /home/xulman/jobs_logs/job-%x-%j-%N.err.txt

#SBATCH -J "CTC_native_python"
#SBATCH -p qcpu

# ${-n} tasks created and spread evenly over ${-N} nodes
#SBATCH -n 5
#SBATCH -N 3
#SBATCH -t 0:2:0

### runs the script in several instances
### (instances needs to sort out somehow on their own how to split the work)
# srun sbatch_worker.sh

### or, load OpenMPI first and use it to
### run the script in several instances
### (instances can read MPI's rank/world to split the work)

# MPI always:
module add OpenMpi/4.1.1-GCC10.3.0-CustomModule

# this or that:
mpirun sbatch_1_worker_BASH.sh
#
mpirun sbatch_1_worker_forPYTHON.sh

# Fiji too!
./sbatch_1_worker_FIJI.sh
