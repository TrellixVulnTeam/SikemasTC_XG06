package id.ac.its.sikemastc.activity.orangtua;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import java.util.HashMap;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.data.SikemasSessionManager;

public class HalamanUtamaOrangtua extends BaseActivity{

    private final String TAG = HalamanUtamaOrangtua.class.getSimpleName();
    private String userId;
    private String userNama;

    private ProgressBar mLoadingIndicator;
    private ConstraintLayout clEmptyView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;


    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_orangtua);

        HashMap<String, String> userDetail = session.getUserDetails();
        userId = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        userNama = userDetail.get(SikemasSessionManager.KEY_USER_NAME);

        mContext = this;

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);



    }
}
