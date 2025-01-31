package id.ac.its.sikemastc.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

import id.ac.its.sikemastc.activity.LoginActivity;
import id.ac.its.sikemastc.sync.SikemasSyncTask;

public class SikemasSessionManager {

    private static String TAG = SikemasSessionManager.class.getSimpleName();

    SharedPreferences pref;
    Editor editor;
    Context _context;

    // Shared preference mode
    int PRIVATE_MODE = 0;
    // Shared preference filename
    private static final String PREF_NAME = "SikemasSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_ROLE = "userRole";
    public static final String KEY_USER_CODE = "userCode";

    public SikemasSessionManager(Context context) {
        this._context = context;
        this.pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
    }

    // session Dosen
    public void createLoginSession(String userId, String name, String email, String role, String kodeUser) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_CODE, kodeUser);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    // session check data wajah / signature
    public void checkUserDataSet(int jumlahWajah, int jumlahSignature) {
        editor.putInt("jumlah_wajah", jumlahWajah);
        editor.putInt("jumlah_signature", jumlahSignature);
        editor.commit();
        Log.d(TAG, "Check user data set length" + jumlahWajah + " - " + jumlahSignature);
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Starting Login Activity
            _context.startActivity(intent);
        }
    }

    public HashMap<String, String> getUserDetails(){
            HashMap<String, String> user = new HashMap<>();

            user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
            user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
            user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));
            user.put(KEY_USER_ROLE, pref.getString(KEY_USER_ROLE, null));
            user.put(KEY_USER_CODE, pref.getString(KEY_USER_CODE, null));

            return user;
    }

    public HashMap<String, Integer> getDataSetLength() {
        HashMap<String, Integer> jumlahData = new HashMap<>();
        jumlahData.put("jumlah_wajah", pref.getInt("jumlah_wajah", 0));
        jumlahData.put("jumlah_signature", pref.getInt("jumlah_signature", 0));

        return jumlahData;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(intent);
        deleteFaceRecognitionLocalData();

        // Destroy the database
        SikemasSyncTask.syncLogoutSession(_context);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void deleteFaceRecognitionLocalData() {
        File fileOrDirectory = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + "/facerecognition/");
        deleteRecursive(fileOrDirectory);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }
}
