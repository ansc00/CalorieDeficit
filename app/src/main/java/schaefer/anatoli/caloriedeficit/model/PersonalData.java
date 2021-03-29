package schaefer.anatoli.caloriedeficit.model;

import java.util.Arrays;
import java.util.Date;

public class PersonalData {

    private int id;
    private String gender;
    private int bodySize;
    private int weight;
    private int age;
    private float palActivity;
    private byte[] firstImage;
    private String date;


    public PersonalData() {
    }

    public PersonalData(int id, String gender, int bodySize, int weight,int age, float palActivity, byte[] firstImage, String date) {
        this.id = id;
        this.gender = gender;
        this.bodySize = bodySize;
        this.weight = weight;
        this.age = age;
        this.palActivity = palActivity;
        this.firstImage = firstImage;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getBodySize() {
        return bodySize;
    }

    public void setBodySize(int bodySize) {
        this.bodySize = bodySize;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public float getPalActivity() {
        return palActivity;
    }

    public void setPalActivity(float palActivity) {
        this.palActivity = palActivity;
    }

    public byte[] getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(byte[] firstImage) {
        this.firstImage = firstImage;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "PersonalData{" +
                "id=" + id +
                ", gender='" + gender + '\'' +
                ", bodySize=" + bodySize +
                ", weight=" + weight +
                ", age=" + age +
                ", palActivity=" + palActivity +
                ", firstImage=" + Arrays.toString(firstImage) +
                ", date='" + date + '\'' +
                '}';
    }
}
