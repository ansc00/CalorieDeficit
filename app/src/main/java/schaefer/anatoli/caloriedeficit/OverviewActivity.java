package schaefer.anatoli.caloriedeficit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

 */
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.DayEntry;
import schaefer.anatoli.caloriedeficit.model.EntryItem;
import schaefer.anatoli.caloriedeficit.model.PersonalData;
import schaefer.anatoli.caloriedeficit.model.SportEntryItem;
import schaefer.anatoli.caloriedeficit.overviewfragment.OverviewFragment;
import schaefer.anatoli.caloriedeficit.util.CalculationFormula;
import schaefer.anatoli.caloriedeficit.util.OnSwipeTouchListener;
import schaefer.anatoli.caloriedeficit.util.PersonalDataConfiguration;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SHARED_PREFERENCE = "SHARED_PREF";
    private TextView bmiValueTextView;
    private TextView baseValueTextView;
    private TextView performanceValueTextView;
    private EditText spendingKcalValueEditText;

    private FrameLayout  overviewFragmentPlaceholder;

    private ImageView bmiInfoImageView;
    private ImageView baseInfoImageView;
    private ImageView performanceInfoImageView;
    private ImageView spendingKcalInfoImageView;

    private DatabaseHelper db;
    private PersonalData personalData = null;

    private Dialog dialog;

    private DayEntry dayEntry = null;

    private Date date;
    private int dateCount = 0;

    private  ArrayList<DayEntry> dayEntries;

   // private AdView mAdView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        /*
        //google adds
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mAdView = findViewById(R.id.adView);
        //RequestConfiguration.Builder requestConfiguration = new  RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("2E6E3B42980A9D601CDF999BA64F090E"));
        //requestConfiguration.build();

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

         */

        //Log.d("test","onCreate OverViewActivity");
        db = DatabaseHelper.getInstance(this);

        bmiValueTextView = findViewById(R.id.bmiValueTextView);
        baseValueTextView = findViewById(R.id.baseValueTextView);
        performanceValueTextView = findViewById(R.id.performanceValueTextView);
        spendingKcalValueEditText = findViewById(R.id.spendingKcalValueEditText);
        spendingKcalValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //// Log.d("test", "" + s);
                SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("deficitValueString", s.toString() );
                editor.apply();
            }
        });




        bmiInfoImageView = findViewById(R.id.bmiInfoImageView);
        baseInfoImageView = findViewById(R.id.baseInfoImageView);
        performanceInfoImageView = findViewById(R.id.performanceInfoImageView);
        spendingKcalInfoImageView = findViewById(R.id.spendingKcalInfoImageView);
        bmiInfoImageView.setOnClickListener(this);
        baseInfoImageView.setOnClickListener(this);
        performanceInfoImageView.setOnClickListener(this);
        spendingKcalInfoImageView.setOnClickListener(this);

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
        String deficitValueString = sharedPref.getString("deficitValueString","500");
        spendingKcalValueEditText.setText(deficitValueString);


        try{
            personalData = db.getPersonalData(PersonalDataConfiguration.PERSONALDATA_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(personalData != null){

            int weight;
            int bodySize;
            if(getResources().getConfiguration().locale.toString().equals(Locale.GERMANY.toString())){ //german
                weight =  personalData.getWeight();
                bodySize = personalData.getBodySize();
            }else{//Default feet, inches
                weight = (int) Math.round ( CalculationFormula.lbsToKg(  personalData.getWeight() ));
                int bodySizeValue = personalData.getBodySize();
                String bodySizeValueString = String.valueOf(bodySizeValue);
                int feet = Integer.parseInt( bodySizeValueString.substring(0,1) );
                int inches = Integer.parseInt( bodySizeValueString.substring(1));
                bodySize = CalculationFormula.feetInchesToCm(feet,inches);
            }

            bmiValueTextView.setText( CalculationFormula.calculateBMI( bodySize , weight) );
            int baseCalorieValue = (int) CalculationFormula.calculateBaseCalorieValue( personalData.getGender(), bodySize, weight , personalData.getAge());
            baseValueTextView.setText( String.valueOf( baseCalorieValue) );
            performanceValueTextView.setText( CalculationFormula.calculatePerformanceValue(personalData.getPalActivity(), baseCalorieValue ) );
        }


        //alle dayEntries aus DB holen
       dayEntries = db.getAllDayEntriesSortedByDate();
      //  for(DayEntry d: dayEntries){
       //     Log.d("test3", d.getDate() + ", id: " + d.getId() +", weight: "+ d.getWeight());
       // }




        //left right, buttons
        final ImageView overviewLeftArrowIV = findViewById(R.id.overviewLeftArrowIV);
        final ImageView overviewRightArrowIV = findViewById(R.id.overviewRightArrowIV);
        overviewLeftArrowIV.setOnClickListener(this);
        overviewRightArrowIV.setOnClickListener(this);
        if(dayEntries.size() == 0){
            overviewLeftArrowIV.setVisibility(View.INVISIBLE);
            overviewRightArrowIV.setVisibility(View.INVISIBLE);
        }
        /*
        for(int i=3; i<= 10; i++) {
            db.deleteDayEntry(i);
        } */
        //db.deleteDayEntry(3);

        overviewFragmentPlaceholder = findViewById(R.id.overviewFragmentPlaceholder);

        if(dayEntries.size() == 0){
            overviewFragmentPlaceholder.setVisibility(View.INVISIBLE);
        }else {
            overviewFragmentPlaceholder.setOnTouchListener(new OnSwipeTouchListener(this) {

                @Override
                public void onSingleTap() {

                    //Log.d("test","----CLICKED");
                    Intent goToAddDayEntryIntent = new Intent(OverviewActivity.this, AddDayEntryActivity.class);
                    int id;
                    String date;
                    if(dayEntry != null){
                        id = dayEntry.getId();
                        date = dayEntry.getDate();

                        goToAddDayEntryIntent.putExtra("selectedDayEntryId", id);
                        goToAddDayEntryIntent.putExtra("selectedDayEntryDate", date );
                        startActivity(goToAddDayEntryIntent);
                        finish();
                    }else{
                        Toast.makeText(OverviewActivity.this, R.string.java_overview_activity_the_entry_does_not_exist_however_you_can_add_the_day_via_the_menu, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onTapLongPress() {
                    final Dialog deleteDialog = new Dialog(OverviewActivity.this);
                    deleteDialog.setContentView(R.layout.delete_item_layout);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView deleteItemDialogMessageTV = deleteDialog.findViewById(R.id.deleteItemDialogMessageTV);
                    deleteItemDialogMessageTV.setText(R.string.dialog_delete_text_message);

                    Button deleteItemDialogDeleteButton = deleteDialog.findViewById(R.id.deleteItemDialogDeleteButton);
                    Button deleteItemDialogCancelButton = deleteDialog.findViewById(R.id.deleteItemDialogCancelButton);
                    deleteItemDialogDeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //aus db löschen
                            try{
                                if(dayEntry != null){ //dayEntry nur HÜLLE ! keine entryItems drin!



                                    ArrayList<EntryItem> entryItems = db.getAllEntryItemsByDayEntryId( dayEntry.getId() );
                                    ArrayList<SportEntryItem> sportEntryItems = db.getAllSportEntryItemsByDayEntryId( dayEntry.getId() );


                                    //entry items löschen (essen)
                                   if( entryItems.size() > 0){
                                        for(int i=0; i< entryItems.size(); i++){
                                            db.deleteEntryItem( entryItems.get(i).getId() );
                                        }
                                   }

                                   //sport entry items löschen (Sport)
                                   if( sportEntryItems.size() > 0){
                                       for(int i=0; i< sportEntryItems.size(); i++){
                                           db.deleteSportEntryItem( sportEntryItems.get(i).getId() );
                                       }
                                   }
                                   //dayEntry löschen
                                   int hasWorked = -1;
                                   hasWorked = db.deleteDayEntry( dayEntry.getId() );
                                   if(hasWorked != -1){
                                       Toast.makeText(getApplicationContext(), R.string.java_overview_activity_day_entry_successfully_delted, Toast.LENGTH_SHORT).show();
                                   }else {
                                       Toast.makeText(getApplicationContext(), R.string.java_overview_activity_ops_an_error_during_delete, Toast.LENGTH_SHORT).show();
                                   }

                                   dayEntries.remove( dayEntry);



                                   //dayEntry = null;
                                   //aktuellen Tag versuchen anzuzeigen
                                   date = new Date();
                                   dateCount = 0;
                                   instanciateDayEntry(date);

                                }else{
                                    Toast.makeText(getApplicationContext(), R.string.overview_activity_this_entry_does_not_exist, Toast.LENGTH_SHORT).show();
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            deleteDialog.dismiss();
                        }
                    });
                    deleteItemDialogCancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });

                    deleteDialog.show();
                }

                @Override
                public void onSwipeLeft() {
                    // Whatever
                    dateCount++;
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, dateCount); //gestern
                    date = calendar.getTime();

                    instanciateDayEntry(date);

                    if(dayEntry != null){
                        shakeAnimationLeft();
                    }
                }

                @Override
                public void onSwipeRight() {
                    // Whatever
                    dateCount--;
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, dateCount); //gestern
                    date = calendar.getTime();

                    instanciateDayEntry(date);

                    if(dayEntry != null){
                        shakeAnimationRight();
                    }

                }


            });
            date = new Date();
            instanciateDayEntry(date);
        }



    }



    private void shakeAnimationLeft(){
        Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_animation_left);
        overviewFragmentPlaceholder.setAnimation(shakeAnim);

    }
    private void shakeAnimationRight(){
        Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_animation_right);
        overviewFragmentPlaceholder.setAnimation(shakeAnim);

    }

    private void instanciateDayEntry(Date date) {
        dayEntry = null;
        String dateParamForDB = "";
        String formatedDateForFragment = "";

        String pattern;
        String pattern2;
        Locale locale = getResources().getConfiguration().locale;
        if(locale.toString().equals(Locale.GERMANY.toString())){
            pattern = "dd.MM.YYYY";
            pattern2 = "EEE dd.MM.YYYY";
        }else{
            pattern = "MM/dd/yy";
            pattern2 = pattern;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        dateParamForDB = sdf.format(date);


        sdf = new SimpleDateFormat(pattern2, locale);
        formatedDateForFragment = sdf.format(date);




        try{
            dayEntry = db.getDayEntryByDate( dateParamForDB );
            //Log.d("test", "->>>" + dayEntry.getDate() );
        }catch (Exception e){
            e.printStackTrace();
        }


        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        OverviewFragment overviewFragment = new OverviewFragment();

        if(dayEntry != null){

            bundle.putString("date", formatedDateForFragment);
            bundle.putInt("perfValue", dayEntry.getPerformanceValue() );
            bundle.putInt("defValue", dayEntry.getDayDeficit() );
            bundle.putInt("sumAteValue", dayEntry.getSumAte() );
            bundle.putInt("sumSportValue", dayEntry.getSumSport() );

            overviewFragment.setArguments(bundle);
            fm.beginTransaction().replace(R.id.overviewFragmentPlaceholder, overviewFragment).commit();

            //shakeAnimationRight();
        }else {


            Toast.makeText(OverviewActivity.this, getString(R.string.java_overview_activity_there_was_no_entry_for_the) + " " + dateParamForDB + " " +getString(R.string.java_overview_activity_found) , Toast.LENGTH_SHORT).show();
            bundle.putString("date", formatedDateForFragment);
            bundle.putString("NODATA", formatedDateForFragment);
            overviewFragment.setArguments(bundle);
            fm.beginTransaction().replace(R.id.overviewFragmentPlaceholder, overviewFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settingsItem:
                //zu den Einstellungen wechseln
                startActivity(new Intent(OverviewActivity.this, SettingsActivity.class));
                return true;
            case R.id.addItem:
                //neues Item hinzufügen
                int calorieDeficit = 0;
                int performanceValue = 0;

                try {
                    //kann vom user leer gelassen werden
                    if (!TextUtils.isEmpty(spendingKcalValueEditText.getText().toString())) {
                        calorieDeficit = Integer.parseInt(spendingKcalValueEditText.getText().toString());
                    }
                    //kann nicht leer sein!
                    performanceValue = Integer.parseInt(performanceValueTextView.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent addItemIntent = new Intent(OverviewActivity.this, AddDayEntryActivity.class);
                addItemIntent.putExtra("calorieDeficit", calorieDeficit);
                addItemIntent.putExtra("performanceValue", performanceValue);
                //addItemIntent.putExtra("palValue", personalData.getPalActivity());
                //addItemIntent.putExtra("baseValue", Integer.parseInt( baseValueTextView.getText().toString() ) );
                if (personalData != null) {
                    addItemIntent.putExtra("defaultWeight", personalData.getWeight());
                   // Log.d("OverviewActivity","" + personalData.getWeight());
                }
                finish();
                startActivity(addItemIntent);

                return true;
            case R.id.statisticsItem:
                if(dayEntries.size() == 0){
                    //keine daten vorhanden
                    Toast.makeText(OverviewActivity.this, R.string.java_overview_activity_you_must_first_add_data_to_view_statistics, Toast.LENGTH_SHORT).show();

                }else {
                    //zur Auswertung Seite gehen
                    startActivity(new Intent(OverviewActivity.this, StatsActivity.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Override
    public void onClick(View v) {



        switch (v.getId()){
            case R.id.bmiInfoImageView:

                double val = Double.parseDouble( bmiValueTextView.getText().toString().replace(",",".") );
                String weightString;
                if(val > 18.4){
                    weightString = getString(R.string.java_overview_activity_so_you_have_normal_weight);
                    if(val > 24.9){
                        weightString = getString(R.string.java_overview_activity_so_you_are_overweight);
                        if( val > 29.9){
                            weightString = getString(R.string.java_overview_activity_you_have_obesity);
                        }
                    }
                }else {
                    weightString = getString(R.string.java_overview_activity_your_are_underweight);
                }

                //string resource ref
                //int[] bmi_info = new int[]{R.string.bmi_info_title, R.string.bmi_info_message_text};

                showCustomDialog( getResources().getString(R.string.bmi_info_title), getResources().getString(R.string.bmi_info_message_text)
                        +  "\n\n "+ getString(R.string.java_overview_activity_your_value_is) + " " + val + " " + getString(R.string.java_overview_activity_and) + " " + weightString);
                break;
            case R.id.baseInfoImageView:
                showCustomDialog(getResources().getString(R.string.base_info_title ),getResources().getString( R.string.base_info_message_text ));
                break;
            case R.id.performanceInfoImageView:
                showCustomDialog(getResources().getString(R.string.performance_info_title ),getResources().getString( R.string.performance_info_message_text ));
                break;
            case R.id.spendingKcalInfoImageView:
                showCustomDialog( getResources().getString(R.string.calories_deficit_info_title ),getResources().getString( R.string.calories_deficit_info_message_text ));
                break;
            case R.id.overviewLeftArrowIV: //walktrough
            case R.id.overviewRightArrowIV:
                //irgendeine View übergeben die in der OverviewActivity besteht!
                Snackbar.make( bmiValueTextView , R.string.java_overview_activity_wipe_the_card_to_the_left_or_right, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void showCustomDialog(String title, String message) {
        dialog  = new Dialog(OverviewActivity.this, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        dialog.setContentView(R.layout.overview_info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView overviewInfoDialogTV = dialog.findViewById(R.id.overviewInfoDialogTV);
        TextView overviewInfoDialogTitleTV = dialog.findViewById(R.id.overviewInfoDialogTitleTV);
        overviewInfoDialogTitleTV.setText(title);
        overviewInfoDialogTV.setText(message);
        TextView overviewInfoDialogOKButton = dialog.findViewById(R.id.overviewInfoDialogOKButton);
        overviewInfoDialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
       /* if (mAdView != null) {
            mAdView.pause();
        }

        */
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
      /*  if (mAdView != null) {
            mAdView.resume();
        }

       */
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
      /*  if (mAdView != null) {
            mAdView.destroy();
        }

       */
        super.onDestroy();
    }
}