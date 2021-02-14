package skrape.avit;

import java.io.File;

public class Converter 
{	
	public static boolean run(File video, VideoInfo vidInfo, int profileIndex)
	{
		final String newFilePath = video.getParent().concat(File.separator).concat(video.getName().split("\\.")[0].concat(".mxf"));
		ProfileInfo profile = vidInfo.availableProfiles[profileIndex];
		
		// Get conversion fps
		String fps = profile.fps;
		if (fps.equalsIgnoreCase("N/A"))
			fps = vidInfo.fps;
		
		// Check if the fps is a natural number (like 30/1 fps = 30 fps)
		String[] fpsParts = fps.split("\\/");
		if (Integer.parseInt(fpsParts[1]) == 1)
			fps = fpsParts[0];
		
		// Generate run command
		final String command = "ffmpeg -y -i \"" + video.getAbsolutePath() + "\" -map 0:v -map 0:a -vcodec dnxhd -b:v " + profile.bitrate + " -r " + fps + " -pix_fmt " + profile.pixelFormat + " -acodec pcm_s16le \"" + newFilePath + "\"";
		
		try {
			
			// Run the command
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor(); // Wait for the process to finish
		
		} catch (Exception e) {
		
			e.printStackTrace();
			return false;
		
		}
		
		return true;
	}
}
