package schaefer.anatoli.caloriedeficit.ui.stats;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

import schaefer.anatoli.caloriedeficit.AddDayEntryActivity;
import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.DayEntry;
import schaefer.anatoli.caloriedeficit.model.PersonalData;
import schaefer.anatoli.caloriedeficit.util.ImageConverter;
import schaefer.anatoli.caloriedeficit.util.PersonalDataConfiguration;


public class BeforeAfterCompareFragment extends Fragment {


    private PageViewModel pageViewModel;

    private TextView compareDateTopTV;
    private TextView compareDateBottomTV;
    private ImageView compareTopIV;
    private ImageView compareBottomIV;

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListenerTop;
    private DatePickerDialog.OnDateSetListener dateSetListenerBottom;

    public BeforeAfterCompareFragment() {
        // Required empty public constructor
    }


    public static BeforeAfterCompareFragment newInstance() {
        BeforeAfterCompareFragment fragment = new BeforeAfterCompareFragment();
        // Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        //  args.putString(ARG_PARAM2, param2);
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 3;
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_before_after_compare, container, false);

        calendar = Calendar.getInstance();
        final DatabaseHelper db = DatabaseHelper.getInstance(view.getContext());

        Locale locale = getResources().getConfiguration().locale;
        String pattern;
        if(locale.toString().equals(Locale.GERMANY.toString())){
            pattern = "dd.MM.YYYY";
        }else {
            pattern = "MM/dd/yy";
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);

        compareDateTopTV = view.findViewById(R.id.compareDateTopTV);
        compareDateBottomTV = view.findViewById(R.id.compareDateBottomTV);


        compareTopIV =  view.findViewById(R.id.compareTopIV);
        compareTopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext() ,android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListenerTop, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        compareBottomIV =  view.findViewById(R.id.compareBottomIV);
        compareBottomIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext() ,android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListenerBottom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });


        dateSetListenerTop = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                try {
                    DayEntry dayEntry =  db.getDayEntryByDate( simpleDateFormat.format(calendar.getTime() ) );

                    Bitmap topImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.img_placeholder);
                    if (dayEntry.getDayImage() != null){
                        topImage = ImageConverter.byteArrayToBitmap(dayEntry.getDayImage());
                    }else{
                        Toast.makeText(getContext(),getString(R.string.before_after_compare_fragment_there_was_no_image_for) + " " + dayEntry.getDate() + " " + getString(R.string.before_after_compare_fragment_found), Toast.LENGTH_SHORT).show();
                    }

                    String topWeight = "";
                    if (dayEntry.getWeight() > 0){
                        topWeight = dayEntry.getWeight() + " " + getResources().getString(R.string.weight_unit) + " @ ";
                    }

                    setTopDateAndImage( dayEntry.getDate(), topImage, topWeight);
                }catch (Exception e){
                    Toast.makeText(getContext(), R.string.before_after_compare_fragment_no_data_available_for_the_day, Toast.LENGTH_SHORT).show();
                }

            }};

        dateSetListenerBottom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                try {
                    DayEntry dayEntry = db.getDayEntryByDate(simpleDateFormat.format(calendar.getTime()));
                    Bitmap bottomImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.img_placeholder);
                    if (dayEntry.getDayImage() != null) {
                        bottomImage = ImageConverter.byteArrayToBitmap(dayEntry.getDayImage());
                    } else {
                        Toast.makeText(getContext(), getString(R.string.before_after_compare_fragment_there_was_no_image_for)+ " " + dayEntry.getDate() + " " + getString(R.string.before_after_compare_fragment_found), Toast.LENGTH_SHORT).show();
                    }

                    String bottomWeight = "";
                    if (dayEntry.getWeight() > 0){
                        bottomWeight = dayEntry.getWeight() + " " + getResources().getString(R.string.weight_unit) + " @ ";
                    }

                    setBottomDateAndImage(dayEntry.getDate(), bottomImage, bottomWeight);
                }catch (Exception e){
                    Toast.makeText(getContext(), R.string.before_after_compare_fragment_no_data_available_for_the_day, Toast.LENGTH_SHORT).show();
                }
            }};




        PersonalData personalData = db.getPersonalData(PersonalDataConfiguration.PERSONALDATA_ID);



        Bitmap topImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.img_placeholder);
        if (personalData.getFirstImage() != null){
            topImage = ImageConverter.byteArrayToBitmap(personalData.getFirstImage());
        }
        String topWeight = "";
        if (personalData.getWeight() > 0){
            topWeight = personalData.getWeight() + " " + getResources().getString(R.string.weight_unit) + " @ ";
        }
        setTopDateAndImage( personalData.getDate(), topImage, topWeight);


        try {
            //letzte bild nehmen
            DayEntry dayEntry = db.getDayEntryByDate( simpleDateFormat.format(new Date() ) );

            Bitmap bottomImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.img_placeholder);
            if(dayEntry.getDayImage() != null){
                bottomImage = ImageConverter.byteArrayToBitmap(dayEntry.getDayImage());
            }
            String bottomWeight = "";
            if (dayEntry.getWeight() > 0){
                bottomWeight = dayEntry.getWeight() + " " + getResources().getString(R.string.weight_unit) + " @ ";
            }
            setBottomDateAndImage(dayEntry.getDate() , bottomImage, bottomWeight);
        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }

    private void setBottomDateAndImage(String date, Bitmap image, String weight)  {
        compareDateBottomTV.setText(weight + date);
        compareBottomIV.setImageBitmap(image);
    }

    private void setTopDateAndImage(String date, Bitmap image, String weight) {
        compareDateTopTV.setText(weight + date);
        compareTopIV.setImageBitmap(image);
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.stats_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.testId:
                //do something
                return true;

            default:  return super.onOptionsItemSelected(item);
        }

    }

 */
}