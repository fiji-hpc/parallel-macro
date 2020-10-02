// Formats a number to 5 digits:
function formatNumber(number) {
	fNumber = "";
	
	digits = lengthOf(""+number);

	for (i = 0; i < (5 - digits); i++) {
		fNumber += "0";
	}
	fNumber += ""+number;
	
	return fNumber;
}


// Calculate the chunk size given 
// a size and the number of files.
function calculateChunkSize(size, files) {
	index = 0;
	counter = 0;
	chunk = newArray(size);
	while(counter < files){
		if(index == size){
			index = 0;
		}
		
		chunk[index] += 1;
		index++;
		counter++;
	}
	return chunk;
}

// Calculate the iteration interval start for each node:
function calculateInterval(size, chunk, start, end){
	start[0] = 0;
	for(i = 0; i < size; i++){
		if(i > 0){
			start[i] = end[i-1];
		}
		end[i] = start[i] + chunk[i];
	}
}

// This works only in UNIX-like systems that support Fiji.
parInit();
	// Start the timer:
	startTime = getTime();
	
	// Define the tasks:
	preprocessingTask = parAddTask("Preprocessing");
	parReportTasks();

	// Get the rank of the node and number of nodes available:
	myRank = parseInt(parGetRank());
	size = parseInt(parGetSize());
	
	// Input and output folders on the cluster:
	inputFolder = "/scratch/work/project/open-19-3/experiment/input/";
	outputFolder = "/scratch/work/project/open-19-3/experiment/output/";
	
	// File prefix and postfix
	prefix = "fused_tp_0_ch_";
	postfix = ".tif";
	
	// Number of files
	files = 1056;

	// Batch mode on
	setBatchMode(true);

	print("Rank "+myRank+" started");

	// Also save the output in a directory by 
	// size (total number of nodes):
	outputFolder += ""+size+"/";
	exec("mkdir -p "+outputFolder);
	exec("chmod -R 777 "+outputFolder);

	print("My rank: "+myRank+" and size: "+size);

	// Split the workload (the number of files) into evenly 
	// distributed parts for each compute node if possible.
	
	// Deal the workload iterations evenly:
	chunk = calculateChunkSize(size, files);
	
	// Create the iteration start and end for each node:	
	start = newArray(size);
	end = newArray(size);
	calculateInterval(size, chunk, start, end);

	print("Rank: "+myRank+" size: "+size+" start: "+start[myRank]+" end: "+end[myRank]+" part size: "+chunk[myRank]);

	
	progress = 0;
	parReportProgress(preprocessingTask, progress);
	for (i=start[myRank]; i < end[myRank]; i++) {
		startImageTime = getTime();
		
		// Open the file
		fileName = prefix+formatNumber(i)+postfix;
		open(inputFolder+"/"+fileName);
				
		// Enhance contrast
		run("Enhance Contrast...", "saturated=0.3");
		// Gaussian blur
		run("Gaussian Blur...", "sigma=2");
		// Edge detection
		run("Find Edges");
		
		// Save the processed file:
		saveAs("Tiff", outputFolder + fileName); 
		
		print("Image "+(i+1)+" time: "+(getTime() - startImageTime)/1000+" seconds");

		progress += 1;
		parReportProgress(preprocessingTask,progress/chunk[myRank]*100);
	}
	parReportProgress(preprocessingTask, 100);
	
	print("Rank: "+myRank+" total time before the barrier: "+(getTime() - startTime)/1000+" seconds.");
	
	parBarrier();
	
	if(myRank == 0){
		print("Total execution time: "+(getTime() - startTime)/1000+" seconds for "+size+" nodes.");
	}
	print("Rank "+myRank+" finished.");
	
	// Exit batch mode
	setBatchMode(false);

parFinalize();