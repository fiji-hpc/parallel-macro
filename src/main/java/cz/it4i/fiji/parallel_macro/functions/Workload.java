
package cz.it4i.fiji.parallel_macro.functions;

import ij.ImagePlus;

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
	private void calculateWorkload(int width, int height, int size) {
		int[] workloadHeightParts = new int[size];
		int[] workloadDisplacementHeightParts = new int[size];
		int[] workloadCounts = new int[size];
		int[] workloadDisplacements = new int[size];

		for (int loopRank = 0; loopRank < size; loopRank++) {
			workloadHeightParts[loopRank] = height / size;
			workloadDisplacementHeightParts[loopRank] = loopRank *
				workloadHeightParts[loopRank];
			if (loopRank == size - 1) {
				// The last node should also do any remaining work.
				workloadHeightParts[loopRank] += height % size;
			}
			workloadCounts[loopRank] = workloadHeightParts[loopRank] * width;
			workloadDisplacements[loopRank] =
				workloadDisplacementHeightParts[loopRank] * width;
		}

		this.heightParts = workloadHeightParts;
		this.displacementHeightParts = workloadDisplacementHeightParts;
		this.counts = workloadCounts;
		this.displacements = workloadDisplacements;
	}

	public Workload(IntBufferImage image, int size) {
		calculateWorkload(image.getHeight(), image.getWidth(), size);
	}

	public Workload(ImagePlus image, int size) {
		calculateWorkload(image.getWidth(), image.getHeight(), size);
	}

}
