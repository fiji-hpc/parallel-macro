#!/usr/bin/env bash

# Job id:
jobid=10

# Path to Fiji:
pathToFiji=/scratch/work/project/open-17-47/apps/Fiji.app-macro-mpitest

ml OpenMPI/4.0.0-GCC-6.3.0-2.27

cd $HOME/hpc-workflow-jobs/$jobid/

mpirun $pathToFiji/ImageJ-linux64  --headless --console -macro  $HOME/hpc-workflow-jobs/$jobid/parallelMacroWrappedScript.ijm
/usr/bin/tail -f /dev/null