package com.example.struchinskiy.catalog_bns;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static DB db;
    public SharedPreferences preferences;
    private static final String first_update = "01.12.2018";
    private static final String e_mail = "a.struchinsky@bns.by";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Calendar calendar;
    private List<Person> people;
    private SpisokCard spisokCard;
    private SearchView search;
    private Long update_date;


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getLastCustomNonConfigurationInstance();
        if (activity != null) {
            this.spisokCard = activity.spisokCard;
        }
        setContentView(R.layout.activity_main);
        //android:background="?attr/colorPrimary"

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (activity == null) {
           // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
           // StrictMode.setThreadPolicy(policy);
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            //StrictMode.setVmPolicy(builder.build());

            preferences = getPreferences(MODE_PRIVATE);
            calendar = Calendar.getInstance();
           // update_date = calendar.getTimeInMillis();
          //  curr_month = calendar.get(Calendar.MONTH) + 1;

            if (preferences.contains("update_date")) {
                update_date = preferences.getLong("update_date", 123);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                try {
                    changeUpdateDate("update_date", format.parse(first_update).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (preferences.contains("check_date")) {
                long check_date = preferences.getLong("check_date", calendar.getTimeInMillis());
                if (calendar.getTimeInMillis() >= check_date) {
                    FTPbase ftPbase = new FTPbase(this);
                    ftPbase.execute(new Date(update_date));
                } else
                    connectToDB();
            } else {
                calendar.getTimeInMillis();
                changeUpdateDate("check_date", calendar.getTimeInMillis());
                connectToDB();
            }
        } else
            connectToDB();
    }


    public void changeUpdateDate(String type, Long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(type, value);
        editor.apply();
    }

    private static class FTPbase extends AsyncTask<Date,Void,Boolean> {
        @SuppressLint("StaticFieldLeak")
        private Context context;
        private ProgressDialog dialog;
        private String result;
        private Boolean isUpdated = false, isNewApk = false;
        private Date update_db_date_new;
        private Date update_apk_date_new;
        private Date update_date_new;

        FTPbase(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Подключение к серверу");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Date... dates) {
           // String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/bns.apk";
           // String download = context.getCacheDir();
            FTPClient ftpClient;
            ftpClient = new FTPClient();
            try {
                ftpClient.connect("86.57.155.212");
                ftpClient.login("allex", "327");
               // String aaa = ftpClient.getReplyString();
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
               // ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
                result = "Не удалось подключиться к серверу для проверки обновлений.";
                return false;
            }
            try {
                publishProgress();
                FileOutputStream fileOutputStream = new FileOutputStream(context.getFilesDir() + "/update_date.txt");
                ftpClient.retrieveFile("/private/update_date.txt", fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                File file = new File(context.getFilesDir() + "/update_date.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String db_date_new, apk_date_new;
                db_date_new = br.readLine();
                apk_date_new = br.readLine();
                br.close();

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                update_db_date_new = format.parse(db_date_new);
                Date last_update = dates[0];
                update_apk_date_new = format.parse(apk_date_new);

                if (update_apk_date_new.after(update_db_date_new))
                    update_date_new = update_apk_date_new;
                else
                    update_date_new = update_db_date_new;

                if (update_db_date_new.after(last_update)) {
                    fileOutputStream = new FileOutputStream(context.getDatabasePath("data.sqlite"));
                    ftpClient.retrieveFile("/private/data.sqlite", fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    isUpdated = true;
                }
                if (update_apk_date_new.after(last_update)) {
                    FTPFile apk = ftpClient.mlistFile("/private/bns.apk");
                    long fileSize = apk.getSize();
                    File file1;
                    long s;
                    file1 = new File(download);
                    do {
                        //  file1 = new File(download);
                        FileOutputStream fos = new FileOutputStream(file1);
                        ftpClient.retrieveFile("/private/bns.apk", fos);
                        fos.flush();
                        fos.close();
                        s = file1.length();
                        // count++;
                        // if (count == 10) {
                        //      break;
                        //  }
                    } while (s != fileSize);
                    isNewApk = true;
                    isUpdated = true;
                }
                if (!isUpdated) {
                    result = "Новых обновлений нет.";
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "Не удалось обновить приложение.";
                return false;
          //  } catch (InterruptedException e) {
           //     e.printStackTrace();
            } finally {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            result = "Приложение обновлено.";
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            super.onProgressUpdate(voids);
                dialog.setMessage("Поиск обновлений");
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if (dialog.isShowing())
                dialog.dismiss();
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            if (!isUpdated)
                update_db_date_new = null;
            ((MainActivity)context).AsynsFinished(response, isNewApk, update_date_new);
        }
    }

    public void AsynsFinished(Boolean result, Boolean newApk, Date update) {
        if (result) {
            calendar.add(Calendar.MONTH, 1);
            changeUpdateDate("check_date", calendar.getTimeInMillis());
            if (update != null)
                changeUpdateDate("update_date", update.getTime());
            if (newApk) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "bns.apk")), "application/vnd.android.package-archive");
               // intent.setDataAndType(Uri.fromFile(new File(this.getFilesDir() + "/bns.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }
        if (db == null)
            connectToDB();
       // if (db == null)
         //   connectToDB();
    }

    public void connectToDB() {
        db = new DB(this);
        try {
            db.open();
            if (spisokCard == null) {
                spisokCard = new SpisokCard();
                Bundle bundle = new Bundle();
                bundle.putString("type", "fio");
                bundle.putString("search", "");
                spisokCard.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, spisokCard).commit();
            }
          //  Toast.makeText(this, "Подключено.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
        if (search != null && !search.getQuery().toString().equals("")) {
            search.setQuery("", false);
        } else {
            super.onBackPressed();
        }
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        appBarLayout.setExpanded(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
               // Log.d(TAG, "onQueryTextSubmit ");
                spisokCard.onTextSubmit(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
              //  Log.d(TAG, "onQueryTextChange ");
                spisokCard.onTextChange(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Bundle bundle = new Bundle();
        Fragment fragment = null;
        Intent intent;
        switch (item.getItemId()) {
            case R.id.search:
              /// bundle.putString("type", "fio");
              //  bundle.putString("search", "");
               // fragment = new SpisokCard();
                fragment = spisokCard;
             //   getSupportFragmentManager().beginTransaction().show(spisokCard).commit();
                break;
            case R.id.ext_search:
                bundle.putString("type", "fio");
                fragment = new Search();
                break;
            case R.id.nav_search:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                    break;
                }
               if (preferences.contains("update_date"))
                    update_date = preferences.getLong("update_date", update_date);
                FTPbase ftpBase = new FTPbase(this);
                ftpBase.execute(new Date(update_date));
                break;
            case R.id.nav_send:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + e_mail));
                startActivity(Intent.createChooser(emailIntent, "Отправить сообщение"));
                /*intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "bns.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                break;
            case R.id.s_birth_day:
                bundle.putString("type", "birth_day");
                fragment = new SpisokCard();
                break;
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,
                            "Обновление приложения доступно", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Обновление приложения не доступно", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null)
            db.close();
    }
}
