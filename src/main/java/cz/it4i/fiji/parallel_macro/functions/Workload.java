
package cz.it4i.fiji.parallel_macro.functions;

public class Workload {

	private int[] heightParts;
	private int[] displacementHeightParts;
	private int[] counts;
	private int[] displacements;

	public int[] getHeightParts() {
		return heightParts;
	}

	public int[] getDisplacementHeightParts() {
		return displacementHeightParts;
	}

	public int[] getCounts() {
		return counts;
	}

	public int[] getDisplacements() {
		return displacements;
	}

	// Returns parts split by image height, displacement by image height, count of
	// elements in the 1D array part and displacements in elements in the 1D array
	// part.
	public Workload(IntBufferImage image, int size) {
		int[] workloadHeightParts = new int[size];
		int[] workloadDisplacementHeightParts = new int[size];
		int[] workloadCounts = new int[size];
		int[] workloadDisplacements = new int[size];

		for (int loopRank = 0; loopRank < size; loopRank++) {
			workloadHeightParts[loopRank] = image.getHeight() / size;
			workloadDisplacementHeightParts[loopRank] = loopRank *
				workloadHeightParts[loopRank];
			if (loopRank == size - 1) {
				// The last node should also do any remaining work.
				workloadHeightParts[loopRank] += image.getHeight() % size;
			}
			workloadCounts[loopRank] = workloadHeightParts[loopRank] * image
				.getWidth();
			workloadDisplacements[loopRank] =
				workloadDisplacementHeightParts[loopRank] * image.getWidth();
		}

		this.heightParts = workloadHeightParts;
		this.displacementHeightParts = workloadDisplacementHeightParts;
		this.counts = workloadCounts;
		this.displacements = workloadDisplacements;
	}

}
