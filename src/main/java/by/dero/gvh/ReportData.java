package by.dero.gvh;

import by.dero.gvh.model.StorageInterface;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ReportData {
    private final StorageInterface storage;

    public ReportData(StorageInterface storage) {
        this.storage = storage;
    }

    public void saveBug(String playerName, String text) {
        try {
            ReportInfo info = new ReportInfo(playerName, text);
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            info.setDate(dateFormat.format(date));
            storage.save("bugReports", dateFormat.format(date) + new Random().nextInt(),
                    new GsonBuilder().setPrettyPrinting().create().toJson(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveAdvice(String playerName, String text) {
        try {
            ReportInfo info = new ReportInfo(playerName, text);
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            info.setDate(dateFormat.format(date));
            storage.save("adviceReports", dateFormat.format(date) + new Random().nextInt(),
                    new GsonBuilder().setPrettyPrinting().create().toJson(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public StorageInterface getStorage() {
        return storage;
    }
}
