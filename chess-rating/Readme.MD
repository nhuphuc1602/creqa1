# Chess Rating Exporter

A Java application that scrapes chess player ratings from chess.com/vi/ratings, filters players based on rating thresholds, and exports data to Google Sheets.

## Features

- Input minimum rating threshold from user
- Scrapes player data from chess.com
- Filters players whose ratings meet/exceed threshold
- Creates timestamped Google Sheets
- Exports filtered data with all columns (Rank, Name, Classical, Rapid, Blitz)
- Saves Google Sheet URL to output.txt

## Technologies Used

- Java 17
- Selenium WebDriver (ChromeDriver)
- WebDriverManager
- Google Sheets API
- Maven

## Setup and Run Instructions

### 1. Prerequisites

- Java 17 or higher
- Maven
- IDE (IntelliJ IDEA, Eclipse, or VS Code with Java support)

Verify installations:
```bash
java -version
mvn -v
```

### 2. Clone Repository

```bash
git clone https://github.com/nhuphuc1602/creqa1.git
cd creqa1/chess-rating
```

### 3. Google API Setup
I have already created credential file in codebase, but Google will dectect credential and restricted it when I upload to Github, so you need to create your own file, you can follow the following way:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create/select a project
3. Enable Google Sheets API and Drive API
4. Create Service Account:
    - Navigate to IAM & Admin > Service Accounts
    - Create new Service Account
    - Manage keys -> Add key -> Create new key
    - Download JSON key file
    - Rename to service-account.json and replace JSON file in `src/main/resources/service-account.json`
5. Share target Google Drive folder with Service Account email

### 4. Build and Run

Build project:
```bash
mvn clean compile
```

Run application:
```bash
mvn exec:java
```

Enter minimum rating when prompted (e.g., 2400)

### Project Structure

```
chess-rating-exporter/
├── src/
│   ├── main/
│   │   ├── java/com/chess/scraper/
│   │   │   ├── Main.java
│   │   │   ├── GoogleSheetsExporter.java
│   │   │   └── RatingsPage.java
│   │   └── resources/
│   │       └── service-account.json
│   └── test/
└── output.txt
```

## Notes

- Uses Service Account for Google API authentication
- Ensure Service Account has proper permissions
- Keep credentials file secure
- Do not commit credentials to public repos

## References

- [Google Sheets API Java Quickstart](https://developers.google.com/sheets/api/quickstart/java)
- [Google Drive API](https://developers.google.com/drive/api/guides/about-sdk)
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [WebDriverManager](https://github.com/bonigarcia/webdrivermanager)
