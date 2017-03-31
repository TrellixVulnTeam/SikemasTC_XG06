package id.ac.its.sikemastc;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import id.ac.its.sikemastc.activity.dosen.HalamanUtamaDosen;
import id.ac.its.sikemastc.activity.dosen.ListKelasDiampu;
import id.ac.its.sikemastc.activity.dosen.PenjadwalanUlang;
import id.ac.its.sikemastc.activity.mahasiswa.HalamanUtamaMahasiswa;
import id.ac.its.sikemastc.activity.mahasiswa.LihatJadwal;
import id.ac.its.sikemastc.activity.orangtua.HalamanUtamaOrangtua;
import id.ac.its.sikemastc.activity.orangtua.LihatJadwalMahasiswa;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.data.SikemasSessionManager;

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mActivityContent;
    public SikemasSessionManager session;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        session = new SikemasSessionManager(this);
        session.checkLogin();

        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        mActivityContent = (FrameLayout) mDrawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, mActivityContent, true);
        super.setContentView(mDrawerLayout);
        Intent intent = getIntent();
        HashMap<String, String> userDetail = session.getUserDetails();

        int checkedDrawerItemId = intent.getIntExtra("checkedDrawerItemId", 0);
        String title = intent.getStringExtra("title");
        String userNama = userDetail.get(SikemasSessionManager.KEY_USER_NAME);
        String userEmail = userDetail.get(SikemasSessionManager.KEY_USER_EMAIL);
        String userId = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        String userRole = userDetail.get(SikemasSessionManager.KEY_USER_ROLE);
        if (checkedDrawerItemId == 0) {
            if (userRole.equals("1"))
                checkedDrawerItemId = R.id.item_home_dosen;
            else if (userRole.equals("2"))
                checkedDrawerItemId = R.id.item_home_mahasiswa;
            else
                checkedDrawerItemId = R.id.item_home_orangtua;
        }

        setupToolbarMenu(title);
        setupNavigationDrawerMenu(checkedDrawerItemId, userNama, userEmail, userId, userRole);
    }

    private void setupToolbarMenu(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (title == null)
            mToolbar.setTitle("Home");
        else
            mToolbar.setTitle(title);
    }

    private void setupNavigationDrawerMenu(int checkedDrawerItemId, String userNama, String userEmail, String userId, String userRole) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View navHeader = navigationView.getHeaderView(0);
        TextView tvName = (TextView) navHeader.findViewById(R.id.tv_nama_user);
        TextView tvEmail = (TextView) navHeader.findViewById(R.id.tv_email_user);
        TextView tvUserId = (TextView) navHeader.findViewById(R.id.tv_id_user);
        tvName.setText(userNama);
        tvEmail.setText(userEmail);
        tvUserId.setText(userId);

        switch (userRole) {
            case "1":
                navigationView.inflateMenu(R.menu.menu_item_dosen);
                break;
            case "2":
                navigationView.inflateMenu(R.menu.menu_item_mahasiswa);
                break;
            case "3":
                navigationView.inflateMenu(R.menu.menu_item_orangtua);
                break;
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (checkedDrawerItemId == 0)
            navigationView.getMenu().getItem(0).setChecked(false);
        else
            navigationView.setCheckedItem(checkedDrawerItemId);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToogle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToogle);
        drawerToogle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String itemName = (String) menuItem.getTitle();
        Toast.makeText(BaseActivity.this, itemName + " Clicked", Toast.LENGTH_SHORT).show();

        closeDrawer();

        switch (menuItem.getItemId()) {
            case R.id.item_home_dosen:
                Intent intentToHomeDosen = new Intent(this, HalamanUtamaDosen.class);
                intentToHomeDosen.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHomeDosen.putExtra("title", menuItem.getTitle());
                startActivity(intentToHomeDosen);
                finish();
                break;
            case R.id.item_home_mahasiswa:
                Intent intentToHomeMahasiswa = new Intent(this, HalamanUtamaMahasiswa.class);
                intentToHomeMahasiswa.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHomeMahasiswa.putExtra("title", menuItem.getTitle());
                startActivity(intentToHomeMahasiswa);
                finish();
                break;
            case R.id.item_home_orangtua:
                Intent intentToHomeOrangtua = new Intent(this, HalamanUtamaOrangtua.class);
                intentToHomeOrangtua.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToHomeOrangtua.putExtra("title", menuItem.getTitle());
                startActivity(intentToHomeOrangtua);
                finish();
                break;
            case R.id.item_penjadwalan_ulang:
                Intent intentToPenjadwalan = new Intent(this, PenjadwalanUlang.class);
                intentToPenjadwalan.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToPenjadwalan.putExtra("title", menuItem.getTitle());
                startActivity(intentToPenjadwalan);
                finish();
                break;
            case R.id.item_list_kelas_diampu:
                Intent intentToListKelas = new Intent(this, ListKelasDiampu.class);
                intentToListKelas.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToListKelas.putExtra("title", menuItem.getTitle());
                startActivity(intentToListKelas);
                finish();
                break;
            case R.id.item_lihat_jadwal:
                Intent intentToJadwal = new Intent(this, LihatJadwal.class);
                intentToJadwal.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToJadwal.putExtra("title", menuItem.getTitle());
                startActivity(intentToJadwal);
                finish();
                break;
            case R.id.item_lihat_jadwal_mahasiswa:
                Intent intentToJadwalMhs = new Intent(this, LihatJadwalMahasiswa.class);
                intentToJadwalMhs.putExtra("checkedDrawerItemId", menuItem.getItemId());
                intentToJadwalMhs.putExtra("title", menuItem.getTitle());
                startActivity(intentToJadwalMhs);
                finish();
                break;
            case R.id.item_settings:
                break;
            case R.id.item_logout:
                Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
                session.logoutUser();
                finish();
                return true;
        }
        return true;
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }
}
