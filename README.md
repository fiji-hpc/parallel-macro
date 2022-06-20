# Parallel Macro
## Introduction
This is a project that enables [Fiji](https://fiji.sc/) users to parallelize their Macro scripts. Fiji is a distribution of ImageJ.

This project must be used along with HPC Workflow Manager that provides a GUI. Follow [these instructions](https://github.com/fiji-hpc/parallel-macro/wiki/How-to-install-HPC-Workflow-Manager-client) to install it from a Fiji update site. The source code can be found [here](https://github.com/fiji-hpc/hpc-workflow-manager-full) along with **manual** installation instructions for it. 

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

> :warning: Names of Environment Modules might be different on your cluster. They may use lower case names, for example `module load maven`.
> Type `module avail` to list all available modules on your cluster.
> Any Apache Maven version compatible with JDK 8 is acceptable for the installation.

* Make sure that the Java Development Kit (JDK) 8 and Apache Maven are available in your cluster by trying the following:
  *  `javac -version` if a message with a version number in the 1.8 series is displayed, then JDK 8 is installed.
     *  If the version is not in the 1.8 series or the command is not found, then try to load a Java module (in the 1.8 series) on your cluster, for example `module load Java/1.8.0_202`. 
  *  `mvn -version` if a message with any version number is displayed, then Maven is installed.
     * If this command is not found, try loading the default Apache Maven module on your cluster with the `module load Maven` command. If there is no available module, try to install Apache Maven manually. 

* Clone this repository:
  * `git clone https://github.com/fiji-hpc/parallel-macro.git `

* Run the following script which will build and install Parallel Macro in the Fiji located in your remote cluster home directory:
  * `bash build.sh`

> :information_source: If you are not using your home directory for the installation, you have to provide the full path of Fiji to the build script. For example, `bash build.sh /mnt/shared/Fiji.app`.

Parallel Macro should now be ready to be used with HPC Workflow Manager.

