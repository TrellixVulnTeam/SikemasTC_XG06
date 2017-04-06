package id.ac.its.sikemastc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import id.ac.its.sikemastc.R;

import static android.content.ContentValues.TAG;

public class AddSetWajah extends AppCompatActivity {

    private String UPLOAD_URL = "http://10.0.2.2/VolleyUpload/upload.php";
    private String KEY_IMAGE = "image";
    private String KEY_IMAGE_PHOTO_NAME = "image_name";
    private String KEY_USER_ID = "user_id";

    private String userTerlogin;
    private String userId;
    private ArrayList<String> encodedImageList;

    private Context mContext;
    private TextView tvUserTerlogin;
    private TextView tvJumlahDataSet;
    private Button btnStart;
    private Button btnSinkronisasi;
    private Button btnUploadFile;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_wajah);
        mContext = this;

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        userTerlogin = intent.getStringExtra("identitas_mahasiswa");
        userId = intent.getStringExtra("id_mahasiswa");

        tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvJumlahDataSet = (TextView) findViewById(R.id.tv_data_set);

        tvUserTerlogin.setText(userTerlogin);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnSinkronisasi = (Button) findViewById(R.id.btn_sinkronisasi_dataset);
        btnUploadFile = (Button) findViewById(R.id.btn_upload_file);

        btnStart.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnUploadFile.setOnClickListener(operate);

        if (getJumlahDataSet() > 0) {
            tvJumlahDataSet.setText(String.valueOf(getJumlahDataSet()));
        }
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_start:
                    Intent intentToStart = new Intent(v.getContext(), AddSetWajahPreview.class);
                    intentToStart.putExtra("user_terlogin", userTerlogin);
                    intentToStart.putExtra("method", AddSetWajahPreview.TIME);
                    intentToStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add dataset photos to "Training" folder
                    FileHelper newFile = new FileHelper();
                    if (isNameAlreadyUsed(newFile.getTrainingList(), userTerlogin)) {
                        Log.d("TrainingList", String.valueOf(newFile.getTrainingList()));
                        Toast.makeText(getApplicationContext(), "Data Set Wajah Ditemukan", Toast.LENGTH_SHORT).show();
                    } else {
                        intentToStart.putExtra("Folder", "Training");
                        startActivityForResult(intentToStart, 1);
                    }
                    break;
                case R.id.btn_sinkronisasi_dataset:
                    break;
                case R.id.btn_upload_file:
                    encodedImageList = new ArrayList<>();
                    encodedImageList = getAllEncodedImageFormat();
                    uploadImages(encodedImageList);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String resultMessage = data.getStringExtra("result_message");
                int numberOfPictures = data.getIntExtra("number_of_pictures", 0);
                Toast.makeText(mContext, resultMessage, Toast.LENGTH_SHORT).show();
                tvJumlahDataSet.setText(String.valueOf(numberOfPictures));
                encodedImageList = new ArrayList<>();
                encodedImageList = getAllEncodedImageFormat();
                uploadImages(encodedImageList);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private boolean isNameAlreadyUsed(File[] list, String name) {
        boolean used = false;
        if (list != null && list.length > 0) {
            for (File person : list) {
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length - 1];
                if (foldername.equals(name)) {
                    used = true;
                    break;
                }
            }
        }
        return used;
    }

    public int getJumlahDataSet() {
        FileHelper imageFiles = new FileHelper(userTerlogin);
        File[] imageList = imageFiles.getTrainingList();
        return imageList.length;
    }

    public ArrayList<String> getAllEncodedImageFormat() {
        ArrayList<String> encodedImage = new ArrayList<>();
        FileHelper imageFiles = new FileHelper(userTerlogin);
        File[] imageList = imageFiles.getTrainingList();
        Log.d("get All Image", String.valueOf(imageList.length));

        if (imageList != null && imageList.length > 0) {
            for (File image : imageList) {
                String imagePath = image.getAbsolutePath();
                Log.d("imagePath", imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
            }
        } else {
            return null;
        }
        return encodedImage;
    }

    // Upload image to server
    private void uploadImages(final ArrayList<String> encodedImagesList) {

        StringRequest stringRequest;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Showing the progress dialog
        progressDialog.setMessage("Mengunggah Data Set ke Server ... ");
        progressDialog.show();

        for (int i = 0; i < encodedImagesList.size(); i++) {
            final int index = i;
            stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Disimissing the progress dialog
                            progressDialog.dismiss();
                            //Showing toast message of the response
                            Log.d("VolleyResponse", "Dapat ResponseVolley Upload Images");
                            Toast.makeText(AddSetWajah.this, "Berhasil Kirim Data Set ke Server", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Dismissing the progress dialog
                            progressDialog.dismiss();
                            Log.d("VolleyErroyResponse", "Error");
                            //Showing toast
                            Toast.makeText(AddSetWajah.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);
                    // Creating parameters
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters
                    params.put(KEY_IMAGE, image);
                    params.put(KEY_IMAGE_PHOTO_NAME, userTerlogin + "_" + index);
                    params.put(KEY_USER_ID, userId);

                    //returning parameters
                    return params;
                }
            };
            //Adding request to the queue
            requestQueue.add(stringRequest);
        }
    }
}
