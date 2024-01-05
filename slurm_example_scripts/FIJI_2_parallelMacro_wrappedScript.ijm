// Start of parallel functions code section:
// This code is automatically appended to the user script file,
// Do NOT remove this section of code!

function parGetRank() {
	rank = call("cz.it4i.fiji.parallel_macro.ParallelMacro.getRank");
	return parseInt(rank);
}

function parGetSize() {
	size = call("cz.it4i.fiji.parallel_macro.ParallelMacro.getSize");
	return parseInt(size);
}

function parBarrier() {
	ret = call("cz.it4i.fiji.parallel_macro.ParallelMacro.barrier");
}

function parReportProgress(task, progress) {
	ret = call("cz.it4i.fiji.parallel_macro.ParallelMacro.reportProgress", task, progress);
}

function parReportText(text) {
	ret = call("cz.it4i.fiji.parallel_macro.ParallelMacro.reportText",text);
}

function parAddTask(description){
	id = call("cz.it4i.fiji.parallel_macro.ParallelMacro.addTask", description);
	return parseInt(id);
}

function parReportTasks(){
	ret = call("cz.it4i.fiji.parallel_macro.ParallelMacro.reportTasks");
}

function parEnableTiming(){
	ret = call("cz.it4i.fiji.parallel_macro.ParallelMacro.enableTiming");
}

function parSelectProgressLogger(type){
	ret = call("cz.it4i.fiji.parallel_macro.ParallelMacro.selectProgressLogger", type);
}

// End of parallel functions section,
// bellow this point is the user's code:

hostname = exec("hostname");
print("FIJI macro on "+hostname+" is # "+parGetRank()+" / "+parGetSize());
