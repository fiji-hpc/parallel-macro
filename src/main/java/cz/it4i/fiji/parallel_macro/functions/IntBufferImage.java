
package cz.it4i.fiji.parallel_macro.functions;

import java.nio.IntBuffer;

public class IntBufferImage {

	private int width = 0;
	private int height = 0;
	private int type;
	private IntBuffer buffer = null;

	public IntBufferImage(int imageWidth, int imageHeight, int imageType,
		IntBuffer imagePixels)
	{
		this.width = imageWidth;
		this.height = imageHeight;
		this.type = imageType;
		this.buffer = imagePixels;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return height;
	}

	public IntBuffer getIntBuffer() {
		return buffer;
	}

	public int getType() {
		return type;
	}

	public void setIntBuffer(IntBuffer newImageBuffer) {
		buffer = newImageBuffer;
	}
}
