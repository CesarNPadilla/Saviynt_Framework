- [**Automation Framework**](#automation-framework)
  - [1. Overview](#1-overview)
  - [2. Key Features](#2-key-features)
  - [3. Technology Stack and Versions](#3-technology-stack-and-versions)
  - [4. Folder Structure](#4-folder-structure)
  - [5. Setup Instructions](#5-setup-instructions)
    - [5.1. Prerequisites](#51-prerequisites)
    - [5.2. Clone the Repository](#52-clone-the-repository)
    - [5.3. Build the Project](#53-build-the-project)
    - [5.4. Running Tests](#54-running-tests)
    - [5.5. Allure Reporting](#55-allure-reporting)
    - [5.6. Adding New Tests](#56-adding-new-tests)
    - [5.7. Environment JSON Management](#57-environment-json-management)
    - [5.8. Manual Changes in .vscode - settings.json to use VS Testing Feature](#58-manual-changes-in-vscode---settingsjson-to-use-vs-testing-feature)
    - [5.9. Notes](#59-notes)

# **Automation Framework**

## 1. Overview
This is a Java-based Test Automation Framework designed for UI (Selenium + Cucumber), Unit/API testing (TestNG), and environment-driven JSON test data. It supports Allure reporting and is ready for CI/CD with GitLab.

## 2. Key Features
- Selenium for UI automation
- Cucumber for BDD-style test cases
- TestNG for unit and API testing
- Allure for unified reporting
- JSON-based environment management (dev, staging, prod)
- WebDriverManager for automatic browser driver management
- Maven for dependency management and build
- Git initialized with IDE-friendly .gitignore

> Ready for Windows using Git Bash

## 3. Technology Stack and Versions
| Technology        | Version   |
|-------------------|-----------|
| Java              | 17        |
| Selenium          | 4.14.0    |
| Cucumber          | 7.23.0    |
| TestNG            | 7.7.1     |
| Jackson           | 2.15.2    |
| Allure            | 2.29.1    |
| WebDriverManager  | 6.2.0     |
| Maven             | 3.9+      |
| Git               | 2.40+     |

## 4. Folder Structure
```bash
automation-framework/
│
├── pom.xml
├── README.md
├── .gitignore
│
├── src
│   ├── main/java/com/companyname/project
│   │   ├── config/             # Environment manager, JSON loader
│   │   ├── core/               # DriverFactory, BaseTest
│   │   ├── pages/              # Page Object classes
│   │   └── utils/              # Wait, Logger, Config utilities
│   │
│   ├── main/resources/
│   │   ├── testdata/           # Environment JSON files
│   │   │   └── testdata.json
│   │   ├── drivers/            # Optional WebDriver binaries
│   │   └── config.properties
│   │
│   └── test/java/com/companyname/project
│       ├── bdd/
│       │   ├── hooks/
│       │   ├── runners/
│       │   └── stepdefinitions/
│       ├── tests/
│       │   ├── ui/
│       │   ├── api/
│       │   └── unit/
│       └── hooks/
│
├── src/test/resources/  
│            ├── features/        # Cucumber .feature files
│            └── allure.properties
├── reports/allure-report/        # Allure report output
└── target/allure-results/        # Allure raw results
```

## 5. Setup Instructions

### 5.1. Prerequisites
- Install Java 17 and configure `JAVA_HOME` and `PATH`. 
- Install Maven 3.9+ and configure `MAVEN_HOME` and `PATH`.
- Install Git (2.40+ recommended).
- Install Git Bash on Windows (for running Bash scripts).

### 5.2. Clone the Repository
```bash
git clone <your-repo-url>
cd automation-framework
```
### 5.3. Build the Project
```bash
mvn clean install
```
>This will download all dependencies specified in pom.xml.

### 5.4. Running Tests
Run All Tests
```bash
mvn clean test
```
Run Tests for Specific Environment
```bash
mvn clean test -Denv=dev -Dbrowser=chrome
```
> Supported environments: dev, qa, staging, prod.

### 5.5. Allure Reporting
Generate and serve Allure report locally:
```bash
mvn allure:serve
```
> The report combines Cucumber and TestNG results.

### 5.6. Adding New Tests
- Step Definitions: Add or update in `src/test/java/com/companyname/project/bdd/stepdefinitions/`.
- Page Objects: Add classes to `src/main/java/com/companyname/project/pages/` and use Selenium + Cucumber.
- Feature Files: Add to `src/test/resources/features/`.
  
- Place Holder for - UI/E2E Tests: Add classes to `src/test/java/com/companyname/project/tests/ui/` and use Selenium + Cucumber.
- Place Holder for - API/Unit Tests: Add classes to `src/test/java/com/companyname/project/tests/api/` or `unit/` using TestNG.

### 5.7. Environment JSON Management
- Location: `src/main/resources/testdata/`
- Files: `testdata.json`
- Structure Example:
```bash
{
  "dev": { 
    "url": "https://acnllp-development.saviyntcloud.com/ECM/",
    "validUser": { "username": "devuser", "password": "Dev@123" },
    "invalidUser": { "username": "dev_invalid", "password": "dev_wrong" }
  },
}
```
> JsonDataLoader.java automatically picks the correct file based on Maven profile (-Pdev, -Pstaging, -Pprod).

### 5.8. Manual Changes in .vscode - settings.json to use VS Testing Feature

```bash
{
     // Java configuration
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.import.gradle.enabled": false,
    "java.import.maven.enabled": true,
    "java.import.gradle.offline.enabled": true,
    "java.errors.incompleteClasspath.severity": "warning",
  
    // Cucumber/Gherkin plugin
    "cucumberautocomplete.steps": [
        "src/test/java/**/*.java"
    ],
    "cucumberautocomplete.syncfeatures": "src/test/resources/features/**/*.feature",
    "cucumberautocomplete.strictGherkin": true,
    
    // Maven build directory (so VS Code can index compiled classes)
    "java.project.outputPath": "target/classes",
    "java.project.testOutputPath": "target/test-classes",
    
    // Enable Go to Definition for Java classes
    "java.referencesCodeLens.enabled": true,
    
    // Enable IntelliSense for Java
    "java.completion.enabled": true,
    
    // Optional: formatting and linting
    "java.format.settings.url": ".vscode/eclipse-formatter.xml",
    "java.format.settings.profile": "GoogleStyle",
    
  // Test Runner config
  "java.test.config": [
    {
      "name": "TestNG",
      "workingDirectory": "${workspaceFolder}",
      "args": [
        "com.companyname.project.bdd.runners.TestRunner"],   // program args (leave empty unless your tests need them)
      "vmArgs": [
        "-ea",
        "-Denv=dev",
        "-Dbrowser=chrome"
      ],
      "testKind": "testng"
    }
  ],

  // Default test framework
  "java.test.defaultConfig": "TestNG",

  // Enable TestNG + Cucumber plugin integration
  "java.test.junit.useProjectClasspath": true,

  // Cucumber plugin options (help step navigation and run features)
  "cucumber.glue": [
    "com.companyname.project.bdd.steps",
    "com.companyname.project.bdd.hooks"
  ],
  "cucumber.features": [
    "src/test/resources/features"
  ],

  // Turn off telemetry
  "telemetry.enableTelemetry": false
}
```
### 5.9. Notes
- Use of WebDriverManager to automatically manage browser drivers.
- Framework is IDE-independent (works in IntelliJ or VS Code).
- Supports parallel execution and integration with GitLab CI/CD.

This README gives a complete setup guide and reference for developers, testers, or CI engineers.
