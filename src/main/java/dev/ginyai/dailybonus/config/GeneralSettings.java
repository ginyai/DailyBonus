package dev.ginyai.dailybonus.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.DayOfWeek;
import java.util.Locale;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@ConfigSerializable
public class GeneralSettings {

    @Setting(value = "Storage", comment = "Settings for data storage")
    private StorageSettings storageSettings = new StorageSettings();

//  todo:
//    @Setting("Locale")
    private Locale locale = Locale.getDefault();

    @Setting(value = "StartOfDay", comment = "When a new day start.")
    private String startOfDay = "04:00";

    @Setting(value = "StartOfWeek", comment = "Which day is a new week start.")
    private DayOfWeek startOfWeek = DayOfWeek.MONDAY;

    public StorageSettings getStorageSettings() {
        return storageSettings;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getStartOfDay() {
        return startOfDay;
    }

    public DayOfWeek getStartOfWeek() {
        return startOfWeek;
    }
}
