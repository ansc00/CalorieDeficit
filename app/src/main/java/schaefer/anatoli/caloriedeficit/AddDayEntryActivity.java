package schaefer.anatoli.caloriedeficit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import schaefer.anatoli.caloriedeficit.adapter.EntryItemRecyclerAdapter;
import schaefer.anatoli.caloriedeficit.adapter.SportEntryItemRecyclerAdapter;
import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.DayEntry;
import schaefer.anatoli.caloriedeficit.model.EntryItem;
import schaefer.anatoli.caloriedeficit.model.PersonalData;
import schaefer.anatoli.caloriedeficit.model.SportEntryItem;
import schaefer.anatoli.caloriedeficit.util.CalculationFormula;
import schaefer.anatoli.caloriedeficit.util.ImageConverter;
import schaefer.anatoli.caloriedeficit.util.PersonalDataConfiguration;

public class AddDayEntryActivity extends AppCompatActivity implements View.OnClickListener, EntryItemRecyclerAdapter.ItemClickListener, SportEntryItemRecyclerAdapter.SportEntryItemClickListener{

    //datumsauswahl
    private Calendar calendar;
    private EditText calendarEditText;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    //handysprach einstellung LOCALE.GERMANY
    private Locale locale;

    //weight in activity_add_day_entry.xml
    private EditText weightEditText;

    private Dialog weightDialog;
    private String currentWeightValue;
    private String currentWeightBe4DotValue;
    private String currentWeightAfterDotValue;

    //PERMISSIONS REQUEST CODES
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 1;

    //REQUEST CODE (on activity result)
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    //image
    private ImageView addEntryPictureImageView;
    private ImageView addEntryCamImageView;
    private Bitmap imageBitmap;

    //EntryItem Adapter/ Receyclerview
    private EntryItemRecyclerAdapter entryItemRecyclerAdapter;
    private RecyclerView caloriesOverviewRecyclerView;

    //SportEntryItem Adapter/ Recyclerview
    private SportEntryItemRecyclerAdapter sportEntryItemRecyclerAdapter;
    private RecyclerView sportCaloriesOverviewRecyclerView;

    //db
    private DatabaseHelper db;

    //calories_overview.xml
    private TextView caloriesOverviewSumCaloriesTextTV;
    private TextView caloriesOverviewSumCaloriesValueTV;

    //sport_calories_overview.xml
    private TextView sportCaloriesOverviewSumCaloriesTextTV;
    private TextView sportCaloriesOverviewSumCaloriesValueTV;


    //summary_overview.xml
    private LinearLayout summaryOverviewOpenCaloriesLinearLayout;
    private TextView summaryOverviewPerformanceValueTV;
    private TextView summaryOverviewDailyDeficitValueTV;
    private TextView summaryOverviewSubtotalValueTV;
    private TextView summaryOverviewAteValueTV;
    private TextView summaryOverviewOpenCaloriesValueTV;
    private TextView summaryOverviewSportTextTV;
    private TextView summaryOverviewSportValueTV;
    private TextView summaryOverviewAteTextTV;


    //Kcal defizit
    private EditText addEntrySpendingKcalValueET;


    //aktuell ausgewählter TAGes eintrag
    private DayEntry dayEntry = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day_entry);

        //db
        db = DatabaseHelper.getInstance(this);

        //kcal defizit
        addEntrySpendingKcalValueET = findViewById(R.id.addEntrySpendingKcalValueET);

        addEntryPictureImageView = findViewById(R.id.addEntryPictureImageView);
        addEntryCamImageView = findViewById(R.id.addEntryCamImageView);
        addEntryPictureImageView.setOnClickListener(this);
        addEntryCamImageView.setOnClickListener(this);

        //gewicht setzen
        weightEditText = findViewById(R.id.addEntryWeightEditText);
        weightEditText.setOnClickListener(this);

        //summary_overview.xml
        summaryOverviewOpenCaloriesLinearLayout = findViewById(R.id.summaryOverviewOpenCaloriesLinearLayout);
        summaryOverviewPerformanceValueTV = findViewById(R.id.summaryOverviewPerformanceValueTV);
        summaryOverviewDailyDeficitValueTV = findViewById(R.id.summaryOverviewDailyDeficitValueTV);
        summaryOverviewSubtotalValueTV = findViewById(R.id.summaryOverviewSubtotalValueTV);
        summaryOverviewAteValueTV = findViewById(R.id.summaryOverviewAteValueTV);
        summaryOverviewOpenCaloriesValueTV = findViewById(R.id.summaryOverviewOpenCaloriesValueTV);
        summaryOverviewAteTextTV = findViewById(R.id.summaryOverviewAteTextTV);
        summaryOverviewSportTextTV = findViewById(R.id.summaryOverviewSportTextTV);
        summaryOverviewSportValueTV = findViewById(R.id.summaryOverviewSportValueTV);

        //calories_overview.xml
        caloriesOverviewSumCaloriesTextTV = findViewById(R.id.caloriesOverviewSumCaloriesTextTV);
        caloriesOverviewSumCaloriesValueTV = findViewById(R.id.caloriesOverviewSumCaloriesValueTV);

        //sport_calories_overview.xml
        sportCaloriesOverviewSumCaloriesTextTV = findViewById(R.id.sportCaloriesOverviewSumCaloriesTextTV);
        sportCaloriesOverviewSumCaloriesValueTV = findViewById(R.id.sportCaloriesOverviewSumCaloriesValueTV);

        //backButton
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //essen add-floating button
        FloatingActionButton addEntryFAB = findViewById(R.id.addEntryFAB);
        addEntryFAB.setOnClickListener(this);

        //sport add-floating button
        FloatingActionButton addSportEntryFAB = findViewById(R.id.addSportEntryFAB);
        addSportEntryFAB.setOnClickListener(this);

        //calories_overview.xml EDIT-button
        ImageView caloriesOverviewEditIV = findViewById(R.id.caloriesOverviewEditIV);
        caloriesOverviewEditIV.setOnClickListener(this);

        //sport_calories_overview.xml EDIT-button
        ImageView sportOverviewEditIV = findViewById(R.id.sportOverviewEditIV);
        sportOverviewEditIV.setOnClickListener(this);


        //Location erfragen
        locale = getResources().getConfiguration().locale;


        //übergabe wenn man über ADD-Button kommt
        int calorieDeficit = getIntent().getIntExtra("calorieDeficit",0);
        int performanceValue = getIntent().getIntExtra("performanceValue",0);
        int defaultWeight = getIntent().getIntExtra("defaultWeight",-1);
        //float palValue = getIntent().getFloatExtra("palValue",0.0f);
        //int baseValue = getIntent().getIntExtra("baseValue",0);

        //übergabe wenn man ein ITEM bei Overview anklickt
        int selectedDayEntryId = getIntent().getIntExtra("selectedDayEntryId",-1);
        String selectedDayEntryDate = getIntent().getStringExtra("selectedDayEntryDate");



        //calendar
        calendar = Calendar.getInstance();
        calendarEditText = findViewById(R.id.addEntryDateEditText);
        calendarEditText.setOnClickListener(this);
        //falls nix übergeben wurde, AKTUELLES DATUM nehmen
        if(selectedDayEntryDate == null){ //wir kommen über den ADD-button
            updateCalendarEditTextWithLocalPattern(calendar.getTime()); //ANZEIGE aktualisieren
        }else {
            //wir kommen über den angetippten übersicht cardview
           // Log.d("test", "was er VROHER HAT: " + selectedDayEntryDate); //Thu Jul 23 20:41:54 GMT+02:00 2020 //aus new DATE() // was er hat: 23.07.2020

            if(locale.toString().equals(Locale.GERMANY.toString())){
                int idx = selectedDayEntryDate.indexOf(".");
                String dayOfMonthString = selectedDayEntryDate.substring(0, idx);

                int idx2 = selectedDayEntryDate.lastIndexOf(".");
                String monthString = selectedDayEntryDate.substring(idx + 1, idx2);

                String yearString = selectedDayEntryDate.substring(idx2 + 1);

                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, Integer.parseInt( yearString ));
                myCalendar.set(Calendar.MONTH, (Integer.parseInt( monthString ) -1)); //starts with 0 = Calendar.JANUARY
                myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt( dayOfMonthString ));
                //calendar.add(Calendar.DATE, 0); //gestern

                updateCalendarEditTextWithLocalPattern( myCalendar.getTime() );//ANZEIGE aktualisieren
            }else{
                int idx = selectedDayEntryDate.indexOf("/");
                String monthString = selectedDayEntryDate.substring(0, idx); //07/28/20

                int idx2 = selectedDayEntryDate.lastIndexOf("/");
                String dayOfMonthString = selectedDayEntryDate.substring(idx + 1, idx2);

                String yearString = selectedDayEntryDate.substring(idx2 + 1);

                Calendar myCalendar = Calendar.getInstance();

                myCalendar.set(Calendar.YEAR, Integer.parseInt( yearString ));
                myCalendar.set(Calendar.MONTH, (Integer.parseInt( monthString ) -1));
                myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt( dayOfMonthString ));
                //calendar.add(Calendar.DATE, 0); //gestern

                updateCalendarEditTextWithLocalPattern( myCalendar.getTime() );//ANZEIGE aktualisieren
            }

        }

        setUpCalenderDateListener();











        //erst wenn alle Felder gesetzt sind:
        try{
            //erst aktuelles datum aus db versuchen rasuzuholen
            if(selectedDayEntryId != -1){
                dayEntry = db.getDayEntryById(selectedDayEntryId);
            }else {
                dayEntry = db.getDayEntryByDate(calendarEditText.getText().toString());
            }
            //alle anzeige werte aktualisieren!
            //calendarEditText.setText( dayEntry.getDate() ); //ist eigtl quasi da auch schon vorher gesetzt!
            weightEditText.setText( String.valueOf( dayEntry.getWeight() ) );

            //setze defizit
            //wert überschreiben
            calorieDeficit = dayEntry.getDayDeficit();
            //Log.d("test", "dayEntry deficit: " + dayEntry.getDayDeficit());
            addEntrySpendingKcalValueET.setText(String.valueOf( calorieDeficit ));


            //falls image vorhanden, setzen
            if(dayEntry.getDayImage() != null){
                imageBitmap = ImageConverter.byteArrayToBitmap( dayEntry.getDayImage() );
                addEntryPictureImageView.setImageBitmap(imageBitmap);
                addEntryCamImageView.setVisibility(View.INVISIBLE);
            }
            performanceValue =  dayEntry.getPerformanceValue() ;
            summaryOverviewPerformanceValueTV.setText( String.valueOf(dayEntry.getPerformanceValue() ));
            summaryOverviewAteValueTV.setText( String.valueOf(dayEntry.getSumAte() ) );

            summaryOverviewSportValueTV.setText( String.valueOf(dayEntry.getSumSport()));

        }catch (Exception e){
            e.printStackTrace();
        }

        if(dayEntry == null){
            //neues erzeugen und ALLE werte aus den felder lesen + in db abspeichern
            dayEntry = new DayEntry();
            dayEntry.setDate( calendarEditText.getText().toString() );
            if( defaultWeight != -1){
                dayEntry.setWeight( defaultWeight );
            }else{
                dayEntry.setWeight( Float.parseFloat( weightEditText.getText().toString() ));
            }

            dayEntry.setDayDeficit(  calorieDeficit   );
            //dayEntry.setDayImage(  );
            dayEntry.setPerformanceValue( performanceValue );


            //dayEntry.setSumAte( Integer.parseInt( summaryOverviewAteValueTV.getText().toString() ));
            //in db speichern
            int dayEntryId = db.addDayEntry(dayEntry);
            dayEntry.setId(dayEntryId);

        }




        //set defizit
        addEntrySpendingKcalValueET.setText(String.valueOf(calorieDeficit));
        //registrier Listener auf deficit feld
        addEntrySpendingKcalValueET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                int val = 0;
                try{
                    val = Integer.parseInt(s.toString() );
                }catch (Exception e){
                    e.printStackTrace();
                }


                //in db speichern
                if(dayEntry != null){
                calculateAndSetSummaryValues( dayEntry.getPerformanceValue() , val  );
                    dayEntry.setDayDeficit( val );
                    db.updateDayEntry(dayEntry);
                }

            }
        });



        //setup weight
        setUpWeight(defaultWeight);

       //entryItems aus DB lesen
        ArrayList<EntryItem> entryItemsAL = db.getAllEntryItemsByDayEntryId( dayEntry.getId() );
        dayEntry.setEntryItems(entryItemsAL);

        //sportEntryItems aus DB lesen
        ArrayList<SportEntryItem> sportEntryItemsAL = db.getAllSportEntryItemsByDayEntryId( dayEntry.getId() );
        dayEntry.setSportEntryItems(sportEntryItemsAL);


        //Adapter verknüpfen mit arraylist und Recyclerview von ESSEN
        caloriesOverviewRecyclerView = findViewById(R.id.caloriesOverviewRecyclerView);
        //caloriesOverviewRecyclerView.setHasFixedSize(true);
        caloriesOverviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entryItemRecyclerAdapter = new EntryItemRecyclerAdapter(this, dayEntry.getEntryItems());
        entryItemRecyclerAdapter.setClickListener(this);
        caloriesOverviewRecyclerView.setAdapter(entryItemRecyclerAdapter);

        //Adapter verknüpfen mit arraylist und Recyclerview von SPORT
        sportCaloriesOverviewRecyclerView = findViewById(R.id.sportCaloriesOverviewRecyclerView);
        sportCaloriesOverviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sportEntryItemRecyclerAdapter = new SportEntryItemRecyclerAdapter(this, dayEntry.getSportEntryItems());
        sportEntryItemRecyclerAdapter.setClickListener(this);
        sportCaloriesOverviewRecyclerView.setAdapter(sportEntryItemRecyclerAdapter);


        //summary berechnen und setzen
        calculateAndSetSummaryValues(performanceValue, calorieDeficit);

        //summe Zeichen setzen ESSEN INNERHALB der CARDVIEW
        caloriesOverviewSumCaloriesTextTV.setText(Html.fromHtml("&#8721"));
        caloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumCalories() ) );

        //summe Zeichen setzen SPORT INNERHALB der CARDVIEW
        sportCaloriesOverviewSumCaloriesTextTV.setText(Html.fromHtml("&#8721"));
        sportCaloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumSportCalories() ) );


    }



    private void setUpWeight(final float defaultWeight) {
        currentWeightValue = null;
        currentWeightBe4DotValue = null;
        currentWeightAfterDotValue = null;
        int idx = -1;

        if (dayEntry != null){
            currentWeightValue = "" + dayEntry.getWeight();
        }else{
            currentWeightValue = "" + defaultWeight;
           // Log.d("AddDayEntryActivity", "" + defaultWeight);
        }

        idx = currentWeightValue.indexOf(".");
        if(idx != -1){
                currentWeightBe4DotValue = currentWeightValue.substring(0, idx);
                currentWeightAfterDotValue = currentWeightValue.substring(idx + 1);
        }


        //setup weightDialog
        weightDialog = new Dialog(this);
        weightDialog.setContentView(R.layout.weight_dialog);
        weightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //weightDialog.setTitle("Gewicht wählen");

        Button okButton = weightDialog.findViewById(R.id.weightDialogOKButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentWeightAfterDotValue == null){
                    currentWeightAfterDotValue = "0";
                }


                currentWeightValue = currentWeightBe4DotValue + "." + currentWeightAfterDotValue;
                weightEditText.setText(currentWeightValue);

                //je weniger man wiegt, desto weniger performanceValue!
                PersonalData personalData = db.getPersonalData(PersonalDataConfiguration.PERSONALDATA_ID);

                int weight;
                int bodySize;
                if(locale.toString().equals(Locale.GERMANY.toString())){
                    weight =  (int) Math.round(Double.parseDouble(currentWeightValue));
                    bodySize = personalData.getBodySize();
                }else{
                    weight = (int) Math.round ( CalculationFormula.lbsToKg( (int) Math.round(Double.parseDouble(currentWeightValue)) ));
                    int bodySizeValue = personalData.getBodySize();
                    String bodySizeValueString = String.valueOf(bodySizeValue);
                    int feet = Integer.parseInt( bodySizeValueString.substring(0,1) );
                    int inches = Integer.parseInt( bodySizeValueString.substring(1));
                    bodySize = CalculationFormula.feetInchesToCm(feet,inches);
                }


                int baseCalorieValue = (int) CalculationFormula.calculateBaseCalorieValue( personalData.getGender(), bodySize,  weight , personalData.getAge());
                String perfValue = CalculationFormula.calculatePerformanceValue( personalData.getPalActivity(), baseCalorieValue);
                calculateAndSetSummaryValues(Integer.parseInt(perfValue), Integer.parseInt( addEntrySpendingKcalValueET.getText().toString() ) );
                dayEntry.setPerformanceValue(Integer.parseInt(perfValue));

                //gewicht in db speichern
                dayEntry.setWeight( Float.parseFloat(currentWeightValue) );
                db.updateDayEntry(dayEntry);
                //Log.d("test", "->" + dayEntry.getWeight() );

                weightDialog.dismiss();
            }
        });

        NumberPicker weightBe4DotNumberPicker = weightDialog.findViewById(R.id.weightBe4DotNumberPicker);

        //text anpassen
        TextView weightTextView = findViewById(R.id.addEntryWeightTextView); //in der activity
        TextView weightDialogLabelTextView = weightDialog.findViewById(R.id.weightDialogLabelTextView); //im dialog
        if(locale.toString().equals("de_DE")){
            weightTextView.setText(R.string.add_day_entry_activity_weight_in_unit);
            weightDialogLabelTextView.setText(R.string.weight_unit);

            weightBe4DotNumberPicker.setMinValue(35);
            weightBe4DotNumberPicker.setMaxValue(300);

            //default value
            weightBe4DotNumberPicker.setValue(Integer.parseInt(currentWeightBe4DotValue));
            weightEditText.setText( currentWeightValue );




        } else{
            weightTextView.setText(getString(R.string.add_day_entry_activity_weight_in_unit));
            weightDialogLabelTextView.setText(getString(R.string.weight_unit));
            weightBe4DotNumberPicker.setMinValue(75);
            weightBe4DotNumberPicker.setMaxValue(660);

            //default value
            weightBe4DotNumberPicker.setValue(Integer.parseInt(currentWeightBe4DotValue));
            weightEditText.setText( currentWeightValue );
        }



        //numberPicker.setDisplayedValues(weightFrontValues);

        //den Listener setzen
        weightBe4DotNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentWeightBe4DotValue = String.valueOf(newVal);
            }
        });

        NumberPicker weightAfterDotNumberPicker = weightDialog.findViewById(R.id.weightAfterDotNumberPicker);
        weightAfterDotNumberPicker.setMinValue(0);
        weightAfterDotNumberPicker.setMaxValue(9);

        weightAfterDotNumberPicker.setValue( Integer.parseInt(currentWeightAfterDotValue));

        weightAfterDotNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentWeightAfterDotValue = String.valueOf(newVal);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //zur sicherheit immer aufrufen und daten abspeichern!
        //db.updateDayEntry(dayEntry);
    }


    //berechen und setzen der ÜBERSICHT
    private void calculateAndSetSummaryValues(int performanceValue, int calorieDeficit) {
        //Leistungsumsatz setzen
        summaryOverviewPerformanceValueTV.setText( String.valueOf(performanceValue));

        //calorie defizit
        if(calorieDeficit < 0){
            calorieDeficit = calorieDeficit * -1;
        }

        if(calorieDeficit == 0){
            summaryOverviewDailyDeficitValueTV.setText(String.valueOf( calorieDeficit ) );
        }else{
            summaryOverviewDailyDeficitValueTV.setText("-" + calorieDeficit );
        }


        //zwischensumme
        int subtotal = performanceValue - calorieDeficit;
        summaryOverviewSubtotalValueTV.setText(String.valueOf(subtotal));


        //calories ESSEN
        summaryOverviewAteTextTV.setText(Html.fromHtml("&#8721") +" " + getString(R.string.add_day_entry_activity_eating));
        int sumCalories = calcSumCalories();
        if(sumCalories == 0){
            summaryOverviewAteValueTV.setText( String.valueOf(sumCalories ));
        }else{
            summaryOverviewAteValueTV.setText("-" + sumCalories);
        }

        //sumAte in dayEntry aktualisieren
        dayEntry.setSumAte( sumCalories);



        //calories SPORT
        summaryOverviewSportTextTV.setText(Html.fromHtml("&#8721") +" "+ getString(R.string.add_day_entry_activity_burnt_by_sport));
        int sumSportCalories = calcSumSportCalories();
        if (sumSportCalories == 0){
            summaryOverviewSportValueTV.setText( "0");
        }else if(sumSportCalories > 0){
            summaryOverviewSportValueTV.setText( "+" + sumSportCalories );
        }else {
            summaryOverviewSportValueTV.setText( "-" + sumSportCalories );
        }

        //sumSport in dayEntry aktualisieren
        dayEntry.setSumSport(sumSportCalories);


        //calories NOCH OFFEN
        int openCalories = subtotal - sumCalories + sumSportCalories;
        boolean setBackGroundRed = openCalories < 0;
        summaryOverviewOpenCaloriesValueTV.setText( String.valueOf(openCalories ) );
        if(setBackGroundRed){
            summaryOverviewOpenCaloriesLinearLayout.setBackgroundColor(Color.RED);
        }else{
            summaryOverviewOpenCaloriesLinearLayout.setBackgroundColor(Color.argb(100,255,141,0)); //orange
        }

        //werte in DB übernehmen
        db.updateDayEntry(dayEntry);
    }


    private void setUpCalenderDateListener() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //String date = calendarEditText.getText().toString();

                //man wählt ein datum, danach wird das EDITTEXT mit dem datum aktualisiert!
                updateCalendarEditTextWithLocalPattern(calendar.getTime());



                    //erst wenn alle Felder gesetzt sind:
                    boolean hasWorked = tryToLoadDayEntryFromDB();


                    if( hasWorked == false){ //neuen eintrag erstellen und VIEWS udpaten!
                        //neuen DATENSATZ anlegen!
                        DayEntry newDayEntry = new DayEntry();
                        newDayEntry.setDate( calendarEditText.getText().toString() );
                        newDayEntry.setWeight( dayEntry.getWeight() );
                        newDayEntry.setDayDeficit( dayEntry.getDayDeficit() );
                        //newDayEntry.setDayImage(); //gibt noch keins
                        //aber vorhandene löschen/zurücksetzen!
                        imageBitmap = null;
                        addEntryCamImageView.setVisibility(View.VISIBLE);
                        addEntryPictureImageView.setImageResource(R.drawable.img_placeholder);
                        //caloriesOverviewSumCaloriesTextTV.setVisibility(View.INVISIBLE);
                        //caloriesOverviewSumCaloriesValueTV.setVisibility(View.INVISIBLE);

                        newDayEntry.setPerformanceValue( dayEntry.getPerformanceValue() );
                        //newDayEntry.setSumAte(); //null
                        //newDayEntry.setEntryItems(); //gibs noch nicht


                        //überschreiben
                        dayEntry = newDayEntry;
                        int dayEntryId = db.addDayEntry(dayEntry);
                        dayEntry.setId(dayEntryId);

                        //alle VIEWS aktualisieren!
                        hasWorked = tryToLoadDayEntryFromDB();
                        if(hasWorked){
                            Toast.makeText(AddDayEntryActivity.this,getString(R.string.add_day_entry_activity_a_new_data_record_was_created) + ". (" + calendarEditText.getText().toString() +")" ,Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddDayEntryActivity.this, R.string.add_day_entry_activity_oops ,Toast.LENGTH_SHORT).show();
                        }

                    }

            }
        };
    }

    private boolean tryToLoadDayEntryFromDB() {
        boolean hasWorked = false;

        try {
            //erst aktuelles datum aus db versuchen rasuzuholen

            dayEntry = db.getDayEntryByDate(calendarEditText.getText().toString());
            //alle anzeige werte aktualisieren!
            //calendarEditText.setText( dayEntry.getDate() ); //ist eigtl quasi da auch schon vorher gesetzt!
            weightEditText.setText(String.valueOf(dayEntry.getWeight()));

            //setze defizit
            //Log.d("test", "dayEntry deficit: " + dayEntry.getDayDeficit());
            addEntrySpendingKcalValueET.setText(String.valueOf(dayEntry.getDayDeficit()));


            //falls image vorhanden, setzen
            if (dayEntry.getDayImage() != null) {
                imageBitmap = ImageConverter.byteArrayToBitmap(dayEntry.getDayImage());
                addEntryPictureImageView.setImageBitmap(imageBitmap);
                addEntryCamImageView.setVisibility(View.INVISIBLE);
            }else { //falls keins da, REST zurücksetezn!
                imageBitmap = null;
                addEntryPictureImageView.setImageResource(R.drawable.img_placeholder);
                addEntryCamImageView.setVisibility(View.VISIBLE);
            }




            hasWorked = true;

            //daten aus DB lesen und setzen!
            //get All EntryItems
            ArrayList<EntryItem> entryItemsAL = db.getAllEntryItemsByDayEntryId(dayEntry.getId());
            dayEntry.setEntryItems(entryItemsAL);

            //get All SportEntryItems
            ArrayList<SportEntryItem> sportEntryItemsAL = db.getAllSportEntryItemsByDayEntryId(dayEntry.getId());
            dayEntry.setSportEntryItems(sportEntryItemsAL);



            calculateAndSetSummaryValues(dayEntry.getPerformanceValue(), dayEntry.getDayDeficit());

            entryItemRecyclerAdapter = new EntryItemRecyclerAdapter(AddDayEntryActivity.this, dayEntry.getEntryItems());
            entryItemRecyclerAdapter.setClickListener(AddDayEntryActivity.this);
            caloriesOverviewRecyclerView.setAdapter(entryItemRecyclerAdapter);

            sportEntryItemRecyclerAdapter = new SportEntryItemRecyclerAdapter(AddDayEntryActivity.this, dayEntry.getSportEntryItems());
            sportEntryItemRecyclerAdapter.setClickListener(AddDayEntryActivity.this);
            sportCaloriesOverviewRecyclerView.setAdapter(sportEntryItemRecyclerAdapter);

            //update
            int calcSumCalories = calcSumCalories();
            caloriesOverviewSumCaloriesValueTV.setText(String.valueOf(calcSumCalories));

            int calcSumSportCalories = calcSumSportCalories();
            sportCaloriesOverviewSumCaloriesValueTV.setText(String.valueOf(calcSumSportCalories));

        } catch (Exception e) {
            e.printStackTrace();
            //es gibt KEIN entryDay an dem TAG und du kannst dafür auch KEINEN ERSTELLEN?
            // Toast.makeText(AddDayEntryActivity.this,"Zu dem Datum " + calendarEditText.getText().toString() + " sind keine Daten vorhanden." ,Toast.LENGTH_SHORT).show();
            // calendarEditText.setText( date ); //old date
            hasWorked = false;
        }

        return hasWorked;
    }



    private void updateCalendarEditTextWithLocalPattern(Date date) {

        String pattern;
        if(locale.toString().equals(Locale.GERMANY.toString())){
            pattern = "dd.MM.YYYY";

        //}else  if(locale.equals("us_US")){
            //pattern = "MM/dd/yy";
        }else{

            pattern = "MM/dd/yy";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
        calendarEditText.setText(simpleDateFormat.format( date ));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addEntryDateEditText:
                //new DatePickerDialog(AddDayEntryActivity.this,dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddDayEntryActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                break;
            case R.id.addEntryWeightEditText:
                weightDialog.show();
                break;
            case R.id.addEntryCamImageView: //walktrough
            case R.id.addEntryPictureImageView:
                //ask permission and grab photo
                if(checkCameraPermission() == true){
                    takeAPhotoFromCamera();
                }else {
                    //um erlaubnis fragen
                    ActivityCompat.requestPermissions(AddDayEntryActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
                }
                break;
            case R.id.addEntryFAB:
               showAddNewEntryDialog();
                break;
            case R.id.addSportEntryFAB:
                final Dialog sportEntryDialog = new Dialog(AddDayEntryActivity.this);
                sportEntryDialog.setContentView(R.layout.sportentry_dialog);
                sportEntryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button addSportEntryButton = sportEntryDialog.findViewById(R.id.addNewSportEntryDialogButton);
                final AutoCompleteTextView sportEntryDialogLabelACTV = sportEntryDialog.findViewById(R.id.sportEntryDialogLabelACTV);
                final EditText sportEntryDialogKcalEditText = sportEntryDialog.findViewById(R.id.sportEntryDialogKcalEditText);
                addSportEntryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Prüfen ob leer
                        if (!TextUtils.isEmpty(sportEntryDialogLabelACTV.getText().toString()) &&
                                !TextUtils.isEmpty(sportEntryDialogKcalEditText.getText().toString()) ){

                            int calories = Integer.parseInt(sportEntryDialogKcalEditText.getText().toString() );
                            String labelEntry = sportEntryDialogLabelACTV.getText().toString().trim();
                            SportEntryItem sportEntryItem = new SportEntryItem();
                            sportEntryItem.setLabel(labelEntry);
                            sportEntryItem.setCalories(calories);
                            //FK setzen
                            sportEntryItem.setDayEntryId( dayEntry.getId() );

                            //in db speichern und dann ID noch setzen ! be4 es auf die AL kann!
                            int id = db.addSportEntryItem(sportEntryItem);
                            //Log.d("AddDayEntryActivity", "meine id: " + id);
                            sportEntryItem.setId(id);

                            try{
                                boolean isVisible = sportEntryItemRecyclerAdapter.getItem(sportEntryItemRecyclerAdapter.getItemCount() -1).isDeleteButtonVisible();
                                sportEntryItem.setDeleteButtonVisible(isVisible);
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                            dayEntry.getSportEntryItems().add(sportEntryItem);


                            //neu berechnen
                            calculateAndSetSummaryValues(Integer.parseInt(summaryOverviewPerformanceValueTV.getText().toString()), Integer.parseInt(summaryOverviewDailyDeficitValueTV.getText().toString()));
                            //SumCalories update
                            caloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumCalories() ));
                            //SumSportCalories update
                            sportCaloriesOverviewSumCaloriesValueTV.setText(String.valueOf( calcSumSportCalories() ));

                            //notifyadapter
                            sportEntryItemRecyclerAdapter.notifyDataSetChanged();
                            sportEntryDialog.dismiss();
                        }else{
                            Toast.makeText(AddDayEntryActivity.this, R.string.add_day_entry_activity_please_fill_in_all_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

                Button sportCancelButton = sportEntryDialog.findViewById(R.id.cancelNewSportEntryDialogButton);
                sportCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sportEntryDialog.dismiss();
                    }
                });
                sportEntryDialog.show();
                break;
            case R.id.caloriesOverviewEditIV:
                //delete buttons einblenden

                //Toast.makeText(this, "edit gedrückt", Toast.LENGTH_SHORT).show();
                //EntryItem entryItem = entryItemsAL.get(entryItemsAL.size() -1);
                //entryItemsAL.remove(entryItem);
                //entryItemsAL.add(entryItem);

                try{
                    boolean isVisible = entryItemRecyclerAdapter.getItem(entryItemRecyclerAdapter.getItemCount() -1).isDeleteButtonVisible(); //vom letzen elemente den isVisible wert holen
                    isVisible = !isVisible;
                    entryItemRecyclerAdapter.setDeleteButtonsVisible(isVisible);
                    entryItemRecyclerAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }
                break;
            case R.id.sportOverviewEditIV:
                try{
                    boolean isVisible = sportEntryItemRecyclerAdapter.getItem(sportEntryItemRecyclerAdapter.getItemCount() -1).isDeleteButtonVisible(); //vom letzen elemente den isVisible wert holen
                    isVisible = !isVisible;
                    sportEntryItemRecyclerAdapter.setDeleteButtonsVisible(isVisible);
                    sportEntryItemRecyclerAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }
                break;

        }
    }

    private void showAddNewEntryDialog() {
        final Dialog entryDialog = new Dialog(AddDayEntryActivity.this);
        entryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        entryDialog.setContentView(R.layout.entry_dialog);
        Button addButton = entryDialog.findViewById(R.id.addNewEntryDialogButton);
        final AutoCompleteTextView entryDialogLabelACTV = entryDialog.findViewById(R.id.entryDialogLabelACTV);
        final EditText entryDialogQuantityEditText = entryDialog.findViewById(R.id.entryDialogQuantityEditText);
        final EditText entryDialogKcalEditText = entryDialog.findViewById(R.id.entryDialogKcalEditText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prüfen ob leer
                if (!TextUtils.isEmpty(entryDialogLabelACTV.getText().toString()) &&
                        !TextUtils.isEmpty(entryDialogQuantityEditText.getText().toString()) &&
                        !TextUtils.isEmpty(entryDialogKcalEditText.getText().toString()) ){


                    int calories = Integer.parseInt(entryDialogQuantityEditText.getText().toString() ) * Integer.parseInt(entryDialogKcalEditText.getText().toString() );
                    String labelEntry = entryDialogLabelACTV.getText().toString().trim() + " (" + entryDialogQuantityEditText.getText().toString() + ")";
                    EntryItem entryItem = new EntryItem();
                    entryItem.setLabel(labelEntry);
                    entryItem.setCalories(calories);
                    //FK setzen
                    entryItem.setDayEntryId_FK( dayEntry.getId() );

                    //in db speichert und dann ID noch setzen ! be4 es auf die AL kann!
                    int id = db.addEntryItem(entryItem);
                    //Log.d("AddDayEntryActivity", "meine id: " + id);
                    entryItem.setId(id);

                    try{
                        boolean isVisible = entryItemRecyclerAdapter.getItem(entryItemRecyclerAdapter.getItemCount() -1).isDeleteButtonVisible();
                        entryItem.setDeleteButtonVisible(isVisible);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    dayEntry.getEntryItems().add(entryItem);


                    //neu berechnen
                    calculateAndSetSummaryValues(Integer.parseInt(summaryOverviewPerformanceValueTV.getText().toString()), Integer.parseInt(summaryOverviewDailyDeficitValueTV.getText().toString()));
                    //SumCalories update
                    caloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumCalories() ));
                    //caloriesOverviewSumCaloriesValueTV.setVisibility(View.VISIBLE);
                    //caloriesOverviewSumCaloriesTextTV.setVisibility(View.VISIBLE);

                    //notifyadapter
                    entryItemRecyclerAdapter.notifyDataSetChanged();
                    entryDialog.dismiss();
                }else{
                    Toast.makeText(AddDayEntryActivity.this,getString(R.string.add_day_entry_activity_please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        Button cancelButton = entryDialog.findViewById(R.id.cancelNewEntryDialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDialog.dismiss();
            }
        });

        FloatingActionButton addNewEntryFlipFAB = entryDialog.findViewById(R.id.addNewEntryFlipFAB);
        addNewEntryFlipFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDialog.dismiss();
                changeDialog();
            }
        });

        entryDialog.show();
    }

    private void changeDialog() {
        final Dialog entryGramDialog = new Dialog(AddDayEntryActivity.this);
        entryGramDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        entryGramDialog.setContentView(R.layout.entry_gram_dialog);
        Button addButton = entryGramDialog.findViewById(R.id.addNewEntryGramDialogButton);
        final AutoCompleteTextView entryDialogGramLabelACTV = entryGramDialog.findViewById(R.id.entryDialogGramLabelACTV);
        final TextView entryGramDialogCorrespondTV = entryGramDialog.findViewById(R.id.entryGramDialogCorrespondTV);

        final EditText entryGramDialog100gKcalET = entryGramDialog.findViewById(R.id.entryGramDialog100gKcalET);
        final EditText entryGramDialogXGramET = entryGramDialog.findViewById(R.id.entryGramDialogXGramET);

        entryGramDialog100gKcalET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    int calories = Math.round ( (Integer.parseInt( entryGramDialog100gKcalET.getText().toString() ) / 100.0f) * Integer.parseInt(entryGramDialogXGramET.getText().toString() ) );
                    entryGramDialogCorrespondTV.setText( String.valueOf( calories));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        entryGramDialogXGramET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    int calories = Math.round ( (Integer.parseInt( entryGramDialog100gKcalET.getText().toString() ) / 100.0f) * Integer.parseInt(entryGramDialogXGramET.getText().toString() ) );
                    entryGramDialogCorrespondTV.setText( String.valueOf( calories));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });





        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prüfen ob leer
                if (!TextUtils.isEmpty(entryDialogGramLabelACTV.getText().toString()) &&
                        !TextUtils.isEmpty(entryGramDialog100gKcalET.getText().toString()) &&
                        !TextUtils.isEmpty(entryGramDialogXGramET.getText().toString()) ){

                    // 100g - 255
                    //22 - 27 // 255:100*22
                    int calories = Math.round ( (Integer.parseInt( entryGramDialog100gKcalET.getText().toString() ) / 100.0f) * Integer.parseInt(entryGramDialogXGramET.getText().toString() ) );

                    String labelEntry = entryDialogGramLabelACTV.getText().toString().trim() + " (" + entryGramDialogXGramET.getText().toString() + "g)";
                    EntryItem entryItem = new EntryItem();
                    entryItem.setLabel(labelEntry);
                    entryItem.setCalories(calories);
                    //FK setzen
                    entryItem.setDayEntryId_FK( dayEntry.getId() );

                    //in db speichert und dann ID noch setzen ! be4 es auf die AL kann!
                    int id = db.addEntryItem(entryItem);
                    //Log.d("AddDayEntryActivity", "meine id: " + id);
                    entryItem.setId(id);

                    try{
                        boolean isVisible = entryItemRecyclerAdapter.getItem(entryItemRecyclerAdapter.getItemCount() -1).isDeleteButtonVisible();
                        entryItem.setDeleteButtonVisible(isVisible);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    dayEntry.getEntryItems().add(entryItem);


                    //neu berechnen
                    calculateAndSetSummaryValues(Integer.parseInt(summaryOverviewPerformanceValueTV.getText().toString()), Integer.parseInt(summaryOverviewDailyDeficitValueTV.getText().toString()));
                    //SumCalories update
                    caloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumCalories() ));
                    //caloriesOverviewSumCaloriesValueTV.setVisibility(View.VISIBLE);
                    //caloriesOverviewSumCaloriesTextTV.setVisibility(View.VISIBLE);

                    //notifyadapter
                    entryItemRecyclerAdapter.notifyDataSetChanged();
                    entryGramDialog.dismiss();
                }else{
                    Toast.makeText(AddDayEntryActivity.this,getString(R.string.add_day_entry_activity_please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        Button cancelButton = entryGramDialog.findViewById(R.id.cancelNewEntryGramDialogButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryGramDialog.dismiss();
            }
        });

        FloatingActionButton addNewEntryGramFlipFAB = entryGramDialog.findViewById(R.id.addNewEntryGramFlipFAB);
        addNewEntryGramFlipFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryGramDialog.dismiss();
                showAddNewEntryDialog();
            }
        });

        entryGramDialog.show();

    }


    private boolean checkCameraPermission() {
        if( ContextCompat.checkSelfPermission(AddDayEntryActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ){
            //haben die erlaubnis dafür
            return true;
        }else{
            return false;
        }
    }

    public void takeAPhotoFromCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(AddDayEntryActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            addEntryPictureImageView.setImageBitmap(imageBitmap);
            addEntryCamImageView.setVisibility(View.INVISIBLE); //camera symbol ausblenden

            //in db speichern
            dayEntry.setDayImage( ImageConverter.bitmapToByteArray(imageBitmap));
            db.updateDayEntry(dayEntry);
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        //Log.d("test", entryItemReceyclerAdapter.getItem(position).getLabel() );
        //delete item from db and update AL
        EntryItem entryItem = entryItemRecyclerAdapter.getItem(position);


        int val =   db.deleteEntryItem(entryItem.getId());
        if(val == -1){
            //toast , hat nicht geklappt
        }else{
            //toast , erfolgreich gelöscht
        }

        //aus AL entfernen
        dayEntry.getEntryItems().remove(entryItem);

        //SumCalories update
        caloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumCalories() ));

        //neu berechnen
        calculateAndSetSummaryValues(Integer.parseInt(summaryOverviewPerformanceValueTV.getText().toString()), Integer.parseInt(summaryOverviewDailyDeficitValueTV.getText().toString()));

       
        //UI update
        entryItemRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onSportEntryItemClick(View view, int position) {
        SportEntryItem sportEntryItem = sportEntryItemRecyclerAdapter.getItem(position);

        int val = db.deleteSportEntryItem( sportEntryItem.getId() );
        if(val == -1){
            //toast , hat nicht geklappt
        }else{
            //toast , erfolgreich gelöscht
        }

        //aus AL entfernen
        dayEntry.getSportEntryItems().remove(sportEntryItem);

        //SumSportCalories update
        sportCaloriesOverviewSumCaloriesValueTV.setText( String.valueOf( calcSumSportCalories() ));

        //neu berechnen
        calculateAndSetSummaryValues(Integer.parseInt(summaryOverviewPerformanceValueTV.getText().toString()), Integer.parseInt(summaryOverviewDailyDeficitValueTV.getText().toString()));


        //UI update
        sportEntryItemRecyclerAdapter.notifyDataSetChanged();

    }


    public int calcSumCalories(){
        //int size = entryItemReceyclerAdapter.getItemCount();
        int sum = 0;
        if (dayEntry.getEntryItems() != null){
            for(int i= 0; i< dayEntry.getEntryItems().size(); i++){
                sum += dayEntry.getEntryItems().get(i).getCalories();
            }
        }

        return sum;
    }

    public int calcSumSportCalories(){
        int sum = 0;
        if (dayEntry.getSportEntryItems() != null){
            for(int i= 0; i< dayEntry.getSportEntryItems().size(); i++){
                sum += dayEntry.getSportEntryItems().get(i).getCalories();
            }
        }

        return sum;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //sorgt dafür das danach OverView neu erzeugt und die Daten somit NEU geladen werden!
        startActivity(new Intent(AddDayEntryActivity.this, OverviewActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}