package schaefer.anatoli.caloriedeficit.settingsfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.SettingsActivity;


public class ActivityDataFragment extends Fragment {

    private Button nextButton;
    private RadioGroup palActivityRadioGroup;
    private RadioButton palSitAndLieRadioButton, palSitAndLessActRadioButton, palSitAndMoreActRadioButton, palWalkAndStayRadioButton, palHardWorkRadioButton;
    //private final float[] PALVALUES = new float[]{1.2f, 1.45f, 1.65f, 1.85f, 2.2f};
    private final float[] PALVALUES = new float[]{1.2f, 1.4f, 1.6f, 1.8f, 2.0f};



    private float palActivityValue;


    public ActivityDataFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityDataFragment.

    // TODO: Rename and change types and number of parameters
    public static ActivityDataFragment newInstance(String param1, String param2) {
        ActivityDataFragment fragment = new ActivityDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            palActivityValue = getArguments().getFloat("palActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_data, container, false);

        nextButton = view.findViewById(R.id.activityDataFNextButton);
        palActivityRadioGroup = view.findViewById(R.id.palRadioGroup);

        palSitAndLieRadioButton = view.findViewById(R.id.palSitAndLieRadioButton);
        palSitAndLessActRadioButton = view.findViewById(R.id.palSitAndLessActRadioButton);
        palSitAndMoreActRadioButton = view.findViewById(R.id.palSitAndMoreActRadioButton);
        palWalkAndStayRadioButton = view.findViewById(R.id.palWalkAndStayRadioButton);
        palHardWorkRadioButton = view.findViewById(R.id.palHardWorkRadioButton);

        float epsilon = 0.00000001f;


        int positionPayValue = -1;

        for(int i=0; i< PALVALUES.length; i++){
            if(Math.abs(PALVALUES[i] - palActivityValue) < epsilon)
                positionPayValue = i;
        }

        switch (positionPayValue){
            case 0: palSitAndLieRadioButton.setChecked(true); break;
            case 1: palSitAndLessActRadioButton.setChecked(true); break;
            case 2: palSitAndMoreActRadioButton.setChecked(true); break;
            case 3: palWalkAndStayRadioButton.setChecked(true); break;
            case 4: palHardWorkRadioButton.setChecked(true); break;
            default: break;

        }



        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                int palValuePosition = -1;
                switch (palActivityRadioGroup.getCheckedRadioButtonId() ){
                    case R.id.palSitAndLieRadioButton:
                        //1.2 oder PALVALUES[0]
                        palValuePosition = 0;
                        break;
                    case R.id.palSitAndLessActRadioButton:
                        palValuePosition = 1;
                        break;
                    case R.id.palSitAndMoreActRadioButton:
                        palValuePosition = 2;
                        break;
                    case R.id.palWalkAndStayRadioButton:
                        palValuePosition = 3;
                        break;
                    case R.id.palHardWorkRadioButton:
                        palValuePosition = 4;
                        break;
                    default:

                        break;
                }

                SettingsActivity settingsActivity = (SettingsActivity) getActivity();

                if(palValuePosition == -1){
                    //keins ausgewählt

                    settingsActivity.showAlertDialog();
                }else{
                    settingsActivity.setResultFromActivityDataFragment(PALVALUES[palValuePosition]);

                    BeginImageFragment beginImageFragment = new BeginImageFragment();
                    settingsActivity.replaceFragment(beginImageFragment);
                }


            }
        });

        return view;
    }

}