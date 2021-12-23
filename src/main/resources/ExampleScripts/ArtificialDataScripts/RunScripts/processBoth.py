#@String inputFolder
#@String outputFolder
#@Integer filesNumber
#@Integer nodesPerGroup

#@ OpService ops
#@ SCIFIO scifio
#@ UIService ui
#@ DatasetService datasets

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
        
def group_of_rank(rank, size, group_size):
    threshold = size//group_size
    return rank//threshold

import time

# Get the Parallel Macro methods:
from cz.it4i.fiji.parallel_macro import ParallelMacro
# Get the scijava-parallel-mpi methods:
from java.lang import Runtime
from ij import IJ
from cz.it4i.scijava.mpi import Measure
from net.imglib2.type.numeric.real import DoubleType
from net.imglib2.view import Views

# This works only in UNIX-like systems that support Fiji.

# This is needed in order to use the split method.
from cz.it4i.scijava.mpi import MPIUtils
from net.imglib2.type.numeric.real import FloatType

# Start the timer:
startTime = time.time()

# Define the tasks:
preprocessingTask = ParallelMacro.addTask("Preprocessing")
ParallelMacro.reportTasks()

# Get the rank of the node and number of nodes available:
myRank = ParallelMacro.getRank()
# size (total number of nodes):
size = ParallelMacro.getSize()

# File prefix and postfix
prefix = "img_t_"
postfix = ".tif"

# Number of files
files = filesNumber

print("Rank "+str(myRank)+" started")

# Diagnostic messages:
if myRank == 0:
	print("* Input directory: " + inputFolder)
	print("* Output directory: " + outputFolder)
	print("* Files number: " + str(filesNumber));
	print("* Nodes per group: " + str(nodesPerGroup));

# Make sure the output directory exists:
run = Runtime.getRuntime()
run.exec("mkdir -p "+outputFolder)
run.exec("chmod -R 777 "+outputFolder)

print("My rank: "+str(myRank)+" and size: "+str(size))

# Split the workload (the number of files) into evenly 
# distributed parts for each compute node if possible.

# Set number of groups:
group_size = size//nodesPerGroup

# Deal the workload iterations evenly in groups:
chunk = calculateChunkSize(group_size, files)

# Create the iteration start and end for each node:
start = [0] * group_size
end = [0] * group_size
calculateInterval(group_size, chunk, start, end)
color = group_of_rank(myRank, size, group_size)

MPIUtils.split(color, myRank) # set the group

print("Rank: "+str(myRank)+" size: "+str(size)+" start: "+str(start[color])+" end: "+str(end[color])+" part size: "+str(chunk[color])+" group: "+str(color))

progress = 0.0
ParallelMacro.reportProgress(preprocessingTask, int(progress))
for i in range(start[color], end[color]):
	startImageTime = time.time()
	
	# Open the file
	fileName = prefix+formatNumber(i)+postfix
	input_path = inputFolder+"/"+fileName
	grayscale = scifio.datasetIO().open(input_path)
	grayscale = ops.convert().float32(grayscale)
    
	# Make image brighter:
	bright_image = ops.math().multiply(grayscale, FloatType(20))
	
	# Gaussian blur
	gauss_kernel = ops.create().kernel([
		[1 / 256.0, 4 / 256.0, 6 / 256.0, 4 / 256.0, 1 / 256.0],
		[4 / 256.0, 14 / 256.0, 24 / 256.0, 14 / 256.0, 4 / 256.0],
		[6 / 256.0, 24 / 256.0, 36 / 256.0, 24 / 256.0, 6 / 256.0],
		[4 / 256.0, 14 / 256.0, 24 / 256.0, 14 / 256.0, 4 / 256.0],
		[1 / 256.0, 4 / 256.0, 6 / 256.0, 4 / 256.0, 1 / 256.0]
	], DoubleType())
	
	without_noise = ops.create().img(bright_image)
	ops.filter().convolve(without_noise, Views.extendMirrorSingle(bright_image), gauss_kernel)
	
	# Save the processed file:
	output_path = outputFolder + fileName
	scifio.datasetIO().save(without_noise, output_path)
	
	print("Image "+str(i+1)+" time: "+str(time.time() - startImageTime)+" seconds")
	
	progress += 1
	ParallelMacro.reportProgress(preprocessingTask, int(progress/chunk[color]*100))
ParallelMacro.reportProgress(preprocessingTask, 100)

print("Rank: "+str(myRank)+" total time before the barrier: "+str(time.time() - startTime)+" seconds.")

ParallelMacro.barrier()

if myRank == 0:
	print("Total execution time: "+str(time.time() - startTime)+" seconds for "+str(size)+" nodes.")
print("Rank "+str(myRank)+" finished.")
