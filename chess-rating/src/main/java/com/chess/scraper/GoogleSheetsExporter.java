package com.chess.scraper;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleSheetsExporter {

    private static final String APPLICATION_NAME = "Chess Rating Exporter";
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/service-account.json";

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(List.of(
                        "https://www.googleapis.com/auth/spreadsheets",
                        "https://www.googleapis.com/auth/drive"
                ));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Drive getDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(List.of("https://www.googleapis.com/auth/drive"));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Create new Google Sheet, write data, and share publicly as read-only.
     *
     * @param title Sheet title
     * @param data  List of rows; each row is an array of String
     * @return URL of created Google Sheet
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public String exportToSheet(String title, List<String[]> data) throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();

        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(title));
        spreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();

        String spreadsheetId = spreadsheet.getSpreadsheetId();
        String sheetName = "Sheet1";

        List<List<Object>> values = new ArrayList<>();
        for (String[] row : data) {
            values.add(Arrays.asList((Object[]) row));
        }
        ValueRange body = new ValueRange().setValues(values);

        sheetsService.spreadsheets().values()
                .update(spreadsheetId, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();

        Drive driveService = getDriveService();
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        driveService.permissions().create(spreadsheetId, permission)
                .setFields("id")
                .execute();

        return "https://docs.google.com/spreadsheets/d/" + spreadsheetId;
    }
}
