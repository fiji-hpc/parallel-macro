# Parallel-Macro
This is a project that enables Fiji (an ImageJ distribution) users to parallelize their Macro scripts.

This project is to be used along with HPC Workflow Manager that provides a GUI.
It can be found here: https://github.com/fiji-hpc/hpc-workflow-manager 

There are two Fiji installations needed:
* One on the local system which runs the HPC Workflow Manager client.
* Another located at the target system, a computer-cluster.

## Build and Install
Before building this package you must:
* make sure that OpenMPI is installed with the Java bindings configured on your target system;
* copy mpi.jar from the lib directory of the OpenMPI installation of the targeted system;
* paste it in the lib directory of your copy of this project;
Finally, build the package using maven.

Steps for installation:
* Copy the resulting jar file of the project;
* download and install Fiji on the target system (if it is not already installed);
* paste the project's jar file in Fiji's either plugins or jars directory (any one of the two directories).

It should now be ready for use. 
If it does not work make sure that you used the correct mpi.jar on the correct Fiji installation.
You will need to repeat the instructions for every target system.

For example, this plugin was tested with the Salomon supercomputer.
The mpi.jar on Salomon is located at /apps/all/OpenMPI/4.0.0-GCC-6.3.0-2.27/lib