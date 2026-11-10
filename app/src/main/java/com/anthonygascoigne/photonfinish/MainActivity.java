package com.anthonygascoigne.photonfinish;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity  extends Activity
{
	
	ImageAdaptor ad;
	@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    int paddingInDp = 30; // Your resolution-independent length

    int paddingInPx = (int) TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, 
    paddingInDp, 
    this.getResources().getDisplayMetrics());
    
    int screenHeight = getResources().getDisplayMetrics().heightPixels;		
    LightGame.getInstance().setGameMode(LightGame.DEFAULT_MODE);
    
    GridView gv = new GridView(this);
    
    gv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    
    gv.setNumColumns(5);
    gv.setHorizontalSpacing(paddingInPx);
    gv.setVerticalSpacing(paddingInPx);
    gv.setGravity(Gravity.BOTTOM);
    gv.setBackgroundColor(Color.BLACK);
    
    LevelPanel.setPanelSize((screenHeight / 3) - (paddingInPx));

    ad = new ImageAdaptor(this);
    gv.setAdapter(ad);
        
    gv.setOnItemClickListener(new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
        {
            if (((LevelPanel) v).isEnabled())
            {
                Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                intent.putExtra("EXTRA_MESSAGE", position);
                startActivity(intent);
            }
        }
    });

    LightGame.getInstance().setContext(this);
    LightGame.getInstance().loadGameData();
    
    // 2. SAFE REFUSE/REFRESH CALL
    if (ad != null) {
        ad.refreshViews();
    }
    
    setContentView(gv);
    
    // Immersive Fullscreen handling
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        gv.post(new Runnable() {
            @Override
            public void run() {
                android.view.WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(android.view.WindowInsets.Type.statusBars() | 
                                    android.view.WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
        });
    }
}

	public void onResume()
	{
		super.onResume();
		
		if (ad != null)
		        {
		ad.refreshViews();
		    }
	}


}
