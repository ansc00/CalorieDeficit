package schaefer.anatoli.caloriedeficit.settingsfragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import schaefer.anatoli.caloriedeficit.OverviewActivity;
import schaefer.anatoli.caloriedeficit.R;
import schaefer.anatoli.caloriedeficit.SettingsActivity;
import schaefer.anatoli.caloriedeficit.util.ImageConverter;

import static android.app.Activity.RESULT_OK;


public class BeginImageFragment extends Fragment implements View.OnClickListener{

    private Button saveButton;
    private Button beginImageCancelButton;
    private ImageView camImageView;
    private ImageView pictureImageView;
    private TextView dateTextView;
    private Bitmap imageBitmap;


    private  boolean isShownFirstTime;



    public static final int PERMISSION_REQUEST_CODE_CAMERA = 1;
    //public static final int PERMISSION_REQUEST_CODE_STORAGE = 2;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private String dateString;
    private byte[] firstImage;

    public BeginImageFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firstImage = getArguments().getByteArray("firstImage");
            dateString = getArguments().getString("date");

            //abbrechen button steuern
            isShownFirstTime = getArguments().getBoolean("isShownFirstTime");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_begin_image, container, false);

        camImageView = view.findViewById(R.id.camImageView);
        pictureImageView = view.findViewById(R.id.pictureImageView);
        saveButton = view.findViewById(R.id.beginImageSaveButton);
        dateTextView = view.findViewById(R.id.dateTextView);
        beginImageCancelButton = view.findViewById(R.id.beginImageCancelButton);
        camImageView.setOnClickListener(this);
        pictureImageView.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        beginImageCancelButton.setOnClickListener(this);


        if(firstImage != null){
            imageBitmap = ImageConverter.byteArrayToBitmap(firstImage);
            pictureImageView.setImageBitmap( imageBitmap);
            camImageView.setVisibility(View.INVISIBLE);

        }

        updateDateTextView();


        return view;
    }

    private void updateDateTextView() {
        if(dateString != null){
            dateTextView.setText(dateString);
        }else {

            Locale locale = getResources().getConfiguration().locale;
            if (locale.equals(Locale.GERMANY)){
                //heutiges datum setzen, falls es NULL ist
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd.MM.YYYY", Locale.GERMANY);
                dateString =  simpleDateFormat.format(new Date());
                dateTextView.setText(dateString);
            }else{
                //heutiges datum setzen, falls es NULL ist
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YYYY", Locale.US);
                dateString =  simpleDateFormat.format(new Date());
                dateTextView.setText(dateString);

            }

        }

        //abbrechen button steuern //wird beim erste mal NICHT angezeigt!
        if(isShownFirstTime == true){
            beginImageCancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        SettingsActivity settingsActivity = (SettingsActivity) getActivity();

        switch (v.getId()){
            case R.id.camImageView: //walktrough
            case R.id.pictureImageView:
                //ask permission and grab photo
                if(checkCameraPermission() == true){
                    takeAPhotoFromCamera();
                }else {
                    //um erlaubnis fragen
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
                }
                break;

            case R.id.beginImageSaveButton:
                //bild in DB speichern +  weiterleitung an nächsten SCREEN

                byte[] image = null;
                if(imageBitmap != null){
                    image = ImageConverter.bitmapToByteArray( imageBitmap);
                }

                settingsActivity.setResultFromBeginImageFragment( image , dateString );
                settingsActivity.savePersonalDataToDB();
                if(!isShownFirstTime){//Beim ersten mal wird nur INFO angezeigt. Danach immer Toast!
                    Toast.makeText(getActivity(), R.string.java_overview_fragment_Toast_settings_saved, Toast.LENGTH_SHORT).show();
                }

                settingsActivity.goToOverviewActivity();
                break;
            case R.id.beginImageCancelButton:
                settingsActivity.goToOverviewActivity();
        }
    }


    private boolean checkCameraPermission() {
       if( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ){
           //haben die erlaubnis dafür
            return true;
       }else{
           return false;
       }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeAPhotoFromCamera();
                } else {
                    Toast.makeText(getActivity(), R.string.java_overview_fragment_camera_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void takeAPhotoFromCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            pictureImageView.setImageBitmap(imageBitmap);
            camImageView.setVisibility(View.INVISIBLE); //camera symbol ausblenden

            //neues bild also auch neues datum setzen!
            dateString = null;
            updateDateTextView();

        }

    }
}

