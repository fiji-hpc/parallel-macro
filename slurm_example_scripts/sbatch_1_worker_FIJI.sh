#!/bin/bash

# OpenMPI module already loaded from the parent script

PATH_TO_FIJI=/home/xulman/HPCWfM_deployment_stuff/Fiji.app

echo "calling Fiji on parallel (IJ1) macro"
mpirun ${PATH_TO_FIJI}/ImageJ-linux64  --headless --run FIJI_2_parallelMacro_wrappedScript.ijm

echo "calling Fiji on parallel Jython script"
mpirun ${PATH_TO_FIJI}/ImageJ-linux64  --headless --run FIJI_2_parallelJythonScript.py
