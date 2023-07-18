package com.social.media;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@SpringBootApplication
public class SocialMediaApplication implements CommandLineRunner {

	private static void writeInPropertiesFile() {
		String username = System.getenv("username");
		String password = System.getenv("password");
		String propertiesFilePath = "src/main/resources/application.properties";

		if (username == null || password == null) {
			log.warn("You need to write your username and password in Environment Variables!");
			return;
		}

		inputAndOutputInProperties(username, password, propertiesFilePath);
	}

	private static void inputAndOutputInProperties(String username, String password, String propertiesFilePath) {
		Properties properties = new Properties();
		try {
			FileInputStream input = new FileInputStream(propertiesFilePath);
			properties.load(input);
			input.close();

			properties.setProperty("spring.datasource.username", username);
			properties.setProperty("spring.datasource.password", password);
			properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/my_social_media");

			FileOutputStream output = new FileOutputStream(propertiesFilePath);
			properties.store(output, null);

			output.flush();
			output.close();

			log.info("Username and password was successfully written in file application.properties.");
		} catch (IOException io) {
			log.error("Error: writing in property file {}", io.getMessage());
		}
	}

	public static void main(String[] args) {
		writeInPropertiesFile();
		SpringApplication.run(SocialMediaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("SocialMediaApplication has been started!!!");
	}
}
