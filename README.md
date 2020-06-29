# Parallel-Macro
This is a project that enables Fiji (an ImageJ distribution) users to parallelize their Macro scripts.

This project is to be used along with HPC Workflow Manager that provides a GUI.
It can be found here: https://github.com/fiji-hpc/hpc-workflow-manager 

There are two Fiji installations needed:
* One on the local system which runs the HPC Workflow Manager client.
* Another located at the target system, a computer-cluster.

## Build 
Use maven with package target to build a jar of this project.

## Install
Before building this package you must:
* Make sure that OpenMPI is installed (or available as a module) with the Java bindings configured on your target system.

Steps for installation:
* Copy the jar file of the project that you build;
* download and install Fiji on the target system (if it is not already installed);
* paste the project's jar file in Fiji's either plugins or jars directory (any one of the two directories).

It should now be ready for use. 
### If it does not work:
Make sure that the directory mpi.jar is in the $LD\_LIBRARY\_PATH of the target system.
Parallel-Macro will find the mpi.jar automatically in the directories listed in the environment variable.
It will also try to "ml" or "module load" the OpenMPI 4.0 module before looking in the environment variable.

You will need to repeat the instructions for every target system.

If you need to manually add the path of mpi.jar in $LD\_LIBRARY\_PATH it is located in OpenMPI's lib directory.

For example, this plugin was tested on the Salomon supercomputer.
The mpi.jar on Salomon is located at /apps/all/OpenMPI/4.0.0-GCC-6.3.0-2.27/lib