package ling.learning.jdt.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsingEnvironment {
	
	List<String> lstSrcPath   = new ArrayList<String>();
	List<String> lstClassPath = new ArrayList<String>();
	List<String> lstCodePages = new ArrayList<String>();
	List<String> lstFilePath   = new ArrayList<String>();
	
	String filePath = null;
	String fileName = null;

	public List<String> getLstSrcPath() {
		return lstSrcPath;
	}
	public String[] getSrcPathArray() {
		return this.lstSrcPath.toArray(new String[0]);
	}
	public void setLstSrcPath(List<String> lstSrcPath) {
		this.lstSrcPath = lstSrcPath;
	}
	public void addSrcPath(String path) {
		this.lstSrcPath.add(path);
	}
	
	public List<String> getLstClassPath() {
		return lstClassPath;
	}
	public String[] getClassPathArray() {
		return this.lstClassPath.toArray(new String[0]);
	}	
 	public void setLstClassPath(List<String> lstClassPath) {
		this.lstClassPath = lstClassPath;
	}
 	public void addClassPath(String path) {
 		this.lstClassPath.add(path);
 	}
 	
	public List<String> getLstCodePages() {
		return lstCodePages;
	}
	public String[] getCodePageArray() {
		return this.lstCodePages.toArray(new String[0]);
	}
	public void setLstCodePages(List<String> lstCodePages) {
		this.lstCodePages = lstCodePages;
	}
	public void addCodePage(String cp) {
		this.lstCodePages.add(cp);	
	}
	
	public List<String> getLstFilePath() {
		return lstFilePath;
	}
	public String[] getFilePathArray() {
		return this.lstFilePath.toArray(new String[0]);
	}
	public void setLstFilePath(List<String> lstSrcPath) {
		this.lstFilePath = lstSrcPath;
	}
	public void addFilePath(String path) {
		this.lstFilePath.add(path);
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	} 
}
