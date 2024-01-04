#!/bin/bash

# OpenMPI module already loaded from the parent script

cd /home/xulman/HPCWfM_deployment_stuff/Fiji.app
mpirun ./ImageJ-linux64  --headless --run ../TEST__parallelMacroWrappedScript.ijm
