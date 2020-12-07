parInit();
	startTime = getTime();
	
	preprocessingTask = parAddTask("Preprocessing");
	rotateTask = parAddTask("Rotate");
	enhanceTask = parAddTask("Enhance image");
	maximumProjectionTask = parAddTask("Maximum projection");
	parReportTasks();

	// Process Z1 Gap membrane marker and project.
	// Serial version of the script by Bruno.
	// This works only in UNIX-like systems that support Fiji.

	// Username, change this before running:
	username = "dsv";

	// Input folder
	inputFolder = "/home/"+username+"/CLUSTER_TEST/";
	// Output folder
	outputFolder = "/home/"+username+"/CLUSTER_TEST/OUTPUT/";
	// Basename
	basename = "slp-HisGap_1_t35s";
	// First CZI file
	rawData = basename +"/slp-HisGap_1_t35s.czi";
	// Number of views
	views = 9;

	// batch mode on
	setBatchMode(true);

	print("Started");

	// Get the rank of the node and number of nodes available:
	myRank = parGetRank();
	size = parGetSize();

	// Also save the output in a directory by size (total number of nodes):
	outputFolder += ""+size+"/";
	exec("mkdir -p "+outputFolder);
	exec("chmod -R 777 "+outputFolder);

	print("My rank: "+myRank+" and size: "+size);

	// Split the workload (the number of views) into equal parts
	// for each compute node if possible
	numberOfIterationsPerNode = floor(views/size);

	start = (myRank * numberOfIterationsPerNode);

	end = start + numberOfIterationsPerNode;
	if (myRank == (size - 1)) {
		end = views;
	}

	print("Rank: "+myRank+" size: "+size+" start: "+start+" end: "+end+" part size: "+numberOfIterationsPerNode);

	for (i=start; i < end; i++) {
		startViewTime = getTime();
		
		parReportProgress(preprocessingTask,0);
				// Open individual view
				viewPath =  inputFolder + rawData;
				run("Bio-Formats Macro Extensions");
				Ext.setSeries(i);
				Ext.openImagePlus(viewPath);
			parReportProgress(preprocessingTask,33);	
				// Rename
				rename(basename +"_E"+ i);
			parReportProgress(preprocessingTask,66);
				// Get stack title
				dataset = getTitle();
		parReportProgress(preprocessingTask,100);

		parReportProgress(rotateTask,0);
				// Reset levels
				run("Green");
				setMinAndMax(200, 3000);
				run("Next Slice [>]");
				run("Magenta");
				setMinAndMax(200, 1500);
				Stack.setDisplayMode("composite");
			parReportProgress(rotateTask,33);
				// Orient stack properly
				run("Rotate 90 Degrees Left");
				run("Flip Horizontally", "stack");
			parReportProgress(rotateTask,66);
				// Save rotated data
				saveAs("Tiff", outputFolder + dataset +".tif");
		parReportProgress(rotateTask,100);

		parReportProgress(enhanceTask,0);
				// Remove some noise
				run("Despeckle", "stack");
			parReportProgress(enhanceTask,25);
				// Subtract background to improve MAX projection
				run("Subtract Background...", "rolling=20 stack");
			parReportProgress(enhanceTask,50);
				// Reset new levels
				setMinAndMax(5, 800);
				run("Previous Slice [<]");
				setMinAndMax(5, 2000);
			parReportProgress(enhanceTask,75);
				// Save processed data as new stack
				saveAs("Tiff", outputFolder + dataset +"_sub20.tif");
		parReportProgress(enhanceTask,100);

		parReportProgress(maximumProjectionTask,0);
				// Maximum projection
				run("Z Project...", "projection=[Max Intensity] all");
			parReportProgress(maximumProjectionTask,20);
				// Get title of maximum projection
				max = getTitle();
			parReportProgress(maximumProjectionTask,40);
				// Save maximum projection
				saveAs("Tiff", outputFolder + max);
			parReportProgress(maximumProjectionTask,60);
				// Create and save as AVI movie
				run("RGB Color", "frames");
				run("AVI... ", "compression=None frame=15 save="+ outputFolder + max +".avi");
			parReportProgress(maximumProjectionTask,80);
				// MAX
				close();
				// Sub
				close();
		parReportProgress(maximumProjectionTask,100);
		
		print("View "+(i+1)+" time: "+(getTime() - startViewTime)/1000+" seconds");
	}

	parBarrier();
	print("Total execution time: "+(getTime() - startTime)/1000+" seconds for "+size+" nodes.");
	print("Finished");
	// exit batch mode
	setBatchMode(false);

parFinalize();