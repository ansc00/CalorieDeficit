package schaefer.anatoli.caloriedeficit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.PersonalData;
import schaefer.anatoli.caloriedeficit.util.PersonalDataConfiguration;

public class StartActivity extends AppCompatActivity {

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //actionbar übergang entfernen
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().hide();

        PersonalData personalData = null;
        DatabaseHelper db = DatabaseHelper.getInstance(StartActivity.this);
        try {
            personalData = db.getPersonalData(PersonalDataConfiguration.PERSONALDATA_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(personalData != null){
            finish();
            startActivity(new Intent(StartActivity.this, OverviewActivity.class));
        }


        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, SettingsActivity.class));
            }
        });
    }
}