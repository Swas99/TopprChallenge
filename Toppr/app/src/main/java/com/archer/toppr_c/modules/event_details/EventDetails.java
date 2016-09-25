package com.archer.toppr_c.modules.event_details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.EventDO;
import com.archer.toppr_c.util.MySQLiteHelper;
import com.archer.toppr_c.util.SharedPreferenceManager;
import com.archer.toppr_c.util.TopprUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class EventDetails
        extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        getSupportActionBar().setTitle("Event details");
        init();
    }

    private void init() {
        findViewById(R.id.ivFav).setOnClickListener(this);
        findViewById(R.id.btnShare).setOnClickListener(this);

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
        downloadAndSetImage(pref.getKeyData(TopprUtils.IMAGE));
        TextView tvName = (TextView)findViewById(R.id.tvName);
        tvName.setText(pref.getKeyData(TopprUtils.NAME));
        if(pref.getKeyData(TopprUtils.CATEGORY).trim().toLowerCase().equals(TopprUtils.HIRING))
        {
            findViewById(R.id.region_hiring).setVisibility(View.VISIBLE);
            TextView tvExperience = (TextView)findViewById(R.id.tvExperience);
            tvExperience.setText(String.format(" Experience: %s", pref.getKeyData(TopprUtils.EXPERIENCE)));

            TextView tvCtc = (TextView)findViewById(R.id.tvCtc);
            tvCtc.setText(" CTC: INR 15L - INR 36L");

            findViewById(R.id.ivCategory).setBackgroundResource(R.drawable.ic_hiring);
        }
        else
            findViewById(R.id.ivCategory).setBackgroundResource(R.drawable.ic_compititive);
        if(pref.getKeyData(TopprUtils.IS_FAV).isEmpty())
            findViewById(R.id.ivFav).setBackgroundResource(R.drawable.ic_empty_star);
        else
            findViewById(R.id.ivFav).setBackgroundResource(R.drawable.ic_full_star);

        TextView tvDescription = (TextView)findViewById(R.id.tvDescription);
        tvDescription.setText(pref.getKeyData(TopprUtils.DESCRIPTION));

    }

    private void downloadAndSetImage(final String _url) {
        new AsyncTask<Void, Void, Bitmap >()
        {
            @Override
            protected Bitmap  doInBackground(Void... params) {
                Bitmap bmp = null;
                URL url;
                try {
                    url = new URL(_url);
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    bmp = BitmapFactory.decodeStream(url
                            .openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return bmp;

            }

            @Override
            protected void onPostExecute(Bitmap  result) {
                if(result!=null)
                {
                    ImageView imageView = (ImageView) findViewById(R.id.ivImage);
                    if(imageView!=null)
                    {
                        imageView.setBackgroundResource(0);
                        imageView.setImageBitmap(result);
                    }
                }

            }
        }.execute();
    }


    private String getUserId() {
        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
        return pref.getKeyData(TopprUtils.USER_ID);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.ivFav:
            {
                SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
                if(!pref.getKeyData(TopprUtils.IS_FAV).isEmpty())
                {
                    pref.saveKeyData(TopprUtils.IS_FAV,"");
                    view.setBackgroundResource(R.drawable.ic_empty_star);
                    MySQLiteHelper.deleteRowFromFavTable(this,pref.getKeyData(TopprUtils.USER_ID),pref.getKeyData(TopprUtils.ID));
                }
                else
                {
                    pref.saveKeyData(TopprUtils.IS_FAV,"1");
                    view.setBackgroundResource(R.drawable.ic_full_star);
                    EventDO event = new EventDO();
                    event.setFav(true);
                    event.setId(pref.getKeyData(TopprUtils.ID));
                    event.setName(pref.getKeyData(TopprUtils.NAME));
                    event.setImage(pref.getKeyData(TopprUtils.IMAGE));
                    event.setCategory(pref.getKeyData(TopprUtils.CATEGORY));
                    event.setExperience(pref.getKeyData(TopprUtils.EXPERIENCE));
                    event.setDescription(pref.getKeyData(TopprUtils.DESCRIPTION));
                    MySQLiteHelper.insertRowToFavoriteTable(this,event,pref.getKeyData(TopprUtils.USER_ID));
                }
                break;
            }
            case R.id.btnShare:
            {
                takeScreenShotAndShare();
                break;
            }
        }
    }

    public  void takeScreenShotAndShare()
    {
        //region create screenshot
        View mainView = getWindow().getDecorView().getRootView();

        mainView.setDrawingCacheEnabled(true);
        Bitmap bitmap = mainView.getDrawingCache();//screenshot for background view

        File imageFile = new File(getFilesDir(),"img.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        }
        catch (Exception e) {
            e.printStackTrace();
            bitmap.recycle();
            mainView.setDrawingCacheEnabled(false);
            return;
        }
        finally {
            mainView.setDrawingCacheEnabled(false);
        }
        //endregion
        //region Share with apps
        Uri screenshotUri = FileProvider.getUriForFile(
                this,
                "com.archer.toppr_c",
                imageFile);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome event!");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingIntent, "Share using.."));
        //endregion
    }
}