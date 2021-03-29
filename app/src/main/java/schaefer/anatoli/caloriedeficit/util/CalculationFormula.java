package schaefer.anatoli.caloriedeficit.util;

import java.text.DecimalFormat;

public class CalculationFormula {

    public static String calculateBMI(int bodySize, int weightKg) {
        double val  = (bodySize * bodySize) / (double) (100 * 100) ;
        DecimalFormat df = new DecimalFormat("#0.0");
        //Log.d("test","OverviewActivity: " + val);
        return df.format(weightKg / val);
    }

    public static double calculateBaseCalorieValue(String gender, int bodySize, int weightKg, int age){
        //Harris-Benedict-Formel // seit 1918
        double base = 0.0;
        if(gender.equals("m")){
            base = 66.47 + (13.7 * weightKg ) + (5.0 * bodySize) - (6.8 * age );
        }else{
            //frau
            base = 655.1 + (9.6 * weightKg ) + (1.8 * bodySize ) - (4.7 * age );
        }

        return  Math.round(base);
    }

    public static String calculatePerformanceValue(float palValue, double baseCalorieValue){
        double performanceVal =  baseCalorieValue  * palValue;
        return String.valueOf( Math.round(performanceVal) );
    }


    public static double   lbsToKg(double lbs){
        return lbs  * 0.453592;
    }

    public static int feetInchesToCm(int feet, int inches){
        double oneInch = 2.54; //cm
        double oneFeet = 30.48; //cm
        double size = feet * oneFeet + inches * oneInch;
        return (int) Math.round(size);
    }
}
