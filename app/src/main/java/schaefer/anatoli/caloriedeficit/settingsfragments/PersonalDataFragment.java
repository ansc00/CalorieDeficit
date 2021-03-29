package schaefer.anatoli.caloriedeficit.settingsfragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Locale;

import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.SettingsActivity;
import schaefer.anatoli.caloriedeficit.model.PersonalData;


public class PersonalDataFragment extends Fragment {

    private Button nextButton;
    private RadioButton wRadioButton;
    private RadioButton mRadioButton;
    private EditText bodySizeEditText;
    private EditText weightEditText;
    private EditText ageEditText;


    private static final String ARG_GENDER = "gender";
    private static final String ARG_WEIGHT = "weight";
    private static final String ARG_BODYSIZE = "bodysize";
    private static final String ARG_AGE = "age";

    // TODO: Rename and change types of parameters
    private String gender;
    private int weight;
    private int bodysize;
    private int age;

    private Locale locale;
    private LinearLayout bodySizeLL;
    private EditText bodySizeFeetET;
    private EditText bodySizeInchesET ;

    public PersonalDataFragment() {
        // Required empty public constructor
    }

    /*
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gender Parameter 1.
     * @param bodySize Parameter 2.
     * @param weight Parameter 2.
     * @return A new instance of fragment PersonalDataFragment.

    // TODO: Rename and change types and number of parameters
    public static PersonalDataFragment newInstance(String gender, String bodySize, String weight, String age) {
        PersonalDataFragment fragment = new PersonalDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENDER, gender);
        args.putString(ARG_BODYSIZE, bodySize);
        args.putString(ARG_WEIGHT, weight);
        args.putString(ARG_AGE, age);

        fragment.setArguments(args);
        return fragment;
    }
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gender = getArguments().getString(ARG_GENDER);
            bodysize = getArguments().getInt(ARG_BODYSIZE);
            weight = getArguments().getInt(ARG_WEIGHT);
            age = getArguments().getInt(ARG_AGE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View v = inflater.inflate(R.layout.fragment_personal_data, container, false);

       //local setzen
        locale = getResources().getConfiguration().locale;

        bodySizeEditText = v.findViewById(R.id.bodySizeEditText); //german
        bodySizeLL = v.findViewById(R.id.bodySizeLL);
        bodySizeFeetET = v.findViewById(R.id.bodySizeFeetET);//default
        bodySizeInchesET = v.findViewById(R.id.bodySizeInchesET);//default


        //seitenaufruf privacy police
        TextView privacyPoliceTV = v.findViewById(R.id.privacyPoliceTV);
        privacyPoliceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                if(locale.toString().equals(Locale.GERMANY.toString())){
                    uri = Uri.parse("https://calorie-deficit.jimdosite.com");
                }else {
                    uri = Uri.parse("https://calorie-deficit.jimdosite.com");
                }
                startActivity(new Intent(Intent.ACTION_VIEW, uri ));
            }
        });


        if(locale.toString().equals(Locale.GERMANY.toString())){
            bodySizeEditText.setVisibility(View.VISIBLE); //GERMAN ON
            bodySizeLL.setVisibility(View.GONE);

            //daten in felder setzen, falls sie in DB ex.
            if(bodysize > 0){
                bodySizeEditText.setText(String.valueOf(bodysize));
            }

        }else{
            bodySizeEditText.setVisibility(View.GONE);
            bodySizeLL.setVisibility(View.VISIBLE); //Default ON

            //daten in felder setzen, falls sie in DB ex.
            if(bodysize > 0){ //4feet 0 inch .... 6feet 11 iches zb. 411 möglich = 4feet 11inches //ingesamt: 2 oder 3 stellige zahl !
                String val = String.valueOf(bodysize);

                bodySizeFeetET.setText( val.substring(0,1)); //ERSTE stelle IMMER feet
                bodySizeInchesET.setText(val.substring(1)); //REST immer inches

            }
        }

       //verknüpfen
       mRadioButton = v.findViewById(R.id.mRadioButton);
       wRadioButton = v.findViewById(R.id.wRadioButton);
       weightEditText = v.findViewById(R.id.weightEditText);
       ageEditText = v.findViewById(R.id.ageEditText);


       //Daten in felder setzen, falls sie in DB exisitieren
       if(gender != null){
           if(gender.equals("m")){
               mRadioButton.setChecked(true);
           }else{
               wRadioButton.setChecked(true);
           }
       }



       if(weight > 0){
           weightEditText.setText(String.valueOf(weight));
       }

       if(age > 0){
           ageEditText.setText(String.valueOf(age));
       }

       nextButton = v.findViewById(R.id.personalDataFNextButton);

       nextButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               boolean isValid = validateInput();

               if(isValid) {
                   ActivityDataFragment activityDataFragment = new ActivityDataFragment();
                   SettingsActivity settingsActivity = (SettingsActivity) getActivity();

                   String gender;
                   if(mRadioButton.isChecked()){
                       gender = "m";
                   }else{
                       gender = "w";
                   }


                   int bodySizeValue;
                   if(locale.toString().equals(Locale.GERMANY.toString())){
                       bodySizeValue = Integer.parseInt(bodySizeEditText.getText().toString());
                   }else{
                       bodySizeValue = Integer.parseInt(bodySizeFeetET.getText().toString() + bodySizeInchesET.getText().toString() );
                   }

                   settingsActivity.setResultFromPersonalDataFragment(gender, bodySizeValue ,
                           Integer.parseInt(weightEditText.getText().toString()), Integer.parseInt(ageEditText.getText().toString()));

                   settingsActivity.replaceFragment(activityDataFragment);
               }else {
                   ((SettingsActivity) getActivity()).showAlertDialog();
               }
           }
       });

        // Inflate the layout for this fragment
        return v;
    }

    private boolean validateInput() {
         boolean isValid = false;

        String weight = weightEditText.getText().toString(); //
        String age = ageEditText.getText().toString();



        boolean isSizeOk = false;
        if(locale.toString().equals(Locale.GERMANY.toString())){
            isSizeOk = bodySizeEditText.getText().toString().trim().length() == 3; //null , empty , 160      //gibs nicht: 3, 99, 1000
        }else {
          try {
              boolean sizeLengthOK = bodySizeFeetET.getText().toString().trim().length() == 1 && //1 stelle
                      (bodySizeInchesET.getText().toString().trim().length() == 1 || bodySizeInchesET.getText().toString().trim().length() == 2); //max. 2 stellen
              int feet = Integer.parseInt( bodySizeFeetET.getText().toString().trim() );
              int inches = Integer.parseInt( bodySizeInchesET.getText().toString().trim());
              isSizeOk = sizeLengthOK && feet >= 4 && feet <= 7 && //wete müssen zwischen 4 und 7 liegen
                      inches >= 0 && inches <= 11; //werte müssen zw. 0 und 11 liegen --> 12 inches = 1 feet
          }catch (Exception e){

              return isValid;
          }

        }








        //prüfen ob leer oder länge = 0
        if(!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(age)
        && isSizeOk && weight.length() >= 2 && weight.length() <=3 && age.length() >=1 && age.length() <= 3){ //Stellenbezogen
         isValid = true;
        }


       return isValid;

    }
}