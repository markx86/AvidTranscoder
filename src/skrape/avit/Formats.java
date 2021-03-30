package skrape.avit;

import java.io.File;
import java.io.FilenameFilter;

public class Formats 
{
	private static final String[] SUPPORTED_FORMATS = {
			
		"mp4", "m4v", "mkv", "webm", "mov"
			
	};
	
	public static String regex = "";
	
	public static void buildRegex()
	{
		for (String format : SUPPORTED_FORMATS)
			regex += "\\." + format + "|";
		regex = regex.substring(0, regex.length() - 1);
		System.out.println("[DBG] Formats regex: " + regex);
	}
	
	public static FilenameFilter VideoFilesFilter = new FilenameFilter() 
	{	
		@Override
		public boolean accept(File file, String name) 
		{
			for (String ext : SUPPORTED_FORMATS)
			{
				if (name.endsWith(".".concat(ext)))
					return true;
			}
			return false;
		}
	};
}
