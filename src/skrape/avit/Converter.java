package skrape.avit;

import java.io.File;
import java.util.List;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegBuilder.Strict;

public class Converter 
{	
	private static String[] getFilter(VideoInfo vidInfo, ProfileInfo profInfo)
	{
		if (vidInfo.width == 0)
			vidInfo.width = profInfo.width;
		else if (vidInfo.width > profInfo.width) {
			
			vidInfo.height = (vidInfo.height / vidInfo.width) * profInfo.width;
			vidInfo.width = profInfo.width;
			
		}
		
		if (vidInfo.height == 0)
			vidInfo.height = profInfo.height;
		else if (vidInfo.height > profInfo.height) {
			
			vidInfo.width = (vidInfo.width / vidInfo.height) * profInfo.height;
			vidInfo.height = profInfo.height;
			
		}
		
		int resizeWidth = profInfo.width, resizeHeight = profInfo.height;
		
		if (vidInfo.width > vidInfo.height) {
			resizeHeight = (vidInfo.height / vidInfo.width) * profInfo.width;
			resizeHeight = (resizeHeight < vidInfo.height) ? vidInfo.height : resizeHeight;
		} else {
			resizeWidth = (vidInfo.width / vidInfo.height) * profInfo.height;
			resizeWidth = (resizeWidth < vidInfo.width) ? vidInfo.width : resizeWidth;
		}
		
		int padWidth = (profInfo.width - resizeWidth) / 2, padHeight = (profInfo.height - resizeHeight) / 2;
		
		return new String[] 
			{
				"scale=" + resizeWidth + "x" + resizeHeight,
				"pad=" + profInfo.width + ":" + profInfo.height + ":" + padWidth + ":" + padHeight + ":black"
			};
	}
	
	public static boolean run(File video, VideoInfo vidInfo, int profileIndex)
	{
		final String newFilePath = video.getParent().concat(File.separator).concat(video.getName().substring(0, video.getName().lastIndexOf(".")).concat(".mxf"));
		final ProfileInfo profile = vidInfo.availableProfiles[profileIndex];
		final String[] filters = getFilter(vidInfo, profile);
		
		// Get conversion fps
		double fps = profile.fps;
		if (fps <= 0)
			fps = vidInfo.fps;
		
		// Build job
		FFmpegBuilder builder = new FFmpegBuilder();
		List<String> command = builder.setInput(video.getAbsolutePath())
									  .overrideOutputFiles(true)
									  .addOutput(newFilePath)
									  .addExtraArgs("-map", "0:v")
									  .addExtraArgs("-map", "0:a:?")
									  .setAudioCodec("pcm_s16le")
									  .setVideoFilter("\"" + filters[0] + "," + filters[1] + "\"")
									  .setVideoCodec("dnxhd")
									  .setVideoResolution(profile.width, profile.height)
									  .setVideoBitRate(profile.bitrate)
									  .setVideoFrameRate(fps)
									  .setVideoPixelFormat(profile.pixelFormat)
									  .setAudioSampleRate(48000)
									  .setStrict(Strict.UNOFFICIAL)
									  .done()
									  .build();
		
		System.out.print("\nConverting \"" + video.getName() + "\" with command:\n\tffmpeg ");
		for (String s : command)
			System.out.print(s + " ");
		System.out.println();
		
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
