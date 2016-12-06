package afengg.oneaday;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkPermissionsForShowImage();
        // The following line should be commented out while under development
        // checkIfDailyLimitExceeded();
        Button delImage = (Button) findViewById(R.id.button_delete_image);
        delImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                checkPermissionsForDeleteImage();
                transitionToEnd();
            }
        });

        Button saveImage = (Button) findViewById(R.id.button_save_image);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                incrementCounter();
                transitionToEnd();
            }
        });

    }

    private void incrementCounter(){
        // Update the counter in order to skip the current image next time
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int counter = prefs.getInt("counter",0);
        counter++;
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("counter", counter);
        edit.commit();
    }

    private void checkIfDailyLimitExceeded(){
        Date currDate = new Date();
        // Convert to long to store in SharedPrefs
        Long currDateLong = currDate.getTime();
        //Retrieve the last date this app was opened, stored in SharedPref
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Long prevDateLong = prefs.getLong("lastVisited",0);
        //Subtract the two dates and check if greater than 24 hours measured in milliseconds
        //             milli  min  hr   day
        long hours24 = 1000 * 60 * 60 * 24;

        if(currDateLong - prevDateLong < hours24 ){
            // Go to end activity
            transitionToEnd();
        }
        else{
            // Else, update the last visited time to the current time
            // and allow user to proceed
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong("lastVisited",currDateLong);
            edit.commit();
        }

    }


    private void deleteImage(){
        TextView textView = (TextView) findViewById(R.id.text_view);
        String filepath = (String) textView.getText();
        File f = new File(filepath);
        if(f.exists()) {
            f.delete();
            textView.setText("Deleted!");
        }
    }

    private void transitionToEnd(){
        Intent intent = new Intent(this, EndActivity.class);
        finish();
        startActivity(intent);
    }

    private void checkPermissionsForDeleteImage(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            deleteImage();
        }
        else {
            requestWriteStoragePermission();
        }
    }
    private void checkPermissionsForShowImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            showOneADay();
        }
        else {
            requestReadStoragePermission();
        }
    }
    private void showOneADay(){
            /*Get sharedpreferences value of counter
            Counter represents number of images to skip.
            Counter will default to value of 0 in the case that it does not exist in sharedpref
            */
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int counter = prefs.getInt("counter",0);
            //Order images in internal storage by date taken
            String [] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN};
            String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " ASC";
            Cursor cursor = this.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    orderBy);
            if (cursor != null && cursor.moveToFirst() ) {
                // Check if there are less images than our counter has counted
                // if so, set counter to 0 and restart
                if(cursor.getCount() < counter){
                    // fill ths in later
                }
                // Else, we move our cursor to the next unseen image
                else{
                    cursor.moveToPosition(counter);
                }
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String photoPath = cursor.getString(columnIndex);
                TextView textView = (TextView) findViewById(R.id.text_view);
                textView.setText(photoPath);
                cursor.close();
                ImageView imageView = (ImageView) findViewById(R.id.image_view);
                imageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] Permissions, int[] grantResults){
        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //granted permission
                showOneADay();
            }
            else {
                this.finish();
                System.exit(0);
            }
        }
        else if (requestCode == MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //granted permission
                deleteImage();
            }
        }
    }

    private void requestReadStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    private void requestWriteStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }
}
