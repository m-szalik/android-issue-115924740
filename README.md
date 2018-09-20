# Android issue 115924740
https://issuetracker.google.com/u/1/issues/115924740

This example app makes a call and after 4s toggles speaker phone.

Result:
 * Android 8.0 - API 26 and higher:  
  There is a message about missing permission in logcat output that additional permission is required `MODIFY_PHONE_STATE`. That permission is granted to system applications only.

Logcat message from Android 8.1 - emulator:
```
W/AudioService: MODIFY_PHONE_STATE Permission Denial: setMode(MODE_IN_CALL) from pid=5140, uid=10085
```

Despite the message on some devices application works correctly.
