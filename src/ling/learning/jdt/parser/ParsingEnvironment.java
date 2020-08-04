package ling.learning.jdt.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsingEnvironment {
	
	String projectPath = null;
	String projectJar = null;
	boolean includeRunningVMBootclasspath = true;
	String javaVersion = null;
	String dumpPath = null;
	
	List<String> lstSrcPath   = new ArrayList<String>();
	List<String> lstClassPath = new ArrayList<String>();
	List<String> lstCodePages = new ArrayList<String>();
	List<String> lstFilePath   = new ArrayList<String>();
	
	String filePath = null;
	String fileName = null;
	
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public String getProjectJar() {
		return projectJar;
	}
	public void setProjectJar(String projectJar) {
		this.projectJar = projectJar;
	}
	public boolean isIncludeRunningVMBootclasspath() {
		return includeRunningVMBootclasspath;
	}
	public void setIncludeRunningVMBootclasspath(boolean includeRunningVMBootclasspath) {
		this.includeRunningVMBootclasspath = includeRunningVMBootclasspath;
	}
	public String getDumpPath() {
		return dumpPath;
	}
	public void setDumpPath(String dumpPath) {
		this.dumpPath = dumpPath;
	}
	public String getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
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
