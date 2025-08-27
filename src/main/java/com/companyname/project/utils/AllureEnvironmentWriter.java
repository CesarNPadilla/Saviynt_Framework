package com.companyname.project.utils;

import io.qameta.allure.Allure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class AllureEnvironmentWriter {

    public static void writeEnvironmentInfo(String browser, String env) {
        try {
            Properties props = new Properties();
            props.setProperty("Browser", browser);
            props.setProperty("Environment", env);
            props.setProperty("OS", System.getProperty("os.name"));
            props.setProperty("Java Version", System.getProperty("java.version"));

            File resultsDir = new File("target/allure-results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            File file = new File(resultsDir, "environment.properties");
            try (FileWriter writer = new FileWriter(file)) {
                props.store(writer, "Allure Environment Properties");
            }

            // Optional: Attach as Allure attachment for visibility
            Allure.addAttachment("Environment Details",
                    "Browser: " + browser + "\n" +
                    "Environment: " + env + "\n" +
                    "OS: " + System.getProperty("os.name") + "\n" +
                    "Java: " + System.getProperty("java.version"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

