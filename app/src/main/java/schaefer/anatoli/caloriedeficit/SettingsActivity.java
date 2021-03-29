package schaefer.anatoli.caloriedeficit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Objects;

import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.PersonalData;
import schaefer.anatoli.caloriedeficit.settingsfragments.ActivityDataFragment;
import schaefer.anatoli.caloriedeficit.settingsfragments.BeginImageFragment;
import schaefer.anatoli.caloriedeficit.settingsfragments.PersonalDataFragment;
import schaefer.anatoli.caloriedeficit.util.PersonalDataConfiguration;

public class SettingsActivity extends AppCompatActivity {


    public static final String TAG = "SettingsActivity";
    private boolean isShownFirstTime = true;
    private PersonalData personalData = null;

    private PersonalData tmpPersonalData;
    private  DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //wird durch die Fragmente geführt und am Ende ggf. gespeichert!
        //Fragmente befüllen das objekt nacheinander!
        tmpPersonalData =  new PersonalData();
        tmpPersonalData.setId(PersonalDataConfiguration.PERSONALDATA_ID);

        db =  DatabaseHelper.getInstance(SettingsActivity.this);
        Bundle bundle = new Bundle();


        try {
            personalData = db.getPersonalData(PersonalDataConfiguration.PERSONALDATA_ID);
        }catch (Exception e){
                e.printStackTrace();
        }

        if(personalData != null) {
            isShownFirstTime = false;

            //daten für das fragment vorbereiten
            bundle.putString("gender",personalData.getGender());
            bundle.putInt("weight",personalData.getWeight());
            bundle.putInt("bodysize",personalData.getBodySize());
            bundle.putInt("age",personalData.getAge());

           // Log.d("TEST", personalData.toString());
        }

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().hide();

        FragmentManager fm = getSupportFragmentManager();
        PersonalDataFragment personalDataFragment = new PersonalDataFragment();
        //daten an das fragment übergeben
        personalDataFragment.setArguments(bundle);

        fm.beginTransaction()
                .replace(R.id.settingsFragmentPlaceholder, personalDataFragment)
                .commit();


    }

    //Fragmente immer von der ACTIVITY aus austauschen!
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager =  getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putBoolean("isShownFirstTime", isShownFirstTime);
        //Log.d(TAG, fragment.getClass().toString());
        //Log.d(TAG, "" + ActivityDataFragment.class);

        //je nach fragment die benötigten informationen setzen aus der db
        if(personalData != null && fragment.getClass().toString().equals(String.valueOf(ActivityDataFragment.class))) {
            bundle.putFloat("palActivity", personalData.getPalActivity());
        }else if(personalData != null && fragment.getClass().toString().equals(String.valueOf(BeginImageFragment.class))){
           // Log.d("test", personalData.getDate() );
            bundle.putByteArray("firstImage", personalData.getFirstImage());
            bundle.putString("date",personalData.getDate());

        }


        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.settingsFragmentPlaceholder, fragment)
                .commit();
    }


    //zur nächsten Activity (OverviewActivity) wechseln!
    public void goToOverviewActivity(){
        if (isShownFirstTime){
            //info einblenden nur beim ERSTEN AUFRUF in der App
            showInfoDialog();
        }else {
            finish(); //kein zurück
            startActivity(new Intent(SettingsActivity.this, OverviewActivity.class));
           
        }
    }

    //show InfoDialog to User
    public void showInfoDialog(){
        final Dialog infoDialog = new Dialog(SettingsActivity.this);
        infoDialog.setContentView(R.layout.overview_info_dialog);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView overviewInfoDialogTitleTV = infoDialog.findViewById(R.id.overviewInfoDialogTitleTV);
        overviewInfoDialogTitleTV.setText(R.string.info_dialog_text_title);
        overviewInfoDialogTitleTV.setVisibility(View.GONE); //ohne ist besser!
        TextView overviewInfoDialogTV = infoDialog.findViewById(R.id.overviewInfoDialogTV);
        overviewInfoDialogTV.setText(R.string.info_dialog_message_text);
        Button overviewInfoDialogOKButton = infoDialog.findViewById(R.id.overviewInfoDialogOKButton);
        overviewInfoDialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
                isShownFirstTime = false;
                goToOverviewActivity();
            }
        });
        infoDialog.show();

    }

    public void showAlertDialog(){
        final Dialog infoDialog = new Dialog(SettingsActivity.this);
        infoDialog.setContentView(R.layout.overview_info_dialog);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView overviewInfoDialogTitleTV = infoDialog.findViewById(R.id.overviewInfoDialogTitleTV);
        overviewInfoDialogTitleTV.setText(R.string.java_settings_activity_error);
        TextView overviewInfoDialogTV = infoDialog.findViewById(R.id.overviewInfoDialogTV);
        overviewInfoDialogTV.setText(R.string.java_settings_activity_please_fill_in_the_form_completely_and_click_on_continue);
        ImageView overviewInfoDialogIV = infoDialog.findViewById(R.id.overviewInfoDialogIV);
        overviewInfoDialogIV.setVisibility(View.VISIBLE);
        overviewInfoDialogIV.setImageResource(R.drawable.ic_error_24);

        //platzhalter
        ImageView overviewInfoDialogIV2 = infoDialog.findViewById(R.id.overviewInfoDialogIV2);
        overviewInfoDialogIV2.setVisibility(View.VISIBLE);

        Button overviewInfoDialogOKButton = infoDialog.findViewById(R.id.overviewInfoDialogOKButton);
        overviewInfoDialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });
        infoDialog.show();

    }

    public void setResultFromPersonalDataFragment(String gender, int bodySize, int weight, int age){
        tmpPersonalData.setGender(gender);
        tmpPersonalData.setBodySize(bodySize);
        tmpPersonalData.setWeight(weight);
        tmpPersonalData.setAge(age);

    }

    public void setResultFromActivityDataFragment(float palValue){
        tmpPersonalData.setPalActivity(palValue);
    }

    public void setResultFromBeginImageFragment(byte[] image, String date){
        tmpPersonalData.setFirstImage(image);
        tmpPersonalData.setDate(date);
    }

    public void savePersonalDataToDB(){
        //gefülltes object über die fragmente wird in der DB gespeichert
        if(isShownFirstTime){
            db.addPersonalData(tmpPersonalData);
        }else{
            db.updatePersonalData(tmpPersonalData);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}