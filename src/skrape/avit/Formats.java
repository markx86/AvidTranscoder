package skrape.avit;

import java.io.File;
import java.io.FilenameFilter;

public class Formats 
{
	private static final String[] SUPPORTED_FORMATS = {
			
		"mp4", "m4v", "mkv", "webm"	
			
	};
	
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
