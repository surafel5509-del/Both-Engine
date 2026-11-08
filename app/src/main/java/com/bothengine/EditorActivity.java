package com.bothengine;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * EditorActivity
 * Activity for the Both Engine editor
 */
public class EditorActivity extends Activity {
    private static final String TAG = "EditorActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "EditorActivity onCreate");
        
        // Request fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Set content view
        // In the future, this would be a custom editor UI
        setContentView(new android.widget.TextView(this) {{
            setText("Both Engine Editor\n(Coming Soon)");
            setTextSize(24);
            setGravity(android.view.Gravity.CENTER);
        }});
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "EditorActivity onResume");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "EditorActivity onPause");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "EditorActivity onDestroy");
    }
}
