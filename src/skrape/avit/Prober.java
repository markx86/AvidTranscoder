package skrape.avit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class Prober 
{	
	private static int getMult(char mult)
	{
		// Get bitrate multiplier
		switch (mult)
		{
		case 'M':
			return 1000 * 1000;
		case 'k':
			return 1000;
		}
		return 1;
	}
	
//	private static void getProfiles(File video, VideoInfo info, File outFile)
//	{
//		File infoCheck = new File("infocheck.mxf"); // Create temp file
//		List<ProfileInfo> profiles = new ArrayList<ProfileInfo>();
//		
//		// Build command
//		final String command = "ffmpeg -y -i \"" + video.getAbsolutePath() + "\" -vcodec dnxhd \"" + infoCheck.getAbsolutePath() + "\"";
//		
//		try {
//			System.out.println(command);
//			// Run command
//			Process p = Runtime.getRuntime().exec(command);
//			
//			// Read the command output
//			InputStream stream = p.getErrorStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//			String line;
//			while ((line = br.readLine()) != null)
//			{
//				System.out.println(line);
//				// Check if the line contains a profile info
//				if (line.startsWith("[dnxhd @ ") && line.contains("Frame size"))
//				{
//					ProfileInfo pInfo = new ProfileInfo();
//					
//					// VideoSize, Bitrate, PixelFormat, Framerate
//					String[] params = line.replace(" ", "").split("\\;");
//					
//					// Get profile size
//					String[] size = params[0].split("\\:")[1].replace("p", "").replace("i", "").split("x");
//					pInfo.width = Integer.parseInt(size[0]);
//					pInfo.height = Integer.parseInt(size[1]);
//					
//					// Get profile bitrate
//					String bitrate = params[1].split("\\:")[1];
//					char mult = bitrate.charAt(bitrate.indexOf("bps") - 1);
//					pInfo.bitrate = Integer.parseInt(bitrate.substring(0, bitrate.indexOf(mult))) * getMult(mult);
//					
//					// Get profile pixel format
//					pInfo.pixelFormat = params[2].split("\\:")[1].split(",")[0];
//					
//					// Get profile fps
//					pInfo.fps = "N/A";
//					if (params.length > 3)
//						pInfo.fps = params[2].split("\\:")[1];
//					
//					profiles.add(pInfo);
//				}
//			}
//			br.close();
//			p.waitFor();
//			
//		} catch (Exception e) {
//		
//			e.printStackTrace();
//		
//		}
//		
//		// Delete temp file
//		infoCheck.delete();
//		
//		// Add available profiles for video
//		info.availableProfiles = new ProfileInfo[profiles.size()];
//		for (int i = 0; i < profiles.size(); i ++)
//			info.availableProfiles[i] = profiles.get(i);
//	}
	
	private static void getProfiles(File video, VideoInfo info)
	{
		List<ProfileInfo> profiles = new ArrayList<ProfileInfo>();
		File infoCheck = new File("infocheck.mxf"); // Create temp file
		
		// Build ffmpeg job
		FFmpegBuilder builder = new FFmpegBuilder();
		builder.setInput(video.getAbsolutePath())
			   .addOutput(infoCheck.getAbsolutePath())
			   .setVideoCodec("dnxhd")
			   .done();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		
		try {
		
			
			FFmpegExecutor executor = new FFmpegExecutor(AvidTranscoder.ffmpeg);
			executor.createJob(builder).run();
			
		} catch (Exception e) {
			
			if (!(e instanceof RuntimeException))
				e.printStackTrace();
		
		}
		
		System.out.flush();
		System.setOut(old);
		
		try {
			
			BufferedReader br = new BufferedReader(new StringReader(baos.toString()));
			String line;
			while ((line = br.readLine()) != null)
			{
				// Check if the line contains a profile info
				if (line.startsWith("[dnxhd @ ") && line.contains("Frame size"))
				{
					ProfileInfo pInfo = new ProfileInfo();
					
					// VideoSize, Bitrate, PixelFormat, Framerate
					String[] params = line.replace(" ", "").split("\\;");
					
					// Get profile size
					String[] size = params[0].split("\\:")[1].replace("p", "").replace("i", "").split("x");
					pInfo.width = Integer.parseInt(size[0]);
					pInfo.height = Integer.parseInt(size[1]);
					
					// Get profile bitrate
					String bitrate = params[1].split("\\:")[1];
					char mult = bitrate.charAt(bitrate.indexOf("bps") - 1);
					pInfo.bitrate = Long.parseLong(bitrate.substring(0, bitrate.indexOf(mult))) * getMult(mult);
					
					// Get profile pixel format
					pInfo.pixelFormat = params[2].split("\\:")[1].split(",")[0];
					
					// Get profile fps
					pInfo.fps = 0;
					if (params.length > 3) 
					{
						String[] parts = params[2].split("\\:")[1].split("\\/");
						pInfo.fps = Integer.parseInt(parts[0]) / Integer.parseInt(parts[1]);
					}
					
					profiles.add(pInfo);
				}
			}
			br.close();
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}
		
		// Delete temp files
		infoCheck.delete();
		
		// Add available profiles for video
		info.availableProfiles = new ProfileInfo[profiles.size()];
		for (int i = 0; i < profiles.size(); i ++)
			info.availableProfiles[i] = profiles.get(i);
	}
	
//	private static void getVideoInfo(File video, VideoInfo info, File outFile)
//	{
//		final String command = "ffprobe -v error -select_streams v:0 -show_entries stream=r_frame_rate,width,height,bit_rate -of default=noprint_wrappers=1 \"" + video.getAbsolutePath() + "\"";
//		try {
//			System.out.println(command);
//			// Run command
//			Process p = Runtime.getRuntime().exec(command);
//			
//			// Read the command output
//			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String line;
//			while ((line = br.readLine()) != null)
//			{
//				System.out.println(line);
//				// Parse the file
//				line.replace(" ", "");
//				String[] args = line.split("=");
//				switch(args[0])
//				{
//				case "width": // Get video width
//					info.width = Integer.parseInt(args[1]);
//					break;
//				case "height": // Get video height
//					info.height = Integer.parseInt(args[1]);
//					break;
//				case "r_frame_rate": // Get video fps
//					info.fps = args[1];
//					break;
//				case "bit_rate": // Get video bitrate
//					if (!args[1].equalsIgnoreCase("N/A"))
//						info.bitrate = Integer.parseInt(args[1]);
//					else
//						info.bitrate = -1;
//					break;
//				}
//			}
//			br.close();
//			p.waitFor();
//		
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		
//		}
//	}
	
	private static void getVideoInfo(File video, VideoInfo info)
	{
		try {
			
			FFmpegProbeResult res = AvidTranscoder.ffprobe.probe(video.getAbsolutePath());
			FFmpegStream stream = res.getStreams().get(0);
			info.width = stream.width;
			info.height = stream.height;
			info.fps = stream.r_frame_rate.doubleValue();
			info.bitrate = stream.bit_rate;
			
		} catch (IOException e) {
		
			e.printStackTrace();
		
		}
	}
	
	public static VideoInfo run(File video)
	{
		VideoInfo info = new VideoInfo();
		getVideoInfo(video, info);
		getProfiles(video, info);
		return info;
	}
}
