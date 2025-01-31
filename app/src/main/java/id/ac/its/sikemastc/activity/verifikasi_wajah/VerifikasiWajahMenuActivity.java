package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasSessionManager;

public class VerifikasiWajahMenuActivity extends AppCompatActivity {

    private final String TAG = VerifikasiWajahMenuActivity.class.getSimpleName();

    private SharedPreferences flagStatus;
    private Button btnVerifikasiWajah;
    private Context mContext;
    private Toolbar toolbar;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;
    private SikemasSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_menu_verifikasi_wajah);

        mContext = this;
        session = new SikemasSessionManager(this);
        flagStatus = getSharedPreferences("flag_status", 0);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Verifikasi Wajah");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10f);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        HashMap<String, String> userDetail = session.getUserDetails();
        nrpMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        namaMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_NAME);
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");
        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(nrpMahasiswa + " - " + namaMahasiswa);

        Button btnKelolaDataSetWajah = (Button) findViewById(R.id.btn_kelola_data_set_wajah);
        btnVerifikasiWajah = (Button) findViewById(R.id.btn_verification_view);

        btnKelolaDataSetWajah.setOnClickListener(operate);
        btnVerifikasiWajah.setOnClickListener(operate);

        checkDataSetStatus();
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_kelola_data_set_wajah:
                    Intent intentToSetWajah = new Intent(v.getContext(), KelolaDataSetWajahActivity.class);
                    intentToSetWajah.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                    intentToSetWajah.putExtra("id_mahasiswa", nrpMahasiswa);
                    intentToSetWajah.putExtra("id_perkuliahan", idPerkuliahan);
                    startActivity(intentToSetWajah);
                    finish();
                    break;
                case R.id.btn_verification_view:
                    if (!flagStatus.getBoolean("training_flag", false)) {
                        Toast.makeText(getApplication(), "Fitur Verifikasi Kehadiran tidak dapat " +
                                        "digunakan\nFitur ini aktif ketika data wajah telah ditraining sebelumnya",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Intent intentToRecognition = new Intent(v.getContext(), VerifikasiWajahActivity.class);
                        intentToRecognition.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                        intentToRecognition.putExtra("id_perkuliahan", idPerkuliahan);
                        startActivity(intentToRecognition);
                        finish();
                    }
                    break;
            }
        }
    };

    private void checkDataSetStatus() {
        boolean flag = flagStatus.getBoolean("training_flag", false);
        if (!flag) {
            btnVerifikasiWajah.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_error_outline), null, null, null);
            btnVerifikasiWajah.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        } else {
            btnVerifikasiWajah.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_verified_user), null, null, null);
            btnVerifikasiWajah.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryText));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataSetStatus();
    }
}