# Formats a number to 5 digits:
def formatNumber(number):
    fNumber = ""

    digits = len(str(number))

    for i in range(0, (5 - digits)):
        fNumber += "0"
    fNumber += ""+str(number)

    return fNumber

# Calculate the chunk size given
# a size and the number of files.
def calculateChunkSize(size, files):
    index = 0
    counter = 0
    chunk = [0] * size
    while counter < files:
        if index == size:
            index = 0
        chunk[index] += 1
        index += 1
        counter += 1
    return chunk

def calculateInterval(size, chunk, start, end):
    start[0] = 0
    for i in range(0, size):
        if i > 0:
            start[i] = end[i-1]
        end[i] = start[i] + chunk[i]

import time

# Get the Parallel Macro methods:
from cz.it4i.fiji.parallel_macro import ParallelMacro
from java.lang import Runtime
from ij import IJ

# This works only in UNIX-like systems that support Fiji.
ParallelMacro.initialise()
# Start the timer:
startTime = time.time()

# Define the tasks:
preprocessingTask = ParallelMacro.addTask("Preprocessing")
ParallelMacro.reportTasks()

# Get the rank of the node ad number of nodes available:
myRank = ParallelMacro.getRank()
size = ParallelMacro.getSize()

# Input and output folders on the cluster:
inputFolder = "/scratch/work/project/open-19-3/experiment/input/"
outputFolder = "/scratch/work/project/open-19-3/experiment/output/"

# File prefix and postfix
prefix = "fused_tp_0_ch_"
postfix = ".tif"

# Number of files
files = 1056

print("Rank "+str(myRank)+" started")

# Also save the output in a directory by 
# size (total number of nodes):
outputFolder += ""+str(size)+"/"
run = Runtime.getRuntime()
run.exec("mkdir -p "+outputFolder)
run.exec("chmod -R 777 "+outputFolder)

print("My rank: "+str(myRank)+" and size: "+str(size))

# Split the workload (the number of files) into evenly 
# distributed parts for each compute node if possible.

# Deal the workload iterations evenly:
chunk = calculateChunkSize(size, files)

# Create the iteration start and end for each node:
start = [0] * size
end = [0] * size
calculateInterval(size, chunk, start, end)

print("Rank: "+str(myRank)+" size: "+str(size)+" start: "+str(start[myRank])+" end: "+str(end[myRank])+" part size: "+str(chunk[myRank]))

progress = 0.0
ParallelMacro.reportProgress(preprocessingTask, int(progress))
for i in range(start[myRank], end[myRank]):
    startImageTime = time.time()
    
    # Open the file
    fileName = prefix+formatNumber(i)+postfix
    image = IJ.openImage(inputFolder+"/"+fileName)
    
    # Enhance contrast
    IJ.run(image, "Enhance Contrast...", "saturated=0.3")
    # Gaussian blur
    IJ.run(image, "Gaussian Blur...", "sigma=2")
    # Edge detection
    IJ.run(image, "Find Edges", "")
    
    # Save the processed file:
    IJ.save(image, outputFolder + fileName)
    
    print("Image "+str(i+1)+" time: "+str(time.time() - startImageTime)+" seconds")
    
    progress += 1
    ParallelMacro.reportProgress(preprocessingTask, int(progress/chunk[myRank]*100))
ParallelMacro.reportProgress(preprocessingTask, 100)

print("Rank: "+str(myRank)+" total time before the barrier: "+str(time.time() - startTime)+" seconds.")

ParallelMacro.barrier()

if myRank == 0:
    print("Total execution time: "+str(time.time() - startTime)+" seconds for "+str(size)+" nodes.")
print("Rank "+str(myRank)+" finished.")

ParallelMacro.finalise()