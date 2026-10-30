package org.gradle.wrapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Lanceur Gradle minimal et auditable, fourni pour le projet pédagogique.
 * Il utilise la distribution HTTPS déclarée dans gradle-wrapper.properties.
 * Il ne fait pas partie de la distribution officielle du wrapper Gradle.
 */
public final class GradleWrapperMain {
    private GradleWrapperMain() { }

    public static void main(String[] args) throws Exception {
        Path projectDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path propertiesFile = projectDir.resolve("gradle/wrapper/gradle-wrapper.properties");
        Properties config = new Properties();
        try (InputStream input = Files.newInputStream(propertiesFile)) {
            config.load(input);
        }
        String distributionUrl = config.getProperty("distributionUrl");
        if (distributionUrl == null || !distributionUrl.startsWith("https://services.gradle.org/distributions/")) {
            throw new IOException("Distribution Gradle HTTPS inattendue : " + distributionUrl);
        }
        String baseName = distributionUrl.substring(distributionUrl.lastIndexOf('/') + 1);
        if (!baseName.matches("gradle-[0-9.]+-bin\\.zip")) {
            throw new IOException("Nom de distribution invalide : " + baseName);
        }
        String gradleFolder = baseName.substring(0, baseName.length() - "-bin.zip".length());
        String gradleUserHome = System.getenv("GRADLE_USER_HOME");
        Path home = gradleUserHome != null && !gradleUserHome.isEmpty()
            ? Paths.get(gradleUserHome) : Paths.get(System.getProperty("user.home"), ".gradle");
        Path cache = home.resolve("wrapper/dists/profil-developpeur-" + gradleFolder);
        Path gradleHome = cache.resolve(gradleFolder);
        Path doneFile = cache.resolve(".complete");
        if (!Files.exists(doneFile)) {
            Files.createDirectories(cache);
            Path archive = cache.resolve(baseName);
            Path temporary = cache.resolve(baseName + ".tmp");
            System.out.println("Téléchargement de " + distributionUrl);
            URLConnection connection = new URL(distributionUrl).openConnection();
            connection.setConnectTimeout(25000);
            connection.setReadTimeout(60000);
            try (InputStream input = new BufferedInputStream(connection.getInputStream())) {
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(temporary, archive, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Extraction de Gradle...");
            try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archive))) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    Path target = cache.resolve(entry.getName()).normalize();
                    if (!target.startsWith(cache)) {
                        throw new IOException("Archive ZIP non sûre : " + entry.getName());
                    }
                    if (entry.isDirectory()) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(zip, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zip.closeEntry();
                }
            }
            Path gradleUnix = gradleHome.resolve("bin/gradle");
            if (Files.exists(gradleUnix)) {
                gradleUnix.toFile().setExecutable(true, false);
            }
            Files.createFile(doneFile);
            Files.deleteIfExists(archive);
        }
        boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
        Path launcher = gradleHome.resolve(windows ? "bin/gradle.bat" : "bin/gradle");
        if (!Files.isRegularFile(launcher)) {
            throw new IOException("Gradle n'a pas été extrait correctement : " + launcher);
        }
        List<String> command = new ArrayList<>();
        if (windows) {
            command.add("cmd.exe");
            command.add("/c");
        }
        command.add(launcher.toString());
        for (String arg : args) command.add(arg);
        Process process = new ProcessBuilder(command)
            .directory(projectDir.toFile())
            .inheritIO()
            .start();
        System.exit(process.waitFor());
    }
}
