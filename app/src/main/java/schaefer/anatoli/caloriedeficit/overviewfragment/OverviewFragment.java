package schaefer.anatoli.caloriedeficit.overviewfragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import schaefer.anatoli.caloriedeficit.R;


public class OverviewFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATE = "date";
    private static final String ARG_PERFORMANCE_VALUE = "perfValue";
    private static final String ARG_DAYDEFICIT_VALUE = "defValue";
    private static final String ARG_SUM_ATE_VALUE = "sumAteValue";
    private static final String ARG_SUM_SPORT_VALUE = "sumSportValue";

    public static final String ARG_NODATA = "NODATA";


    private String date;
    private int performanceValue;
    private int dayDeficitValue;
    private int sumAteValue;
    private int sumSportValue;
    private String noData = null;


    public OverviewFragment() {
        // Required empty public constructor
    }



/*
    public static OverviewFragment newInstance(String date, int performanceValue, int dayDeficitValue, int sumAteValue, int sumSportValue) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putInt(ARG_PERFORMANCE_VALUE, performanceValue);
        args.putInt(ARG_DAYDEFICIT_VALUE, dayDeficitValue);
        args.putInt(ARG_SUM_ATE_VALUE, sumAteValue);
        args.putInt(ARG_SUM_SPORT_VALUE, sumSportValue);
        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString(ARG_DATE);
            performanceValue = getArguments().getInt(ARG_PERFORMANCE_VALUE);
            dayDeficitValue = getArguments().getInt(ARG_DAYDEFICIT_VALUE);
            sumAteValue = getArguments().getInt(ARG_SUM_ATE_VALUE);
            noData = getArguments().getString(ARG_NODATA);
            sumSportValue = getArguments().getInt(ARG_SUM_SPORT_VALUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        TextView summaryOverviewIdTV = view.findViewById(R.id.summaryOverviewIdTV);
        TextView summaryOverviewPerformanceValueTV = view.findViewById(R.id.summaryOverviewPerformanceValueTV);
        TextView summaryOverviewDailyDeficitValueTV = view.findViewById(R.id.summaryOverviewDailyDeficitValueTV);
        TextView summaryOverviewAteValueTV = view.findViewById(R.id.summaryOverviewAteValueTV);
        TextView summaryOverviewSportValueTV = view.findViewById(R.id.summaryOverviewSportValueTV);

        TextView summaryOverviewSubtotalValueTV = view.findViewById(R.id.summaryOverviewSubtotalValueTV);
        TextView summaryOverviewOpenCaloriesTextTV = view.findViewById(R.id.summaryOverviewOpenCaloriesTextTV);
        LinearLayout summaryOverviewOpenCaloriesLinearLayout = view.findViewById(R.id.summaryOverviewOpenCaloriesLinearLayout);
        TextView   summaryOverviewOpenCaloriesValueTV = view.findViewById(R.id.summaryOverviewOpenCaloriesValueTV);

        if(noData == null){

            //zu berechnen
            int subTotal = performanceValue - dayDeficitValue;
            summaryOverviewSubtotalValueTV.setText(String.valueOf(subTotal));

            int openCalories = subTotal - sumAteValue + sumSportValue;
            summaryOverviewOpenCaloriesValueTV.setText( String.valueOf(openCalories) );

            if(openCalories >= 0){
                summaryOverviewOpenCaloriesTextTV.setText(R.string.java_overview_fragment_total_savings);
                summaryOverviewOpenCaloriesLinearLayout.setBackgroundColor(Color.argb(100,255,141,0)); //orange
            }else {
                summaryOverviewOpenCaloriesTextTV.setText(R.string.java_overview_fragment_total);
                summaryOverviewOpenCaloriesLinearLayout.setBackgroundColor(Color.RED); //orange
            }

            summaryOverviewIdTV.setText(date);
            summaryOverviewPerformanceValueTV.setText(String.valueOf( performanceValue ));
            if(dayDeficitValue > 0){
                summaryOverviewDailyDeficitValueTV.setText( "-" + dayDeficitValue );
            }else {
                summaryOverviewDailyDeficitValueTV.setText("0");
            }

            if (sumAteValue == 0){
                summaryOverviewAteValueTV.setText( "0");
            }else if(sumAteValue > 0){
                summaryOverviewAteValueTV.setText( "-" + sumAteValue );
            }else {
                summaryOverviewAteValueTV.setText( "+" + sumAteValue );
            }

            if (sumSportValue == 0){
                summaryOverviewSportValueTV.setText( "0");
            }else if(sumSportValue > 0){
                summaryOverviewSportValueTV.setText( "+" + sumSportValue );
            }else {
                summaryOverviewSportValueTV.setText( "-" + sumSportValue );
            }


        }else{//KEINE DATEN VORHANDEN
            summaryOverviewIdTV.setText(date);
            summaryOverviewPerformanceValueTV.setText("0");
            summaryOverviewDailyDeficitValueTV.setText("0");
            summaryOverviewAteValueTV.setText("0");
            summaryOverviewSportValueTV.setText("0");
            summaryOverviewSubtotalValueTV.setText("0");
            summaryOverviewOpenCaloriesValueTV.setText( "0");
            summaryOverviewOpenCaloriesLinearLayout.setBackgroundColor(Color.argb(50,0,0,255)); //orange
        }



        return view;
    }
}