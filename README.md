# Parallel Macro
## Introduction
This is a project that enables [Fiji](https://fiji.sc/) users to parallelize their Macro scripts. Fiji is a distribution of ImageJ.

This project must be used along with HPC Workflow Manager that provides a GUI which can be found [here](https://github.com/kozusznik/hpc-workflow-manager-full/tree/paradigmOverSSH) along with installation instructions for it.

## Build 
Use maven with package target to build a jar of this project.

## Install
Before installing this package you must:
* Have access to an HPC cluster.
* Make sure that OpenMPI is installed (or available as a module) with the Java bindings configured on the cluster.

###  Follow these steps to install:
* Upload the package (jar) file of the project that you build to the cluster.
* Download the latest Linux version of Fiji from the [official site](https://fiji.sc/) on your target system (if it is not already installed). You can use the wget program that is included in most UNIX-like systems to download Fiji on the cluster.
* Extract the downloaded archive of Fiji on a directory in which you have all access rights. Fiji is now installed.
* Copy the project's package (jar) file in Fiji's "plugins" or "jars" directory (any one of the two directories will do, there is no need to copy to both directories). The plugin is now installed.

It should now be ready to be used with HPC Workflow Manager.

### Troubleshooting:
Make sure that the directory mpi.jar is in the $LD\_LIBRARY\_PATH of the target system.
Parallel-Macro will find the mpi.jar automatically in the directories listed in the environment variable.
It will also try to "ml" or "module load" the OpenMPI 4.0 module before looking in the environment variable.

You will need to repeat the instructions for every target system.

If you need to manually add the path of mpi.jar in $LD\_LIBRARY\_PATH it is located in OpenMPI's lib directory.

For example, this plugin was tested on the Salomon supercomputer.
The mpi.jar on Salomon is located at /apps/all/OpenMPI/4.0.0-GCC-6.3.0-2.27/lib
