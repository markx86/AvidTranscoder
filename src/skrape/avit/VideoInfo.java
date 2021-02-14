package skrape.avit;

public class VideoInfo 
{
	public int width, height;
	public long bitrate;
	public double fps;
	public ProfileInfo[] availableProfiles;
	
	public String toString()
	{
		return "\tSize: " + width + "x" + height + "\n" +
			   "\tFPS: " + fps + "\n" +
			   "\tBitrate: " + ((bitrate < 0) ? "N/A" : bitrate) + "\n" +
			   "\tAvailable profiles: " + availableProfiles.length;
	}
}