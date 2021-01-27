package com.migrator.appmigrator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import javax.swing.*;
import com.migrator.appmigrator.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class FrameworkMigrator extends JFrame {

	private GridBagConstraints gridBagConstraints;
	private JLabel outputPathLabel;
	private JTextField outputPathField;
	private JLabel comboLabel;
	private JComboBox valueList;

	public FrameworkMigrator() {
		initComponents();
		setLookAndFeel();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		setTitle("Framework Migrator");

		comboLabel = new JLabel("Select number of projects to migrate:");
		valueList = new JComboBox();

		outputPathLabel = new JLabel("Output project path:");
		outputPathField = new JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		getContentPane().add(comboLabel, gridBagConstraints);

		String [] options = new String[] { "Choose...", "1" ,"2", "3", "4", "5" };
		valueList.setModel(new javax.swing.DefaultComboBoxModel(options));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		getContentPane().add(valueList, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		getContentPane().add(outputPathLabel, gridBagConstraints);

		outputPathField.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		getContentPane().add(outputPathField, gridBagConstraints);

		ClassLoader cldr = FrameworkMigrator.class.getClassLoader();
		URL url= cldr.getResource("images/folder.png");
		JButton outputButton = new JButton();
		if(null != url)
		outputButton.setIcon(new ImageIcon(url));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		getContentPane().add(outputButton, gridBagConstraints);
		outputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int rVal = fileChooser.showOpenDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					outputPathField.setText(fileChooser.getSelectedFile()
							.toString());
				}
			}
		});

		JButton entriesButton = new JButton("Create Entries");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		getContentPane().add(entriesButton, gridBagConstraints);

		entriesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = (String)valueList.getModel().getSelectedItem();
				List<JTextField> inputFields =new ArrayList<JTextField>();
				map.put("fields", inputFields);

				for(int i=0;i < Integer.parseInt(value); i++) {
					JLabel inputLabel = new JLabel("Input project path:");
					JTextField field = new JTextField();
					field.putClientProperty("id", "id"+i);
					inputFields.add(field);

					JButton inputButton = new JButton();
					ClassLoader cldr = FrameworkMigrator.class.getClassLoader();
					URL url= cldr.getResource("images/folder.png");
					inputButton.setIcon(new ImageIcon(url));

					gridBagConstraints = new java.awt.GridBagConstraints();
					gridBagConstraints.gridx = 0;
					gridBagConstraints.gridy = (i+4);
					gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
					gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
					getContentPane().add(inputLabel, gridBagConstraints);

					field.setColumns(20);
					gridBagConstraints = new java.awt.GridBagConstraints();
					gridBagConstraints.gridx = 1;
					gridBagConstraints.gridy = (i+4);
					gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
					getContentPane().add(field, gridBagConstraints);

					gridBagConstraints = new java.awt.GridBagConstraints();
					gridBagConstraints.gridx = 2;
					gridBagConstraints.gridy = (i+4);
					gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
					gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
					getContentPane().add(inputButton, gridBagConstraints);
					inputButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							fileChooser.setAcceptAllFileFilterUsed(false);
							int rVal = fileChooser.showOpenDialog(null);
							if (rVal == JFileChooser.APPROVE_OPTION) {
								field.setText(fileChooser.getSelectedFile()
										.toString());
							}
						}
					});
					pack();
				}
			}
		});

		JButton migrateButton = new JButton("Migrate");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
		gridBagConstraints.insets = new java.awt.Insets(5, -80, 0, 0);
		getContentPane().add(migrateButton, gridBagConstraints);
		migrateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<JTextField> fields = map.get("fields");
				for(JTextField field : fields) {
					String inputPath = field.getText();
					String newPath = outputPathField.getText();

					if (StringUtils.isNotBlank(inputPath)
							&& StringUtils.isNotBlank(newPath)) {
						process(newPath, inputPath);
					}
				}
			}
		});
		pack();
	}

	private void setLookAndFeel() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			repaint();
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
	}

	public void process(String path, String inputAppPath) {
		String projectName = inputAppPath.substring(inputAppPath.lastIndexOf("\\")+1, inputAppPath.length());
		String outputAppPath = path + "//" + projectName + "//";

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

			System.out.println("input app path : "+inputAppPath);
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
														outputAppPath,projectName);

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

	private HashMap<String, List<JTextField>> map = new HashMap<String, List<JTextField>>();
}
