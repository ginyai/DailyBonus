package dev.ginyai.dailybonus.config;

import dev.ginyai.dailybonus.view.chest.ChestElement;
import ninja.leaping.configurate.objectmapping.Setting;

import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
//@ConfigSerializable
public class ChestViewDisplaySettings {
    @Setting("Size")
    private int size;

    @Setting("Title")
    private String title;

    @Setting("Elements")
    private Map<Integer, ChestElement> elements;

    public ChestViewDisplaySettings(int size, String title, Map<Integer, ChestElement> elements) {
        this.size = size;
        this.title = title;
        this.elements = elements;
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public Map<Integer, ChestElement> getElements() {
        return elements;
    }
}
