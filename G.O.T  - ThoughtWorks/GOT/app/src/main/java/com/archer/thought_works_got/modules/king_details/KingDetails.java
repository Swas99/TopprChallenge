package com.archer.thought_works_got.modules.king_details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.archer.thought_works_got.R;
import com.archer.thought_works_got.data_model.sql_db_models.KingsDO;
import com.archer.thought_works_got.util.GOT_Util;
import com.archer.thought_works_got.util.MySQLiteHelper;

import java.io.File;
import java.io.FileOutputStream;

public class KingDetails
        extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_king_details);
        getSupportActionBar().setTitle("Battle Stats");
        init();
    }

    private void init() {

        KingsDO data = MySQLiteHelper.getKingData(this,getIntent().getStringExtra(GOT_Util.KING_NAME));
        if(data==null)
            return;

        ImageView ivKing = (ImageView) findViewById(R.id.ivKing);
        TextView tvKingName = (TextView) findViewById(R.id.tvKingName);
        TextView tvRank = (TextView) findViewById(R.id.tvRank);
        TextView tvRating = (TextView) findViewById(R.id.tvRating);
        TextView tvBattlesWon = (TextView) findViewById(R.id.tvBattlesWon);
        TextView tvTotalBattles = (TextView) findViewById(R.id.tvTotalBattles);
        TextView tvBestRank = (TextView) findViewById(R.id.tvBestRank);
        TextView tvWorstRank = (TextView) findViewById(R.id.tvWorstRank);
        TextView tvHighestRating = (TextView) findViewById(R.id.tvHighestRating);
        TextView tvLowestRating = (TextView) findViewById(R.id.tvLowestRating);
        TextView tvStrength = (TextView) findViewById(R.id.tvStrength);
        TextView tvBattlesLost = (TextView) findViewById(R.id.tvBattlesLost);


        String display_text = "<u>" + data.getName() + "</u>";
        tvKingName.setText(Html.fromHtml(display_text));
        switch (data.getName())
        {
            case GOT_Util.MANCE:
                ivKing.setImageResource(R.drawable.ic_mance_rayder);
                break;
            case GOT_Util.BALON:
                ivKing.setImageResource(R.drawable.ic_balon);
                break;
            case GOT_Util.RENLY:
                ivKing.setImageResource(R.drawable.ic_renly);
                break;
            case GOT_Util.JOFFREY:
                ivKing.setImageResource(R.drawable.ic_joffrey);
                break;
            case GOT_Util.STANNIS:
                ivKing.setImageResource(R.drawable.ic_stannis);
                break;
            case GOT_Util.ROB_STARK:
                ivKing.setImageResource(R.drawable.ic_robb_stark);
                break;
        }
        display_text = "Rank: " + data.getCurrentRank();
        tvRank.setText(display_text);
        display_text = data.getCurrentRating();
        if(display_text.indexOf(".")>0)
            display_text = display_text.substring(0,display_text.indexOf("."));
        display_text = "Rating: " + display_text;
        tvRating.setText(display_text);
        display_text = "Total battles: " + data.getTotalBattles();
        tvTotalBattles.setText(display_text);
        display_text = "Battles won: " + data.getBattlesWon();
        tvBattlesWon.setText(display_text);

        display_text = "Best rank: " + data.getTopRank();
        tvBestRank.setText(display_text);
        display_text = "Worst rank: " + data.getWorstRank();
        tvWorstRank.setText(display_text);
        display_text = "Highest rating: " + data.getHighestRating();
        tvHighestRating.setText(display_text);
        display_text = "Lowest rating: " + data.getLowestRating();
        tvLowestRating.setText(display_text);
        display_text = "Battles lost: " + data.getBattlesLost();
        tvBattlesLost.setText(display_text);

        int x = MySQLiteHelper.getCountOfBattlesWonAttacking(this,data.getName());
        int y = MySQLiteHelper.getCountOfBattlesWonDefending(this,data.getName());
        display_text = "Battles won attacking = " + String.valueOf(x) +
                "\nBattles won defending = " + String.valueOf(y) + "\n";

        if(x>y)
            display_text+= "Strength: Attack";
        else if(x<y)
            display_text+= "Strength: Defend";
        tvStrength.setText(display_text);



        findViewById(R.id.btnShare).setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

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
                "com.archer.thought_works_got",
                imageFile);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Check out these stats");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingIntent, "Share using.."));
        //endregion
    }
}