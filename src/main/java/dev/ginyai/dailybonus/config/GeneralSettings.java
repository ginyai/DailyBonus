package dev.ginyai.dailybonus.config;

import com.google.common.collect.ImmutableList;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@ConfigSerializable
public class GeneralSettings {

    @Setting(value = "Storage", comment = "Settings for data storage")
    private StorageSettings storageSettings = new StorageSettings();

    @Setting("Locale")
    private String locale = Locale.getDefault().toLanguageTag();

    @Setting(value = "StartOfDay", comment = "When a new day start.")
    private String startOfDay = "04:00";

    @Setting(value = "StartOfWeek", comment = "Which day is a new week start.")
    private DayOfWeek startOfWeek = DayOfWeek.MONDAY;

    @Setting(value = "SettingsDir")
    private List<String> settingsDir = ImmutableList.of("%data_dir%/settings");

    public StorageSettings getStorageSettings() {
        return storageSettings;
    }

    public String getLocale() {
        return locale;
    }

    public String getStartOfDay() {
        return startOfDay;
    }

    public DayOfWeek getStartOfWeek() {
        return startOfWeek;
    }

    public List<String> getSettingsDir() {
        return settingsDir;
    }
}
