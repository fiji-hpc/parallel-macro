#!/usr/bin/env bash

# Job id:
jobid=155

# Remote working directory:
workingDirectory=$HOME/hpc-workflow-jobs-temp

# Path to Fiji:
pathToFiji=/home/osboxes/fiji/Fiji.app

ml OpenMPI/4.0.0-GCC-6.3.0-2.27

cd $workingDirectory/$jobid/

mpirun $pathToFiji/ImageJ-linux64 --ij2 --headless --console  --run $workingDirectory/$jobid/processBoth.py