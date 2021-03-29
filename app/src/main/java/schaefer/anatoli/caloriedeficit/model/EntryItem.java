package schaefer.anatoli.caloriedeficit.model;

public class EntryItem {
    private int id;
    private String label;
    private int calories;
    //private String date;

    private int dayEntryId_FK;

    private boolean isDeleteButtonVisible = false;

    public EntryItem() {
    }

    public boolean isDeleteButtonVisible() {
        return isDeleteButtonVisible;
    }

    public void setDeleteButtonVisible(boolean deleteButtonVisible) {
        isDeleteButtonVisible = deleteButtonVisible;
    }

    public int getDayEntryId_FK() {
        return dayEntryId_FK;
    }

    public void setDayEntryId_FK(int dayEntryId_FK) {
        this.dayEntryId_FK = dayEntryId_FK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

   /* public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    */

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "EntryItem{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", calories=" + calories +
                ", dayEntryId_FK=" + dayEntryId_FK +
                ", isDeleteButtonVisible=" + isDeleteButtonVisible +
                '}';
    }
}
