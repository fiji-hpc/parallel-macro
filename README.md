# Parallel Macro
## Introduction
This is a project that enables [Fiji](https://fiji.sc/) users to parallelize their Macro scripts. Fiji is a distribution of ImageJ.

This project must be used along with HPC Workflow Manager that provides a GUI which can be found [here](https://github.com/fiji-hpc/hpc-workflow-manager-full) along with installation instructions for it.

## Build and Install
#### Prerequisites
Before building and installing this package you must:
* Have access to an HPC cluster.
* Make sure that Open MPI is installed (or available as a module) on the cluster.
* Although you can compile Parallel Macro locally, it is best to do so on the remote cluster. In this case make sure that Java 8 and Maven are also installed (or available as a module) on the remote cluster.

#### Installation Steps
* Download the official Linux version of Fiji on the remote cluster using `wget`:
  * `wget https://downloads.imagej.net/fiji/latest/fiji-linux64.zip`

* Extract the downloaded archive of Fiji on a directory in which you have all access rights. Fiji is now installed.:
  * `unzip fiji-linux64.zip`

* Load the Maven and Java modules on your cluster. The names of the modules may be different on your machine.
  * `module load Maven/3.3.9`
  * `module load Java/1.8.0_202`
* Clone this repository:
  * `git clone https://github.com/fiji-hpc/parallel-macro.git `

* Run the following script which will build and install Parallel Macro in the Fiji located in your remote cluster home directory:
  * `bash build.sh`

> :information_source: If you are not using your home directory for the installation, you have to provide the full path of Fiji to the build script. For example, `bash build.sh /mnt/shared/Fiji.app`.

Parallel Macro should now be ready to be used with HPC Workflow Manager.

