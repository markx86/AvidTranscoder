package skrape.avit;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileChooser
{
	public static File[] show(String title)
	{
		JFileChooser chooser = new JFileChooser();
		
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileFilter() 
		{	
			@Override
			public String getDescription() 
			{
				return "Video files";
			}
			
			@Override
			public boolean accept(File file) 
			{
				if (file.isDirectory())
					return true;
				return Formats.VideoFilesFilter.accept(file.getParentFile(), file.getName());
			}
		});
		
		int ret = chooser.showDialog(null, "Open");
		
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			File files[] = chooser.getSelectedFiles();
			if (files.length == 0)
				return new File[] { chooser.getSelectedFile() };
			else
				return files;
		}
		
		return null;
	}
}
