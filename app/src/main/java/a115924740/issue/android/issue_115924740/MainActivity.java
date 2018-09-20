package a115924740.issue.android.issue_115924740;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQ_CODE = 10283974;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.CALL_PHONE,                 // for making a call
            Manifest.permission.MODIFY_AUDIO_SETTINGS,      // for enabling speaker phone
//            Manifest.permission.MODIFY_PHONE_STATE,         // for enabling speaker phone
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonMakeACall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phoneEditText = findViewById(R.id.editTextPhone);
                String phone = phoneEditText.getText().toString().trim();
                if (phone.length() < 3) {
                    Toast.makeText(MainActivity.this, "Please enter valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkPermissions()) { // check if all required permissions are granted than make a call.
                            makeACall(phone);
                        } else { // ask for permissions
                            requestPermissions(PERMISSIONS, PERMISSION_REQ_CODE);
                        }
                    } else {
                        makeACall(phone);
                    }
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (checkPermissions()) {
                Toast.makeText(this, "Click again to make a call.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Missing required permissions.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check if app has all required permissions
     * @return true if all permissions are granted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        for(String perm : PERMISSIONS) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Missing permission: " + perm);
                return false;
            }
        }
        return true;
    }

    /**
     * Make a call with speaker phone enabled.
     */
    @SuppressLint("MissingPermission") // already granted
    private void makeACall(String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleSpeakerPhone();
            }
        }, 4000); // after 4s
    }

    /**
     * Try to enable/disable speaker phone
     */
    private void toggleSpeakerPhone() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            Log.i(TAG, "Try to toggle speaker phone.");
            audioManager.setMode(AudioManager.MODE_IN_CALL); // this requires also MODIFY_PHONE_STATE permission
            boolean state = audioManager.isSpeakerphoneOn();
            audioManager.setSpeakerphoneOn(! state);
            final String msg = "Speaker phone should be " + (state ? "OFF" : "ON") + " now. Result: " + (audioManager.isSpeakerphoneOn() ? "ON" : "OFF");
            Log.i(TAG, msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
