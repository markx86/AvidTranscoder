package skrape.avit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

public class AvidTranscoder 
{
	public static FFmpeg ffmpeg;
	public static FFprobe ffprobe;
	
	public static enum OS {
		Linux,
		Windows,
		MacOS
	};
	public static OS hostOS;
	
	private static boolean manualSelect;
	
	private static List<File> findFiles(File dir)
	{
		List<File> filesFound = new ArrayList<File>();
		File[] files = dir.listFiles(Formats.VideoFilesFilter);
		
		for (File file : files)
		{
			if (file.isDirectory())
				filesFound.addAll(findFiles(file));
			else
				filesFound.add(file);
		}
		
		return filesFound;
	}
	
	private static int chooseProfile(VideoInfo vidInfo)
	{
		double[] minDistance = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
		int profileIndex = 0;
		
		for (int i = 0; i < vidInfo.availableProfiles.length; i ++)
		{
			ProfileInfo info = vidInfo.availableProfiles[i];
			double fpsDist = (info.fps < 0) ? 0.0d : Math.abs(info.fps - vidInfo.fps);
			double bitrateDist = (vidInfo.bitrate < 0) ? 0.0f : Math.abs(info.bitrate - vidInfo.bitrate);
			double resDist = Math.abs(info.width - vidInfo.width) + Math.abs(info.height - vidInfo.height);
			
			if (resDist <= minDistance[0] && bitrateDist <= minDistance[1] && fpsDist <= minDistance[2] && info.bitrate < vidInfo.availableProfiles[profileIndex].bitrate)
			{
				if (vidInfo.bitrate > info.bitrate && vidInfo.availableProfiles[profileIndex].bitrate > info.bitrate)
					continue;
				minDistance[0] = resDist;
				minDistance[1] = bitrateDist;
				minDistance[2] = fpsDist;
				profileIndex = i;
			}
		}
		
		return profileIndex;
	}
	
	private static void findFFmpeg()
	{
		String[] path = System.getenv("PATH").split("\\".concat(File.pathSeparator));
		String ext = (hostOS == OS.Windows) ? ".exe" : "";
		
		// Horrible code that should work regardless of the OS (please don't hate me, I'm tired it's 3 A.M.)
		for (String entry : path)
		{
			String tmpPath = ((entry.endsWith(File.separator)) ? entry.concat("ffmpeg") : entry.concat(File.separator + "ffmpeg")) + ext;
			if ((new File(tmpPath)).exists())
			{
				System.out.println("FFmpeg installation found at: " + entry);
				
				try {
				
					ffmpeg = new FFmpeg(entry.concat(File.separator + "ffmpeg" + ext));
					ffprobe = new FFprobe(entry.concat(File.separator + "ffprobe" + ext));
					
				} catch (IOException e) {
				
					e.printStackTrace();
					System.exit(-3);
					
				}
				
				return;
			}
		}
		
		System.out.println("Could not find FFmpeg");
		System.exit(-2);
	}
	
	private static void findOS()
	{
		// Kinda redundant
		String osname = System.getProperty("os.name");
		if (osname.contains("Mac"))
			hostOS = OS.MacOS;
		else if (osname.contains("Windows"))
			hostOS = OS.Windows;
		else
			hostOS = OS.Linux;
		
		// Check if the OS was detected correctly
		String name = "Undetected";
		switch (hostOS)
		{
		case Linux:
			name = "Linux";
			break;
		case MacOS:
			name = "MacOS";
			break;
		case Windows:
			name = "Windows";
			break;
		}
		System.out.println("Detected OS: " + name);
	}
	
	public static void main(String[] args)
	{
		List<File> files = new ArrayList<File>();
		List<File> inputs = new ArrayList<File>();
		
		findOS();
		findFFmpeg();
		
		// Was the program launched from the command line?
		if (args.length == 0) {
			
			// If not ask the user to select some files
			System.out.println("Please select the files to transcode");
			File[] selected = FileChooser.show("Choose a file or folder");
			
			if (selected == null || selected.length == 0)
			{
				System.out.println("No file(s) selected! Quitting...");
				System.exit(-1);
			}
			
			for (File file : selected)
				inputs.add(file);
		
		} else {
		
			// If yes treat the argument as files
			for (String arg : args)
				inputs.add(new File(arg));
		
		}
		
		// Filter video files
		for (File file : inputs)
		{
			if (!file.exists()) // If the file doesn't exist why bother
				continue;
			if (file.isDirectory()) // If it's a directory search it recursively
				files.addAll(findFiles(file));
			else if (Formats.VideoFilesFilter.accept(null, file.getName())) // Otherwise check if the file's a video file and in that case add it to the list
				files.add(file);
		}
		
		if (files.size() == 0)
		{
			System.out.println("No video files found in input files");
			System.exit(3);
		}
		
		System.out.println("Added " + files.size() + " file" + ((files.size() > 1) ? "s" : ""));

		// Ask for automatic selection
		Scanner sc = new Scanner(System.in);
		while (true)
		{
			System.out.print("Do you want to automatically select a profile for each video? [Y/n] > ");
			String res = sc.nextLine();
			
			if (res.equalsIgnoreCase("y"))
				manualSelect = false;
			else if (res.equalsIgnoreCase("n"))
				manualSelect = true;
			else {
				
				System.out.println("Invalid option.");
				continue;
			
			}
			
			break;
		}
		
		for (File video : files)
		{
			// Get video info for video
			VideoInfo vidInfo = Prober.run(video);
			int index = 0;
			
			// Get profile index
			if (manualSelect) {
				
				while (true)
				{
					System.out.println("Select profile for video \"" + video.getName() + "\"");
					for (int i = 0; i < vidInfo.availableProfiles.length; i ++)
					{
						System.out.println("[" + i + "] " + vidInfo.availableProfiles[i].toString().replace("\n", " | ").replace("\t", ""));
					}
					System.out.print("Insert profile index [0" + " - " + (vidInfo.availableProfiles.length - 1) + "] > ");
					index = sc.nextInt();
					if (index >= vidInfo.availableProfiles.length || index < 0)
					{
						System.out.println("Invalid profile\n");
						continue;
					}
					break;
				}
				
			} else
				index = chooseProfile(vidInfo);
			
			System.out.println("\nProcessing video \"" + video.getName() + "\"\n -> VideoInfo:\n" + vidInfo.toString() + "\n -> ProfileInfo:\n" + vidInfo.availableProfiles[index].toString());
			
			// Convert the video
			if (!Converter.run(video, vidInfo, index))
				System.out.println("Failed to convert \"" + video.getAbsolutePath() + "\"");
			else
				System.out.println("\nSuccessfully converted \"" + video.getName() + "\"");
		}
		
		sc.close();
		System.exit(0);
	}
}
