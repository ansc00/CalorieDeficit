package schaefer.anatoli.caloriedeficit.ui.stats;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.data.DatabaseHelper;
import schaefer.anatoli.caloriedeficit.model.DayEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class StatsWeightFragment extends Fragment {

    //private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private LineChart chart;

    public static StatsWeightFragment newInstance() {
        StatsWeightFragment fragment = new StatsWeightFragment();
       // Bundle bundle = new Bundle();
        //bundle.putInt(ARG_SECTION_NUMBER, index);
        //fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 0;
        if (getArguments() != null) {
           // index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats_weight, container, false);

        chart = root.findViewById(R.id.lineChartWeight);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());

        //daten aus db holen
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


       // Log.i("test3", ""+dayEntries.size());


        //max. 14 tage einblenden //sonst sieht man NIX ?!
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



        return root;
    }

    private void setUpChart(ArrayList<DayEntry> dayEntries) {
        List<Entry> entries = new ArrayList<>();
        final HashMap<Integer,String> dateToWeightMap = new HashMap<>();


        final Locale locale = getResources().getConfiguration().locale;
        final String pattern;

        if(locale.toString().equals(Locale.GERMANY.toString())){
            pattern = "dd.MM";

        }else {
            pattern = "MM/dd";

        }




        final SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        int count = 0;
        for(DayEntry de: dayEntries){

            String myDate =  de.getDate(); //24.07.2020

            Date date = null;
            //long millis = 0;
            String dateString = "";
            float msFloat = 0.0f;
            try {
                date = sdf.parse(myDate);
                dateString = sdf.format(date.getTime());

               // millis = date.getTime();
               // millis = millis + (1000 * 60 *60 *12); //zeitpunkt verschieben von 00:00 auf 12 std später

                //Log.i("test3",     "date: "+ date + " datestring: " + dateString + ", msFlaot: " + msFloat + ", count: " + count);
                dateToWeightMap.put(count, dateString);
                //Log.d("test3","" + myDate  + ", floatwert: "+ msFloat + " date: " + sdf.format(millis)); //27.07.2020, floatwert: 1.59580081E12
                entries.add(new Entry((float) count, de.getWeight()));
                count++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //float msFloat = (float) millis;





        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.stats_calories_eaten_fragment_weight_in_unit));
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLUE);
        dataSet.setValueTextSize(14.0f);
        dataSet.setValueFormatter(new ValueFormatter() {

            private DecimalFormat mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(value);
            }
        });




        LineData lineData = new LineData(dataSet);




        XAxis xAxis = chart.getXAxis();
         xAxis.setValueFormatter(new ValueFormatter() {



            @Override
            public String getFormattedValue(float value) {

                int idx = (int) value ;
               // long millis =  (long) value ;
               // Date d = new Date(millis);
                // Log.i("test3","weiter unten: " + value + ", dazugehörige int: " + idx);
               // Log.i("test3","weiter unten: " +   value);
                return dateToWeightMap.get(idx) + "";
            }
        });






         //xAxis.setGranularity(24.0f);
         //xAxis.setDrawGridLines(true);
         //xAxis.setAxisMinimum(entries.get(0).getX());
         //xAxis.setAxisMaximum(entries.get(entries.size()-1).getX());
        xAxis.setLabelRotationAngle(300);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        //chart.setBackgroundColor(Color.YELLOW);

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

    }*/

}