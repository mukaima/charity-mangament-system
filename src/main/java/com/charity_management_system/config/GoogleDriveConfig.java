package com.charity_management_system.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {
    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();

    /**
     * Retrieves the path to the Google credentials file.
     *
     * @return The path to the credentials file.
     */
    private static String getPathToGoogleCredentials(){
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, "cred.json");
        return filePath.toString();
    }

    /**
     * Bean to create and return a Google Drive service instance.
     *
     * @return A configured Google Drive service instance.
     * @throws GeneralSecurityException If security setup fails.
     * @throws IOException              If there is an error reading the credentials file.
     */
    @Bean
    public Drive driveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .build();
    }
}
