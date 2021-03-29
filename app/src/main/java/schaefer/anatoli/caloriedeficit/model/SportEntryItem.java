package schaefer.anatoli.caloriedeficit.model;

public class SportEntryItem {
    private int id;
    private String label;
    private int calories;

    private int dayEntryId;

    private boolean isDeleteButtonVisible = false;

    public SportEntryItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getDayEntryId() {
        return dayEntryId;
    }

    public void setDayEntryId(int dayEntryId) {
        this.dayEntryId = dayEntryId;
    }

    public boolean isDeleteButtonVisible() {
        return isDeleteButtonVisible;
    }

    public void setDeleteButtonVisible(boolean deleteButtonVisible) {
        isDeleteButtonVisible = deleteButtonVisible;
    }

    @Override
    public String toString() {
        return "SportEntryItem{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", calories=" + calories +
                ", dayEntryId=" + dayEntryId +
                ", isDeleteButtonVisible=" + isDeleteButtonVisible +
                '}';
    }
}
