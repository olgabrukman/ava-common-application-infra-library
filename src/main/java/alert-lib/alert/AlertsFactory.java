package alert;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class AlertsFactory {
    private File alertsDescriptionFile;

    public AlertsFactory(String alertsDescriptionFileName) {
        alertsDescriptionFile = new File(alertsDescriptionFileName);
    }

    public AlertsFactory(File alertsDescriptionFile) {
        this.alertsDescriptionFile = alertsDescriptionFile;
    }

    public AlertsContainer createAllAlerts() throws IOException {
        try (BufferedReader alertsReader = new BufferedReader(new FileReader(alertsDescriptionFile))) {
            return getAlertsWorker(alertsReader);
        }
    }

    private AlertsContainer getAlertsWorker(BufferedReader reader) throws IOException {
        AlertsContainer allAlerts = new AlertsContainer();
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
        List<CSVRecord> csvRecordList = parser.getRecords();

        for (CSVRecord csvRecord : csvRecordList) {
            if (csvRecord.getRecordNumber() == 1) {
                // Ignore first line header
                continue;
            }

            Alert alert = new Alert(csvRecord);
            allAlerts.setAlert(alert.getId(), alert);
        }
        return allAlerts;
    }
}
