package com.migrator.appmigrator;

import com.migrator.appmigrator.helper.BeanHelper;
import com.migrator.appmigrator.helper.CodeBuilder;
import com.migrator.appmigrator.helper.MigratorActionHelper;
import com.migrator.appmigrator.parser.JspParser;
import com.migrator.appmigrator.parser.XmlParser;
import com.migrator.appmigrator.util.CommonUtil;
import com.migrator.appmigrator.util.Constants;
import com.migrator.appmigrator.util.FileUtil;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SourceMigrateAction {
	
	public static List<BeanHelper> beansInfo=new ArrayList<BeanHelper>();
	public static Map<String,List<String>> formBeanMapping=new HashMap<String,List<String>>(); 
	public static String contextScanPath="";
	
	
	private String getCurrentPackageName(String fileCanonicalPath){
		return fileCanonicalPath.substring(fileCanonicalPath.indexOf("src/", 0)+4,fileCanonicalPath.length()).replace('/', '.');
	}
	
	
	public static String getNewPackageName(String dirName, String currentPackage,String fileCanonicalPath,String inputProjectPath){
		boolean isActionOnly=false;
		boolean isFormOnly=false;
		try {
			System.out.println("Package name"+currentPackage+":FilePath"+fileCanonicalPath+":InputprojectPath:"+inputProjectPath);
			File[] classes= CommonUtil.getAllClassesInPath(fileCanonicalPath);
			for(File file:classes){
				System.out.println("class name:"+file.getName());
				String fileString=FileUtils.readFileToString(file);
				if(fileString.contains("extends ActionForm")){
					isFormOnly=true;
					BeanHelper beanHelper=new BeanHelper();
					beanHelper.setActionBean(false);
					beanHelper.setFormBean(true);
					beanHelper.setName(file.getName().split("\\.")[0]);
					beanHelper.setOldPackageInfo(currentPackage);
					beansInfo.add(beanHelper);
				}else if(fileString.contains("extends Action")){
					isActionOnly=true;
					BeanHelper beanHelper=new BeanHelper();
					beanHelper.setActionBean(true);
					beanHelper.setFormBean(false);
					System.out.println("file Name:"+file.getName());
					beanHelper.setName(file.getName().split("\\.")[0]);
					beanHelper.setOldPackageInfo(currentPackage);
					beanHelper.setDependentForms(MigratorActionHelper.findAllDependendForms(file,inputProjectPath));
					beansInfo.add(beanHelper);
				} else{
					BeanHelper beanHelper=new BeanHelper();
					beanHelper.setDependentForms(MigratorActionHelper.findAllDependendForms(file,inputProjectPath));
					beansInfo.add(beanHelper);
				}
				
			}
			
			if(isActionOnly && !isFormOnly && currentPackage.length() > 0){
				currentPackage = currentPackage.replace("\\",".");
				return currentPackage.substring(0, currentPackage.lastIndexOf('.'))+"."+"controller";
			}else if(isFormOnly && !isActionOnly){
				currentPackage = currentPackage.replace("\\",".");
				return currentPackage.substring(0, currentPackage.lastIndexOf('.'))+"."+"command";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentPackage;
	}

	private static void visitAndProcessAllFiles(String dirName, File dir, String oldPackagePath, String newPackagePath, String inputProjectPath, String outputAppPath, List<BeanHelper> beansInfo, ArrayList<String> errors,String projectName) throws IOException{
		
		String sourcePackageAppPath=inputProjectPath+"/src/";
		String destPackageAppPath=outputAppPath+"/src/";
		System.out.println("Dir name here : "+dirName+" : "+inputProjectPath);
		System.out.println("Source path : "+sourcePackageAppPath);
		System.out.println("Dest path : "+destPackageAppPath);

		File sourceDir=new File(sourcePackageAppPath);
		File destDir=new File(destPackageAppPath);
		destDir = new File(destDir.getAbsolutePath());
		//1.create package directory
		FileUtil.createDirectory(destPackageAppPath);
		//2.Copy all files from source project to destination project
		if(dirName.equalsIgnoreCase(Constants.RESOURCE_DIR)) {
			File [] files = dir.listFiles();
			for(File file : files) {
				if(file.isFile()) {
					if (file.getName().endsWith("js")) {
						destPackageAppPath = outputAppPath + "/src/" + Constants.JS_RESOURCE_PATH;
					} else if (file.getName().endsWith("css")) {
						destPackageAppPath = outputAppPath + "/src/" + Constants.CSS_RESOURCE_PATH;
					}
					CommonUtil.copyFile(destPackageAppPath, file);
				}
			}
		}

		//FileUtils.copyDirectory(sourceDir, destDir);
		//3.Goto new path
		File [] files = destDir.listFiles();
		for(File file: files){
			if(!file.getName().startsWith(".") && file.isFile() && file.getName().endsWith(".java")){
				new MigratorActionHelper().parseAndProcess(file, beansInfo,newPackagePath,errors);
			}
		}

		File srcfile = new File(inputProjectPath+"/src/");
		destPackageAppPath = outputAppPath + "/src/main/java/com/hack";
		Collection<File> srcFiles = FileUtils.listFiles(srcfile, null, true);
		for(File file2 : srcFiles){
			if(!file2.getName().startsWith(".") && file2.isFile() && file2.getName().endsWith(".java")){
				destPackageAppPath = outputAppPath + "/src/main/java/com/hack";
				CommonUtil.copyFile(destPackageAppPath, file2);
			} else if(file2.getName().endsWith(".jsp")){
				destPackageAppPath = outputAppPath + "/src/" + Constants.JSP_PATH;
				CommonUtil.copyFile(destPackageAppPath, file2);
			} else if(file2.getName().equalsIgnoreCase("applicationContext.xml")||file2.getName().equalsIgnoreCase("web.xml")) {
				destPackageAppPath = outputAppPath + "/src/" + Constants.WEBXML_PATH;
				CommonUtil.copyFile(destPackageAppPath, file2);
			} else if(file2.getName().endsWith("html")) {
				destPackageAppPath = outputAppPath + "/src/" + Constants.STATIC_PATH;
				CommonUtil.copyFile(destPackageAppPath, file2);
			} else if(file2.getName().equalsIgnoreCase("struts.xml")) {
				destPackageAppPath = outputAppPath + "/src/" + Constants.APPROP_RESOURCE_PATH;
				CommonUtil.copyFile(destPackageAppPath, file2);
			}
		}

		XmlParser.processWebAndContextConfig(outputAppPath);

		sourcePackageAppPath= "./src/generatedfiles/";
		sourceDir=new File(sourcePackageAppPath);
		files = sourceDir.listFiles();
		destPackageAppPath = outputAppPath + "/src/main/java/com/hack";
		for(File file : files) {
			if(file.isFile())
			{
				if (file.getName().equalsIgnoreCase("Dockerfile") ||file.getName().contains("travis")
				|| file.getName().contains("ecs")) {
					CommonUtil.copyFile(outputAppPath, file);

					try {
						Path path = Paths.get(outputAppPath+"/"+file.getName());
						Stream<String> lines = Files.lines(path);
						List <String> replaced = lines.map(line -> line.replaceAll("java-webapp", projectName)).collect(Collectors.toList());
						Files.write(path, replaced);
						lines.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		sourcePackageAppPath= "./src/main/resources";
		sourceDir=new File(sourcePackageAppPath);
		files = sourceDir.listFiles();
		for(File file : files) {
			System.out.println("File 2 : " + file.getName());
			if (!file.getName().startsWith(".") && file.isFile()) {
				if(file.getName().endsWith("properties")) {
					destPackageAppPath = outputAppPath + "/src/" + Constants.APPROP_RESOURCE_PATH;
					CommonUtil.copyFile(destPackageAppPath, file);
				}
			}
		}

	}

	public  ArrayList<String> processSourceFiles(File dir,String inputProjectPath,String outputAppPath,String projectName){
		ArrayList<String> errorMessgs=new ArrayList<String>();
			try {
				
				System.out.println("dir Name;"+dir.getName()+" dir path:"+dir.getCanonicalPath());
				boolean doProcess=false;
				for(File file:dir.listFiles()){
					System.out.println("file path:"+file.getName());
					if(!file.getName().startsWith(".") && file.isFile()){
						doProcess=true;
						break;
					}
				}
				if(doProcess){
					String currentPackageName=getCurrentPackageName(dir.getCanonicalPath());
					String newPackageName=getNewPackageName(dir.getName(), currentPackageName,dir.getCanonicalPath(),inputProjectPath);
					visitAndProcessAllFiles(dir.getName(), dir, currentPackageName,newPackageName,inputProjectPath,outputAppPath,beansInfo,errorMessgs,projectName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
		return errorMessgs;
	}
	
	private ArrayList<String> processJspFile(String fileName,String destJspDirPath,ArrayList<String> errors){
		File file=new File(destJspDirPath+"/"+fileName);
		JspParser.parse(file, SourceMigrateAction.formBeanMapping);
		return errors;
	}
	
	private ArrayList<String> processConfigFile(File file,String outputDirPath,ArrayList<String> errors){
		System.out.println("file name:"+file.getName()+",outputDirPath:"+outputDirPath);
		XmlParser.parseConfigXML(SourceMigrateAction.contextScanPath, "./config/spring/dispatcher-servlet.xml", outputDirPath);
		XmlParser.parseWebXML(file,outputDirPath);
		return errors;
	}
	
	public  ArrayList<String> processWebContent(File dir,String inputProjectPath,String outputAppPath){
		ArrayList<String> errorMessgs=new ArrayList<String>();
		try{
			
			String sourcePackageAppPath=inputProjectPath+"/WebContent/";
			String destPackageAppPath=outputAppPath+"/WebContent/";
			
			//2.Copy all files from source project to destination project
			for(File file:dir.listFiles()){
				System.out.println("list of files:"+file.getCanonicalPath());
				if(file.getName().endsWith(".jsp")){
					File jspDir=new File(destPackageAppPath+"/jsp");
					System.out.println("jsp path"+jspDir.getCanonicalPath());
					if(!jspDir.exists()){
						jspDir.mkdir();
					}
					FileUtils.copyFileToDirectory(file, jspDir);
					processJspFile(file.getName(),jspDir.getCanonicalPath(),errorMessgs);
				}else if(file.getName().equals("META-INF")){
					FileUtils.copyDirectory(new File(sourcePackageAppPath+"META-INF"), new File(destPackageAppPath+"META-INF"));
				}else if(file.getName().equals("WEB-INF")){
					for(File subFile:file.listFiles()){
						if(subFile.getName().equals("web.xml")){
						processConfigFile(subFile,destPackageAppPath+"WEB-INF",errorMessgs);	
						}else if(subFile.getName().equals("lib")){
							for(File libFile:subFile.listFiles()){
								if(libFile.getName().endsWith(".jar")){
									String destLibPath=destPackageAppPath+"/WEB-INF/lib";
									File destLibDir=new File(destLibPath);
									if(!StringUtils.containsIgnoreCase(libFile.getName(), "struts") && !new File(destLibPath,libFile.getName()).exists()){
										FileUtils.copyFileToDirectory(libFile,destLibDir);
									}
								}
							}
						}
					}
				}else{
					if(file.isFile() && !file.getName().startsWith(".")){
						FileUtils.copyFileToDirectory(file, new File(destPackageAppPath));
					}else if(file.isDirectory() && !file.getName().equals("lib")){
						FileUtils.copyDirectory(new File(sourcePackageAppPath+file.getName()), new File(destPackageAppPath+file.getName()));
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return errorMessgs;
	}
}
