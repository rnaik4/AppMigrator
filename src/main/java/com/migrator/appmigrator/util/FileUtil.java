package com.migrator.appmigrator.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.migrator.appmigrator.parser.XmlParser;
import org.apache.commons.io.FileUtils;

public class FileUtil {

	public static List<File> getSubdirs(File file) {
		List<File> subdirs = Arrays.asList(file.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}
		}));
		subdirs = new ArrayList<File>(subdirs);
		List<File> deepSubdirs = new ArrayList<File>();
		for (File subdir : subdirs) {
			deepSubdirs.addAll(getSubdirs(subdir));
		}
		subdirs.addAll(deepSubdirs);
		return subdirs;
	}
	
	public static void createDirectory(String path){
		 try {
			FileUtils.forceMkdir(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean createNewFile(String path){
		try {
			FileOutputStream fout = new FileOutputStream(path);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void copyUpdatePomFile(String name, String inputAppPath, String outputAppPath) {
		try {
			String sourceFilePath = getCurrentWorkingDirectory()+"\\pom1.xml";
			String destinationPath = outputAppPath;
			XmlParser.parsePomFile(name, new File(sourceFilePath), sourceFilePath, destinationPath);

		}catch(Exception ex) {
			System.out.println("Error copying pom file : "+ex.getMessage());
		}
	}

	public static List<String> findFiles(Path path, String fileExtension)
			throws IOException {

		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}

		List<String> result;

		try (Stream<Path> walk = Files.walk(path)) {
			result = walk
					.filter(p -> !Files.isDirectory(p))
					.map(p -> p.toString().toLowerCase())
					.filter(f -> f.endsWith(fileExtension))
					.collect(Collectors.toList());
		}
		return result;
	}

	private static String getCurrentWorkingDirectory() {
		return System.getProperty("user.dir");
	}
}
