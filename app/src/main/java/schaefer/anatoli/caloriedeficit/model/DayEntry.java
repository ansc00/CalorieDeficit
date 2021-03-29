package schaefer.anatoli.caloriedeficit.model;

import java.util.ArrayList;

public class DayEntry {
    private int id;
    private String date;
    private float weight;
    private int dayDeficit;
    private byte[] dayImage;
    private int performanceValue;
    private int sumAte;
    private int sumSport;

    private ArrayList<EntryItem> entryItems;
    private ArrayList<SportEntryItem> sportEntryItems;

    public DayEntry(){
        entryItems = new ArrayList<EntryItem>();
        sportEntryItems = new ArrayList<SportEntryItem>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSumSport() {
        return sumSport;
    }

    public void setSumSport(int sumSport) {
        this.sumSport = sumSport;
    }

    public ArrayList<SportEntryItem> getSportEntryItems() {
        return sportEntryItems;
    }

    public void setSportEntryItems(ArrayList<SportEntryItem> sportEntryItems) {
        this.sportEntryItems = sportEntryItems;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getDayDeficit() {
        return dayDeficit;
    }

    public void setDayDeficit(int dayDeficit) {
        this.dayDeficit = dayDeficit;
    }

    public byte[] getDayImage() {
        return dayImage;
    }

    public void setDayImage(byte[] dayImage) {
        this.dayImage = dayImage;
    }

    public int getPerformanceValue() {
        return performanceValue;
    }

    public void setPerformanceValue(int performanceValue) {
        this.performanceValue = performanceValue;
    }

    public int getSumAte() {
        return sumAte;
    }

    public void setSumAte(int sumAte) {
        this.sumAte = sumAte;
    }

    public ArrayList<EntryItem> getEntryItems() {
        return entryItems;
    }

    public void setEntryItems(ArrayList<EntryItem> entryItems) {
        this.entryItems = entryItems;
    }
}
