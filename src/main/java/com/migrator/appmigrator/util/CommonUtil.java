package com.migrator.appmigrator.util;

import japa.parser.ParseException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class CommonUtil {
	
	/**
	 * 
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName  The base package
	 * @return The classes
	 *  
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = (URL) resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList classes = new ArrayList();
		for (File directory : dirs) { classes.addAll(findClasses(directory, packageName));}
		return (Class[]) classes.toArray(new Class[classes.size()]);
	}

	/**
	 * 
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory The base directory
	 * @param packageName The package name for classes found inside the base directory 
	 * @return The classes	  
	 * @throws ClassNotFoundException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
		List classes = new ArrayList();
		if (!directory.exists()) { return classes;	}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName+ '.'+ file.getName().substring(0,file.getName().length() - 6)));
			}
		}
		return classes;
	}
	
	public static File[] getAllClassesInPath(String path) throws ParseException, IOException{
		System.out.println("Input path:"+path);
		File[] dirs = new File(path).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				System.out.println("file name:"+file.getName());
				return file.getName().endsWith(".java");
						
			}
		});
		
		return dirs;
	}

	public static void copyFiles(String destPackageAppPath, File sourceDir) throws IOException {
		File destDir;
		destDir=new File(destPackageAppPath);
		destDir = new File(destDir.getAbsolutePath());
		FileUtils.copyDirectory(sourceDir, destDir);
	}

	public static void copyFile(String destPackageAppPath, File file) throws IOException {
		File destDir;
		destDir=new File(destPackageAppPath);
		destDir = new File(destDir.getAbsolutePath());
		FileUtils.copyFileToDirectory(file, destDir);
	}

	public static int getRandomNum() {
		Random rand = new Random(); //instance of random class
		int upperbound = 25;
		//generate random values from 0-24
		int int_random = rand.nextInt(upperbound);
		return int_random;
	}

	public static void renameFile(String destPackageAppPath, File file) throws Exception {
		File destDir=new File(destPackageAppPath);
		File [] files = destDir.listFiles();
		for(File f : files) {
			if(f.isFile() && f.getName().endsWith("txt")) {
				f.renameTo(file);
				FileUtils.copyFileToDirectory(file, new File(destPackageAppPath));
				f.delete();
				break;
			}
		}
	}
}
