package kevinhuang.facial_authentication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final int VIDEO_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void capturePicture(View view){

        Intent camera_intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        this.startActivity(camera_intent);
        File video_file = getFilepath();
        Uri video_uri = Uri.fromFile(video_file);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == VIDEO_REQUEST_CODE){
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "Pictures Successfully Captured", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Picture Capture Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public File getFilepath(){
        File folder = new File("sdcard/video_app");
        if(folder.exists()){
            folder.mkdir();
        }

        File video_file = new File(folder, "sample_video.mp4");

        return video_file;
    }
}
