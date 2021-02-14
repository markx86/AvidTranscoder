package skrape.avit;

public class ProfileInfo 
{
	public int width, height;
	public int bitrate;
	public String fps;
	public String pixelFormat;
	
	// Convert fps fraction to float
	public float getFPS()
	{
		if (fps.equalsIgnoreCase("N/A"))
			return -1.0f;
		String[] args = fps.split("\\/");
		int div = Integer.parseInt(args[1]);
		if (div != 0)
			return Integer.parseInt(args[0]) / div;
		return -1.0f;
	}
	
	public String toString()
	{
		return "\tSize: " + width + "x" + height + "\n" +
			   "\tFPS: " + ((fps.equalsIgnoreCase("N/A")) ? "N/A" : getFPS()) + "\n" +
			   "\tBitrate: " + ((bitrate < 0) ? "N/A" : bitrate) + "\n" +
			   "\tPixel format: " + pixelFormat;
	}
}
