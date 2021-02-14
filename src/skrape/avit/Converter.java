package skrape.avit;

import java.io.File;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class Converter 
{	
	public static boolean run(File video, VideoInfo vidInfo, int profileIndex)
	{
		final String newFilePath = video.getParent().concat(File.separator).concat(video.getName().substring(0, video.getName().lastIndexOf(".")).concat(".mxf"));
		ProfileInfo profile = vidInfo.availableProfiles[profileIndex];
		
		// Get conversion fps
		double fps = profile.fps;
		if (fps <= 0)
			fps = vidInfo.fps;
		
		// Build job
		FFmpegBuilder builder = new FFmpegBuilder();
		builder.setInput(video.getAbsolutePath())
			   .overrideOutputFiles(true)
			   .addOutput(newFilePath)
			   .addExtraArgs("-map", "0:v")
			   .addExtraArgs("-map", "0:a")
			   .setAudioCodec("pcm_s16le")
			   .setVideoCodec("dnxhd")
			   .setVideoResolution(profile.width, profile.height)
			   .setVideoBitRate(profile.bitrate)
			   .setVideoFrameRate(fps)
			   .setVideoPixelFormat(profile.pixelFormat)
			   .done();
		
		try {
			
			// Run it
			FFmpegExecutor executor = new FFmpegExecutor(AvidTranscoder.ffmpeg);
			executor.createJob(builder).run();
			
		} catch (Exception e) {
		
			e.printStackTrace();
			return false;
		
		}
		
		return true;
	}
}
