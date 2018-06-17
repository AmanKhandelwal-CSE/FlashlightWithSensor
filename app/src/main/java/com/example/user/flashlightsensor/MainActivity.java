package com.example.user.flashlightsensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;




 public class MainActivity extends Activity
 {
     long lastUpdatetime=0;
     ToggleButton lightButton;
     SharedPreferences sharedPreferences,shortcutPreferences;
     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         lastUpdatetime =System.currentTimeMillis();
         lightButton = (ToggleButton)findViewById(R.id.lightbutton);
         sharedPreferences = getSharedPreferences("light",MODE_PRIVATE);
         shortcutPreferences=getSharedPreferences("Short cut",MODE_PRIVATE);
         if(!shortcutPreferences.getBoolean("Short cut",false))
         {
             createShortcut(this);
             SharedPreferences.Editor editor = shortcutPreferences.edit();
             editor.putBoolean("Short cut",true);
             editor.commit();
         }
         if(sharedPreferences.getInt("light",0)==1)
            lightButton.setChecked(true);
         lightButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 SharedPreferences.Editor editor = sharedPreferences.edit();
                 if(sharedPreferences.getInt("light",0)==0)
                 editor.putInt("light",1);
                 else
                     editor.putInt("light",0);
                 editor.commit();
             }
         });
          startService(new Intent(this,MainActivity1.class));
     }

     public void createShortcut(Context context)
     {
         Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
         shortcutIntent.setAction(Intent.ACTION_MAIN);
         shortcutIntent.putExtra("duplicate", false);
         shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Flash light");
         Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher_background);
         shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
         shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), MainActivity.class));
         context.sendBroadcast(shortcutIntent);

     }
 }


