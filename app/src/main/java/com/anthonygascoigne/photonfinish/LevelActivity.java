package com.anthonygascoigne.photonfinish;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.graphics.Rect;

public class LevelActivity extends Activityimplements View.OnLayoutChangeListener, Panel.OnMenuTriggerListener
{
	static final boolean DEV = false;
	Panel pv;
	
	public static final int PAUSE = 0;
	public static final int CREATE = 1;
	public static final int RESUME = 2;
	public static final int DESTROY = 3;
	public static final int START = 4;
	public static final int STOP = 4;
	public static int event;

	// public static final int MENU_MOVE = 0;
	public static final int MENU_REFLECTOR = 0;
	public static final int MENU_RAY = 1;
	public static final int MENU_TARGET = 2;
	public static final int MENU_DELETE = 5;
	public static final int MENU_SAVE = 6;
	
	public static int NUM_GRID_ROWS = 4;
 
 LightGame game;
	LightModel light;

	RelativeLayout fl;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		// Reflector.deleteAllReflectors();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		light = LightModel.getInstance();
		
		
		Rect windowBounds = getWindowManager().getCurrentWindowMetrics().getBounds()	;
		
		float aspectRatio = (float)windowBounds.width()/windowBounds.height();
		
		if (aspectRatio > (float)1.7)
			NUM_GRID_ROWS = 4;
		else
			NUM_GRID_ROWS = 5;

		//AG can setaxislength and setboundingrect be done together?
		light.setAxisLength(getWindowManager().getCurrentWindowMetrics().getBounds().height()/NUM_GRID_ROWS);

		pv = new Panel(this, light);
		pv.addOnLayoutChangeListener(this);
	
		
		try {
		    Method setLayerTypeMethod = pv.getClass().getMethod("setLayerType", new Class[] {int.class, Paint.class});
		    
				setLayerTypeMethod.invoke(pv, new Object[] {View.LAYER_TYPE_SOFTWARE, null});
			
		} catch (NoSuchMethodException e)
		{
		    // Older OS, no HW acceleration anyway
		} catch (IllegalArgumentException e)
		{
		    e.printStackTrace();
		} catch (IllegalAccessException e)
		{
		    e.printStackTrace();
		} catch (InvocationTargetException e)
		{
		// TODO Auto-generated catch block
	    	e.printStackTrace();
	    }

		light.setBoundingRect(pv);







		game = LightGame.getInstance();
		

		game.setActivity(this);
		game.setPanel(pv);
		if (DEV)
		{
			game.copyAssets();
		}
	
		
		Intent intent = getIntent();
		int level  = intent.getIntExtra("EXTRA_MESSAGE",0);

		if (savedInstanceState == null)
		{
			game.loadLevel(level);
		}
		

	initComponents();
		

		/************************************************************************/

	}
	
	
	public void onPause()
	{
		super.onPause();
		game.saveGameData();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		if (DEV)
		{
			menu.add(Menu.NONE, MENU_REFLECTOR, 1, "ref");
		menu.add(Menu.NONE, MENU_RAY, 2, "ray");
		menu.add(Menu.NONE, MENU_TARGET, 3, "target");
	
		menu.add(Menu.NONE, MENU_DELETE, 6, "delete");
		menu.add(Menu.NONE, MENU_SAVE, 7, "save");
		menu.add(Menu.NONE, 8, 8, "del sel");
		menu.add(Menu.NONE, 9, 9, "Dev");
		menu.add(Menu.NONE, 10, 10, "Play");
		menu.add(Menu.NONE, 11, 11, "Free");
		menu.add(Menu.NONE, 12, 12, "Reset Level");
		menu.add(Menu.NONE, 13, 13, "Copy to SD Card");
		menu.add(Menu.NONE, 14, 14, "Show solution");
		menu.add(Menu.NONE, 15, 15, "Save solution");
		menu.add(Menu.NONE, 16, 16, "Help");
	
		}
		else
		{
			menu.add(Menu.NONE, 12, 0, "Reset Level");
			menu.add(Menu.NONE, 14, 1, "Show solution");
			menu.add(Menu.NONE, 16, 2, "Help");
		}

		return true;
	}

	
	private boolean popItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case MENU_REFLECTOR:
			if (game.getMode() != LightGame.PLAY)
			{
			LightModel.addReflector();
				pv.invalidate();
			}
			return true;
		case MENU_RAY:
			if (game.getMode() != LightGame.PLAY)
			{
				light.addRay();
				pv.invalidate();
			}
			return true;
		case MENU_TARGET:
			if (game.getMode() != LightGame.PLAY)
			{
				light.createTarget();
				pv.invalidate();
			}
			return true;
		/*case MENU_PREV:

			game.prevLevel();
			pv.invalidate();

			return true;*/
		
		case MENU_DELETE:

			if (game.getMode() != LightGame.PLAY)
			{
				light.deleteAll();
				light.setEdited(true);
				pv.invalidate();
			}

			return true;
		case MENU_SAVE:
			light.setEdited(false);
			game.saveLevel(false);
			pv.invalidate();
			return true;

		case 8:
			light.deleteSelected();
			pv.invalidate();
			return true;
		case 9:

			game.setGameMode(LightGame.SET_LEVEL);

			initComponents();
		//	game.loadLevel(0);
			pv.invalidate();
			return true;
		case 10:

			game.setGameMode(LightGame.PLAY);

			initComponents();
		//	game.loadLevel(0);
			pv.invalidate();
			return true;

		case 11:

			game.setGameMode(LightGame.FREE);

			initComponents();
			//
			game.resetLevel();
			pv.invalidate();
			return true;
		case 12:

			game.resetLevel();
			pv.invalidate();
			return true;
		case 13:
			
			game.copyfilesToSdCard();
			return true;
		case 14:
			game.loadLevelSolution();
			light.updateModel();
			pv.invalidate();
			return true;
		case 15:
			light.setEdited(false);
			game.saveLevel(true);
			pv.invalidate();
			return true;
		case 16:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Drag the reflectors into the beam to guide the laser through the targets. \nThe level is complete when all targets are activated. \n\n"+ 
					"White targets can be activated by any beam. Coloured targets can only be activated by a beam of matching colour. \n\nGreen reflectors rotate about their mid-point. Press to select and then move your finger away as if to drag the reflector. This will cause it to rotate to point to your finger.\n White reflectors do not move\n\n "+
					"Tapping the arrow in the top right corner of the screen will cause each reflector to be selected, in turn, from left to right. Drag on any empty space on the screen, and the selected reflector will follow. This is useful when a reflector is difficult to select due to being to close to other objects or if the screen is overcrowded").setCancelable(true)
					.setPositiveButton("Ok", null);
			AlertDialog alert = builder.create();
			alert.show();
        
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void levelComplete()
	{

		game.levelComplete();

	}

	private void initComponents()
	{
		fl = new RelativeLayout(this);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

		if (pv.getParent() != null)
		{

			((RelativeLayout) pv.getParent()).removeView(pv);
		}
		fl.addView(pv, params);
		
		
		if (game.getMode() != LightGame.PLAY)
		{

			/*********** buttonRaySelect ******************/
			Button buttonRaySelect = new Button(this);
			buttonRaySelect.setId(11);
			buttonRaySelect.setText("Ray");
			//buttonRaySelect.setPadding(left, top, right, bottom)
			buttonRaySelect.setMaxWidth(light.AXIS_LENGTH);
			buttonRaySelect.setMinWidth(light.AXIS_LENGTH);

			buttonRaySelect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					light.cycleRaySelection(false);
					pv.invalidate();
				}
			});

			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			//params.addRule(RelativeLayout.BELOW, viewPadding.getId());

			fl.addView(buttonRaySelect, params);

			/*********** button Source Select ******************/
			Button buttonSourceSelect = new Button(this);
			buttonSourceSelect.setId(12);
			buttonSourceSelect.setText("Souce");
			buttonSourceSelect.setMaxWidth(light.AXIS_LENGTH);
			buttonSourceSelect.setMinWidth(light.AXIS_LENGTH);

			buttonSourceSelect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					light.cycleRaySelection(true);
					pv.invalidate();
				}
			});

			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, buttonRaySelect.getId());
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

			fl.addView(buttonSourceSelect, params);

			/*********** button select target ******************/
			Button buttonTargetSelect = new Button(this);
			buttonTargetSelect.setId(13);
			buttonTargetSelect.setText("Target");
			buttonTargetSelect.setMaxWidth(light.AXIS_LENGTH);
			buttonTargetSelect.setMinWidth(light.AXIS_LENGTH);

			buttonTargetSelect.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					light.cycleTargetSelection();
					pv.invalidate();
				}
			});

			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, buttonSourceSelect.getId());
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

			fl.addView(buttonTargetSelect, params);

			/**************** buttonDeleteSeleted **********************************/
			/*
			 * Button buttonDeleteSeleted =new Button(this);
			 * buttonDeleteSeleted.setId(14);
			 * buttonDeleteSeleted.setText("Del");
			 * buttonDeleteSeleted.setMinimumWidth(100); //
			 * buttonDeleteSeleted.setAlpha((float) 0.5);
			 * 
			 * 
			 * buttonDeleteSeleted.setOnClickListener(new View.OnClickListener()
			 * { public void onClick(View v) { pv.deleteSeleted(); } });
			 * 
			 * 
			 * params =new
			 * RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
			 * LayoutParams.WRAP_CONTENT);
			 * params.addRule(RelativeLayout.BELOW,buttonTargetSelect.getId() );
			 * params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
			 * RelativeLayout.TRUE);
			 * 
			 * fl.addView(buttonDeleteSeleted, params);
			 */

			/*********** buttonRotate ******************/
			ToggleButton buttonRotate = new ToggleButton(this);
			buttonRotate.setId(15);
			buttonRotate.setText("Rot");
			buttonRotate.setMaxWidth(light.AXIS_LENGTH);
			buttonRotate.setMinWidth(light.AXIS_LENGTH);

			buttonRotate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					light.setRotateMode(((ToggleButton)v).isChecked());
				}
			});

			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, buttonTargetSelect.getId());
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

			fl.addView(buttonRotate, params);

			/*********** buttonTargetId ******************/
			Button buttonTargetId = new Button(this);
			buttonTargetId.setId(16);
			buttonTargetId.setText("ID");
			buttonTargetId.setMaxWidth(light.AXIS_LENGTH);
			buttonTargetId.setMinWidth(light.AXIS_LENGTH);

			buttonTargetId.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					light.setTargetId();
					pv.invalidate();
				}
			});

			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, buttonRotate.getId());
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

			fl.addView(buttonTargetId, params);


			/*********** buttonSetRotate ******************/
			
			  Button buttonSetRotate =new Button(this);
			  buttonSetRotate.setId(17);
			  buttonSetRotate.setText("Set Rot"); 
			  buttonSetRotate.setMaxWidth(light.AXIS_LENGTH);
			  buttonSetRotate.setMinWidth(light.AXIS_LENGTH);
			  
			  buttonSetRotate.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v)
			  { 
				  int type = light.getSelectedRefType();
				  
				  if (type != -1)
				  {
				  light.setSelectedRefType(++type);
				  pv.invalidate();
				  }
			  } });
			  
			  params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.RIGHT_OF, buttonTargetId.getId());
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			  
			  
			  
			  fl.addView(buttonSetRotate, params);
			 

		}

		setContentView(fl);
		/*****************************************************/
	}
	


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom)
        
    {
    	
    	int newWidth = right - left;
    	int newHeight = bottom - top;
    	
    	WindowInsetsController controller = getWindow().getInsetsController();
// Hide both the status bar and navigation bar

              if (controller != null)
	              {
                    controller.hide(WindowInsets.Type.systemBars());
                    // Allow the user to swipe from edges to show bars transiently without resizing layout
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
	              }    	
    
  }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) 
    {
        super.onWindowFocusChanged(hasFocus);
        
        // If the app is coming back into focus and we want it full screen, enforce it now
        if (hasFocus) 
        	{
           	WindowInsetsController controller = getWindow().getInsetsController();
// Hide both the status bar and navigation bar

              if (controller != null)
	              {
                 //   controller.hide(WindowInsets.Type.systemBars());
                    // Allow the user to swipe from edges to show bars transiently without resizing layout
                //    controller.setSystemBarsBehavior(
                         //   WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
	              }
        }
    }

    @Override
    public void onMenuTriggered(View anchorView, ViewGroup viewGroupParent) 
    {
    	   PopupMenu popup = new PopupMenu(this, anchorView);
    	   // final ViewGroup parentViewGroup = (ViewGroup) getParent()        Menu menu = popup.getMenu();    	
        onCreateOptionsMenu(popup.getMenu());
        
        // 5. Clean up tracking views from memory when closed to prevent leaks
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
              //  if (parentViewGroup != null) {
                   viewGroupParent.removeView(anchorView);
             //   }
            }
        });

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
             {
               // if (item.getItemId() == 1) {
                   // Toast.makeText(getContext(), "Selected Option One", Toast.LENGTH_SHORT).show();
                    return popItemSelected(item);
            }
        });        
        popup.show();
    }
}