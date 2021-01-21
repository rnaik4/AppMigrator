package com.migrator.appmigrator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.*;

import com.migrator.appmigrator.helper.CodeBuilder;
import com.migrator.appmigrator.util.CommonUtil;
import com.migrator.appmigrator.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

@Controller
public class FrameworkMigrator {
	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel statusLabel;
	private JPanel controlPanel;
	private JTextField newProjectNameField;

	@PostConstruct
	private void prepareGUI() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}

		mainFrame = new JFrame();
		mainFrame.setTitle("Framework Migrator");
		mainFrame.setBounds(300, 90, 600, 400);
		//mainFrame.getContentPane().setLayout(new GridLayout(4, 1));
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		headerLabel = new JLabel("", JLabel.CENTER);
		statusLabel = new JLabel("", JLabel.CENTER);
		statusLabel.setSize(450, 200);

		mainFrame.add(headerLabel);
		mainFrame.getContentPane().setLayout(null);
		displayGUI();
	}

	public void displayGUI() {
		headerLabel.setText("Migrator Inputs");

		JLabel oldProjectPathLabel = new JLabel(
				"Input Project(Struts) Directory Path", JLabel.RIGHT);
		oldProjectPathLabel.setSize(200, 20);
		oldProjectPathLabel.setLocation(48, 100);
		mainFrame.add(oldProjectPathLabel);

		final JTextField oldProjectPathField = new JTextField(10);
		oldProjectPathField.setSize(200, 20);
		oldProjectPathField.setLocation(280, 100);
		mainFrame.add(oldProjectPathField);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(10, 41, 87, 50);
		btnBrowse.setSize(90, 30);
		btnBrowse.setLocation(480, 100);
		mainFrame.add(btnBrowse);

		JLabel newProjectNameLabel = new JLabel("New Project(Spring) Name",
				JLabel.RIGHT);
		newProjectNameLabel.setSize(150, 20);
		newProjectNameLabel.setLocation(60, 140);
		mainFrame.add(newProjectNameLabel);

		JTextField newProjectNameField = new JTextField(10);
		newProjectNameField.setSize(150, 20);
		newProjectNameField.setLocation(280, 140);
		mainFrame.add(newProjectNameField);

		JLabel newProjectPathLabel = new JLabel(
				"Output Project(Spring) Directory Path", JLabel.LEFT);
		newProjectPathLabel.setSize(250, 20);
		newProjectPathLabel.setLocation(60, 180);
		mainFrame.add(newProjectPathLabel);

		JTextField newProjectPathField = new JTextField(10);
		newProjectPathField.setSize(150, 20);
		newProjectPathField.setLocation(280, 180);
		mainFrame.add(newProjectPathField);
		//newProjectNameField.setText("SpringBootApp"+ CommonUtil.getRandomNum());

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				// For Directory
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// For File
				// fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int rVal = fileChooser.showOpenDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					oldProjectPathField.setText(fileChooser.getSelectedFile()
							.toString());
				}
			}
		});

		JButton migrateBtn = new JButton("Migrate");
		migrateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = newProjectPathField.getText();
				String name = newProjectNameField.getText();
				String inputPath = oldProjectPathField.getText();
				if (StringUtils.isNotBlank(path)
						&& StringUtils.isNotBlank("name")
						&& StringUtils.isNotBlank("inputPath")) {
					process(path, name, inputPath);
				}
			}
		});

		migrateBtn.setSize(90, 40);
		migrateBtn.setLocation(60, 220);
		mainFrame.add(migrateBtn);
		mainFrame.setVisible(true);
	}

	public void process(String path, String projectName, String inputAppPath) {

		String outputAppPath = path + projectName;

		try {

			File mainDir = new File(outputAppPath);
			if (!mainDir.exists()) {
				if (mainDir.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			// 1.2 Initial project data structure
			String[] initialDirs = {
					"src",
					"target/classes",
					"src/main/java",
					"src/main/java/com/hack",
					"src/main/resources",
					"src/main/resources/static",
					"src/main/resources/static/js",
					"src/main/resources/static/css",
					"src/main/webapp",
					"src/main/webapp/WEB-INF",
					"src/main/webapp/WEB-INF/jsp"
			};
			// 1.3 Creating basic project folders
			for (String dir : initialDirs) {
				File subDirs = new File(outputAppPath + "/" + dir);
				if (mainDir.exists()) {
					if (subDirs.mkdirs()) {
						System.out.println("Multiple directories are created!");
					} else {
						System.out
								.println("Failed to create multiple directories!");
					}
				}
			}
			// 1.4 copying spring related libraries
			/*String sourcePath = "./config/spring/lib";
			String destinationPath = outputAppPath + "/WebContent/WEB-INF/lib";

			FileUtils.copyDirectory(new File(sourcePath), new File(
					destinationPath));*/

			// Step 2: Starts analyzing input project
			//CodeBuilder.generateSpringBootEntryPoint();

			File[] projectDirs = new File(inputAppPath)
					.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file) {
							return file.isDirectory()
									&& (file.getName().equalsIgnoreCase("src") || file
											.getName().equalsIgnoreCase(
													"webapp"));
						}
					});

			if (projectDirs.length == 0) {
				System.out.println("Empty project input for migrator");
			} else {
				for (File dir : projectDirs) {
					if (dir.getName().equalsIgnoreCase("src")) {
						for (File subDir : dir.listFiles()) {
							if (subDir.isDirectory()) {
								System.out.println("Sub dir name : "+subDir.getName());
								List<File> subDirs = FileUtil
										.getSubdirs(subDir);
								System.out.println("Sub dir length : "+subDirs.size());
								for (File folder : subDirs) {
									if (folder.listFiles().length != 0) {
										System.out.println("dirToProcess:"
												+ folder.getCanonicalPath());
										new SourceMigrateAction()
												.processSourceFiles(folder,
														inputAppPath,
														outputAppPath);

									}
								}
							} else {
								System.out.println("Sub Dir is a File : "+subDir.getName());
							}
						}

					} else if (dir.getName().equalsIgnoreCase("webapp")) {
						System.out.println("dirToProcess:"
								+ dir.getCanonicalPath());
						new SourceMigrateAction().processWebContent(dir,
								inputAppPath, outputAppPath);
					}
				}
				FileUtil.copyUpdatePomFile(projectName, inputAppPath, outputAppPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public static void main(String[] args) throws IOException {
		FrameworkMigrator fm = new FrameworkMigrator();
		fm.displayGUI();
	}*/
}
