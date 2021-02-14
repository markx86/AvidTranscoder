package skrape.avit;

public class ProfileInfo 
{
	public int width, height;
	public long bitrate;
	public double fps;
	public String pixelFormat;
	
	public String toString()
	{
		return "\tSize: " + width + "x" + height + "\n" +
			   "\tFPS: " + fps + "\n" +
			   "\tBitrate: " + ((bitrate < 0) ? "N/A" : bitrate) + "\n" +
			   "\tPixel format: " + pixelFormat;
	}
}
