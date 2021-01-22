package com.migrator.appmigrator.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

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

public class XmlParser {

	@SuppressWarnings("resource")
	public static void parseConfigXML(String contextScanPath,String inputFilePath,String outputFilePath) {
	    try {
	    	File inputFile=new File(inputFilePath);
		    String inputFileString=FileUtils.readFileToString(inputFile);
			FileUtils.copyFileToDirectory(inputFile, new File(outputFilePath));
			inputFileString=inputFileString.replace("BASE_PKG", contextScanPath);
			System.out.println("finalConfigString:"+inputFileString);    
			FileOutputStream  out = new FileOutputStream(new File(outputFilePath+"/"+inputFile.getName()));
			out.write(inputFileString.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void parsePomFile(String projectName, File pomFile, String sourceFilePath, String destinationPath) {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = (Document) builder.build(pomFile);
			Element pomNode = doc.getRootElement();
			for (Element element : pomNode.getChildren()) {
				if (element.getName().equalsIgnoreCase("artifactId")) {
					element.setText(projectName);
				}
			}
			FileUtil.createNewFile(destinationPath+"\\pom.xml");
			XMLOutputter xmlOutput = new XMLOutputter();

			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(destinationPath+"\\pom.xml"));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void processWebAndContextConfig(String destinationPath) {
			try {
				boolean found = false;
				boolean appCtxt = false;
				destinationPath = destinationPath+ "/src/" + Constants.WEBXML_PATH;
				System.out.println("In process config : "+destinationPath);
				List<String> files = FileUtil.findFiles(Paths.get(destinationPath), "xml");
				for(String file : files) {
					if(file.contains("web.xml")) {
						File webFile = new File(file);
						SAXBuilder builder = new SAXBuilder();
						Document doc = (Document) builder.build(file);
						Element webAppNode = doc.getRootElement();

						for(Element element : webAppNode.getChildren()) {
							if(element.getName().equals("listener"))
								found = true;
						}
						if(!found){
							System.out.println("null listener");
							Element listenerElement = new Element("listener");
							Element listenerClass = new Element("listener-class");
							listenerClass.setText(Constants.SPRING_LISTENER);
							listenerElement.addContent(listenerClass);
							webAppNode.addContent(listenerElement);
						}
						XMLOutputter xmlOutput = new XMLOutputter();
						xmlOutput.setFormat(Format.getPrettyFormat());
						xmlOutput.output(doc, new FileWriter(destinationPath + "/" + webFile.getName()));
						System.out.println("Donce : "+destinationPath + "/" + webFile.getName());
					} else if(file.contains("applicationContext")) {
						appCtxt = true;
					}
				}
				if(!appCtxt) {
					String filePath= "./src/generatedfiles/applicationContext.xml";
					CommonUtil.copyFile(destinationPath, new File(filePath));
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}

	public static void displayFile(File file) throws Exception {
		Scanner input = new Scanner(file);

		while (input.hasNextLine())
		{
			System.out.println(input.nextLine());
		}
	}
	public static void parseWebXML(File xmlFile,String outputDirPath) {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			FileUtils.copyFileToDirectory(xmlFile, new File(outputDirPath));
			doc = (Document) builder.build(xmlFile);
			Element webAppNode = doc.getRootElement();
			String servletName = null;

			for (Element element : webAppNode.getChildren()) {
				if (element.getName().equals("servlet")) {
					Element servletClassElement = element.getChild("servlet-class", element.getNamespace());
					servletName = element.getChild("servlet-name",element.getNamespace()).getText();
					if (servletClassElement.getText().equalsIgnoreCase("org.apache.struts.action.ActionServlet")) {
						servletClassElement.setText("org.springframework.web.servlet.DispatcherServlet");
					}

					Element initParamElement = element.getChild("init-param",element.getNamespace());
					if (initParamElement.getChild("param-name", element.getNamespace()).getText().equalsIgnoreCase("config")) {
						initParamElement.getChild("param-name",element.getNamespace()).setText("contextConfigLocation");
						initParamElement.getChild("param-value",element.getNamespace()).setText("/WEB-INF/dispatcher-servlet.xml");
					}
				}

				if (element.getName().equalsIgnoreCase("servlet-mapping")) {
					Element servletNameElement = element.getChild("servlet-name", element.getNamespace());
					if (servletNameElement.getText().equalsIgnoreCase(servletName)) {
						element.getChild("url-pattern", element.getNamespace()).setText("/");
					}
				}
				
				if(element.getName().equalsIgnoreCase("welcome-file-list")){
					for(Element child:element.getChildren()){
						String childValue=child.getText();
						if(childValue.endsWith(".jsp")){
							child.setText("/WEB-INF/jsp/"+childValue);
						}
					}
				}
			}

			/*if(webAppNode.getChildren("listener") == null) {
				Element listenerElement = new Element("listener");
				Element listenerClass = new Element(Constants.SPRING_LISTENER);
				listenerElement.addContent(listenerClass);
			}*/

			webAppNode.removeChildren("jsp-config", webAppNode.getNamespace());

			XMLOutputter xmlOutput = new XMLOutputter();

			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(outputDirPath+"/"+xmlFile.getName()));

			System.out.println("File updated!");

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
