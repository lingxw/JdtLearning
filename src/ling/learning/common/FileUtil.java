package ling.learning.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static  List<File> GetAllFiles(String dirPath) {
		
		List<File> listFiles = new ArrayList<File>();
		
		File curr = new File(dirPath);
		File[] childList = curr.listFiles();
		for(File child : childList) {
			
			if(child.isFile()) {
				listFiles.add(child);
			}else if( child.isDirectory()) {
				List<File> tempList = GetAllFiles(child.getAbsolutePath());
				listFiles.addAll(tempList);
			}
		}	
		
		return listFiles;		
		
	}
	
	public static String readFileToString(String filePath) throws IOException {
		
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}
}
