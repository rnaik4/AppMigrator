package com.migrator.appmigrator;

import com.migrator.appmigrator.util.FileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class AppMigratorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(AppMigratorApplication.class).headless(false).run(args);
		FrameworkMigrator migrator = new FrameworkMigrator();
	}
}
