package schaefer.anatoli.caloriedeficit.ui.stats;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.DayEntry;

public class StatsCaloriesEatenFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   // private static final String ARG_PARAM1 = "param1";
   // private static final String ARG_PARAM2 = "param2";
    private PageViewModel pageViewModel;

    private BarChart chart;

    // private String mParam1;
   // private String mParam2;

    public StatsCaloriesEatenFragment() {
        // Required empty public constructor
    }


    public static StatsCaloriesEatenFragment newInstance() {
        StatsCaloriesEatenFragment fragment = new StatsCaloriesEatenFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
          //  mParam1 = getArguments().getString(ARG_PARAM1);
          //  mParam2 = getArguments().getString(ARG_PARAM2);

        }
        pageViewModel.setIndex(index);
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_stats_calories_eaten, container, false);

       chart = view.findViewById(R.id.barChartCaloriesEaten);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        ArrayList<DayEntry> dayEntries = db.getAllDayEntriesSortedByDate();



        dayEntries.sort(new Comparator<DayEntry>() {
            @Override
            public int compare(DayEntry o1, DayEntry o2) {
                final Locale locale = getResources().getConfiguration().locale;
                final String pattern;
                if(locale.toString().equals(Locale.GERMANY.toString())){
                    pattern = "dd.MM.yyyy";
                }else{
                    pattern = "MM/dd";
                }
                final SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
                Date date = null;
                Date date2 = null;
                try {
                    date = sdf.parse(o1.getDate());
                    date2 = sdf.parse(o2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                    //Log.d("test2", "WHAT");
                }

                long millis1 = date.getTime();
                long millis2 = date2.getTime();

                if (millis1 > millis2){
                    return 1;
                }else{
                    return -1;
                }

            }
        });


        if (dayEntries.size() >= 14){
            ArrayList<DayEntry> newDayEntries = new ArrayList<>();
            int offset = dayEntries.size() - 14 + 1; //30 - 14 = 16      // 15 - 14 = 1     //14 - 14 = 0

            for( int i = offset; i < dayEntries.size(); i++){ // von 16... 30
                newDayEntries.add(dayEntries.get(i));
                //Log.i("test3", "nimmt auf: " + dayEntries.get(i).getDate());
            }

            dayEntries = newDayEntries;
        }


        try {
            setUpChart(dayEntries);
        }catch (Exception e){
            e.printStackTrace();

        }



        return view;
    }

    private void setUpChart(final ArrayList<DayEntry> dayEntries) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();



        int count = 0;

        for(DayEntry de: dayEntries){
            barEntries.add(new BarEntry(count, (float) de.getSumAte()));
            count++;
        }

        BarDataSet dataSet = new BarDataSet(barEntries, getString(R.string.stats_calories_eaten_fragment_kcal_eaten));
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.RED);
        dataSet.setValueTextSize(18.0f);


        BarData barData = new BarData(dataSet);
        barData.setValueTextColor(Color.RED);
        barData.setDrawValues(true);




        chart.setData(barData);
        chart.getDescription().setEnabled(false);
        //chart.setBackgroundColor(Color.YELLOW);

        chart.setDrawValueAboveBar(true);
        //chart.setFitBars(true);



        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelRotationAngle(300);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setGranularity(1.0f);
        //xAxis.setDrawGridLines(false);
        final Locale locale = getResources().getConfiguration().locale;
        final String pattern;
        if(locale.toString().equals(Locale.GERMANY.toString())){
            pattern = "dd.MM";
        }else{
            pattern = "MM/dd";
        }

        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat(pattern, locale);

            @Override
            public String getFormattedValue(float value) {

                String myDate = dayEntries.get((int) value).getDate();  //24.07.2020

                String date = new Date(0).toString(); //default
                try {
                    date = mFormat.format( mFormat.parse(myDate) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return date;
            }
        });

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0.0f);

        //chart.invalidate();
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

