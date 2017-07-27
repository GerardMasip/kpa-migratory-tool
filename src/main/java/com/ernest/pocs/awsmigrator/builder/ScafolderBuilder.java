package com.ernest.pocs.awsmigrator.builder;

import com.ernest.pocs.awsmigrator.writer.FileWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ScafolderBuilder {

    @Autowired
    FileWriter fileWriter;

    @Value("${gradle.directory}")
    private String gradleDirectory;

    public void create(String destinationPath, String projectName, String mainPackage) throws IOException {
        System.out.println("***** STARTING " + projectName + " *****");
        String rootFolder = destinationPath+"//"+projectName;

        createRootProjectFolder(rootFolder);
        System.out.println("Completed: " + rootFolder);

        copyGradle(rootFolder);
        System.out.println("Completed: Gradle wrapper");

        createSettingGradleFile(projectName, rootFolder);
        System.out.println("Completed: settings.gradle");

        createGradlePropertiesFile(rootFolder);
        System.out.println("Completed: gradle.properties");

        createSpinnakerFolder(projectName, rootFolder);
        System.out.println("Completed: spinnaker configuration");

        createGitIgnoreRoot(rootFolder);
        System.out.println("Completed: .gitignore root folder");

        createBuildGradleRootFile(rootFolder);
        System.out.println("Completed: build.gradle root folder");

        createProjectModules(rootFolder, projectName);
        System.out.println("Completed: ct module and ms module folders");

        createBuildGradleCtFile(rootFolder, projectName);
        System.out.println("Completed: build.gradle *-ct folder");

        createBuildGradleMsFile(rootFolder, projectName, mainPackage);
        System.out.println("Completed: build.gradle *-ms folder");

        createEurekaConfig(rootFolder);
        System.out.println("Completed: EurekaConfig created");
        System.out.println("***** COMPLETED " + projectName + " *****");
    }

    private void createEurekaConfig(String rootFolder) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/config/EurekaConfig.java"));
        new File(rootFolder+"//config").mkdir();
        fileWriter.writeIntoFile(rootFolder+"//config//EurekaConfig.java", content);
    }

    private void createBuildGradleMsFile(String rootFolder, String projectName, String mainPackage) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/build.gradle_Ms"));
        content = content.replaceAll("#WithDashesProjectName", projectName);
        content = content.replaceAll("#WithoutDashesProjectName", projectName.replaceAll("-",""));
        System.out.println(mainPackage);
        content = content.replaceAll("#mainPackage", mainPackage);
        fileWriter.writeIntoFile(rootFolder + "//"+projectName+"-ms//build.gradle", content);
    }

    private void createBuildGradleCtFile(String rootFolder, String projectName) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/build.gradle_Ct"));
        content = content.replaceAll("#projectName", projectName);
        fileWriter.writeIntoFile(rootFolder + "//"+projectName+"-ct//build.gradle", content);
    }

    private void createProjectModules(String rootFolder, String projectName) {
        new File(rootFolder+"//"+projectName+"-ms").mkdir();
        new File(rootFolder+"//"+projectName+"-ct").mkdir();
    }

    private void createBuildGradleRootFile(String rootFolder) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/build.gradle_Root"));
        fileWriter.writeIntoFile(rootFolder + "//build.gradle", content);

    }

    private void createGitIgnoreRoot(String rootFolder) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/.gitignore_Root"));
        fileWriter.writeIntoFile(rootFolder + "//.gitignore", content);
    }

    private void createGradlePropertiesFile(String rootFolder) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/gradle.properties"));
        fileWriter.writeIntoFile(rootFolder + "//gradle.properties", content);
    }

    private void createSpinnakerFolder(String projectName, String rootFolder) throws IOException {
        new File(rootFolder+"//spinnaker").mkdir();
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/spinnaker/job.yml"));
        content = content.replaceAll("#WithDashesProjectName", projectName);
        content = content.replaceAll("#WithoutDashesProjectName", projectName.replaceAll("-",""));
        fileWriter.writeIntoFile(rootFolder + "//spinnaker//job.yml", content);
    }

    private void copyGradle(String rootFolder) throws IOException {
        FileUtils.copyDirectory(
                FileUtils.getFile(gradleDirectory + "//gradle"),
                FileUtils.getFile(rootFolder + "//gradle"));

        FileUtils.copyFileToDirectory(
                FileUtils.getFile(gradleDirectory + "//./gradlew"),
                FileUtils.getFile(rootFolder));

        Runtime.getRuntime().exec("chmod 777 "+rootFolder+"//gradlew");
    }

    private void createRootProjectFolder(String rootFolder) {
        new File(rootFolder).mkdir();
    }

    private void createSettingGradleFile(String projectName, String rootFolder) throws IOException {
        String content  = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("filesScafolder/settings.gradle"));
        content = content.replaceAll("#serviceName", projectName);
        fileWriter.writeIntoFile(rootFolder + "//settings.gradle", content);
    }

}
