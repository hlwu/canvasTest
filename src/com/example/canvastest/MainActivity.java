package com.example.canvastest;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import com.example.canvastest.TopWindowService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

    private DrawView mDrawView = null;
    private Paint mPaint =  null;
    private String mImagePath = null;
    
    private boolean mAlphaSliderEnabled = false;
    private boolean mHexValueEnabled = false;
    
    final int SELECT_IMAGE = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawView = (DrawView) findViewById(R.id.drawView);
        initPaint();

        final WindowManager wm = this.getWindowManager();
        Log.d("flaggg", "MainActivity.onCreate "+wm.getDefaultDisplay().getWidth()+"     "+wm.getDefaultDisplay().getHeight());
//        mDrawView.setBitmap(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
        mDrawView.setmContext(getApplicationContext());
        
        IntentFilter filter = new IntentFilter("com.example.canvastest.click");
        registerReceiver(mReceiver, filter);

        
        //        iv = (ImageView) findViewById(R.id.iv);
        //        saveBtn = (Button) findViewById(R.id.saveBtn);
        //        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        //        saveBtn.setOnClickListener(this);
        //        cancelBtn.setOnClickListener(this);

        //        paint = new Paint();
        //        paint.setColor(Color.WHITE);
        //        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, this.getResources().getDisplayMetrics()));
        //        paint.setAntiAlias(true);
        //        paint.setDither(true);
        //        paint.setStyle(Paint.Style.STROKE);
        //        paint.setStrokeJoin(Paint.Join.ROUND);
        //        paint.setStrokeCap(Paint.Cap.ROUND);
        //        paint.setStrokeWidth(12);
        //        BlurMaskFilter blur  = new BlurMaskFilter(13,BlurMaskFilter.Blur.NORMAL);
        //        paint.setMaskFilter(blur);

        //        ViewTreeObserver vto2 = iv.getViewTreeObserver(); 
        //        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
        //            @Override
        //            public void onGlobalLayout() { 
        //                iv.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
        //            } 
        //        }); 
        //        ViewTreeObserver vto = iv.getViewTreeObserver(); 
        //        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
        //            public boolean onPreDraw() { 
        ////                int height = iv.getMeasuredHeight(); 
        ////                int width = iv.getMeasuredWidth(); 
        ////                Log.d("flaggg", /*wm.getDefaultDisplay().getWidth()+"/n"+wm.getDefaultDisplay().getHeight()
        ////                        +*/"    "+height
        ////                        +"  "+width);
        //                Log.d("flaggg", wm.getDefaultDisplay().getWidth()+"     "+wm.getDefaultDisplay().getHeight()
        //                        +"    "+iv.getMeasuredWidth()
        //                        +"  "+iv.getMeasuredHeight());
        //                return true; 
        //            } 
        //        });


        //        bitmap = Bitmap.createBitmap(iv.getMeasuredWidth(), iv.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        ////        bitmap = Bitmap.createBitmap(wm.getDefaultDisplay().getWidth(), (wm.getDefaultDisplay().getHeight())/8*7, Bitmap.Config.ARGB_8888);
        //        canvas = new Canvas(bitmap);
        //        canvas.drawColor(Color.TRANSPARENT);
        //        canvas.drawBitmap(bitmap, new Matrix(), paint);
        //        iv.setImageBitmap(bitmap);
        //        iv.setOnTouchListener(this);
    }

    @Override
    protected void onStop() {
        Log.d("flaggg", "MainActivity.onStop");
        // TODO Auto-generated method stub
        super.onStop();
        Intent showIntent = new Intent(this, TopWindowService.class);
        showIntent.putExtra(TopWindowService.OPERATION,
                TopWindowService.OPERATION_NOT);
        startService(showIntent);
    }
    
    @Override
    protected void onStart() {
        Log.d("flaggg", "MainActivity.onStart");
        // TODO Auto-generated method stub
        super.onStart();
        
        mDrawView.setNaviBarVisible(this, false);
    }

    @Override
    protected void onDestroy() {
        Log.d("flaggg", "MainActivity.onDestroy");
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("flaggg", "onReceive action : " + action);
            if(action.equals("com.example.canvastest.click")) {
                chooseBackground();
            }
        }
    };
    
    public void chooseBackground()
    {
        Log.d("flaggg", "chooseBackground");
        String[] items = {"select image","solid color", "pen color"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("choose background");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                if(arg1 == 0)
                {
                    pickupLocalImage(SELECT_IMAGE);

                }
                if(arg1 == 1)
                {
                    showColorDialog(null);
                }
                if(arg1 == 2)
                {
                    choosePaint();
                }
            }
        });
        builder.create().show();
    }
    
    protected void pickupLocalImage(int return_num) 
    {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent,return_num);
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    protected void showColorDialog(Bundle state) {

        ColorPickerDialog dialog = new ColorPickerDialog(MainActivity.this, Color.BLACK);
        dialog.setOnColorChangedListener(new BackgroundColorListener());
        if (mAlphaSliderEnabled) {
            dialog.setAlphaSliderVisible(true);
        }
        if (mHexValueEnabled) {
            dialog.setHexValueEnabled(true);
        }
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        dialog.show();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
            case SELECT_IMAGE:
                try{
                    Uri imgUri = data.getData();
                    if(imgUri != null){
                        ContentResolver cr = this.getContentResolver();
                        String[] columnStr = new String[]{MediaStore.Images.Media.DATA};
                        Cursor cursor = cr.query(imgUri, columnStr, null, null, null);
                        if(cursor != null){
                            int nID = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            if(cursor.moveToFirst()){
                                mImagePath = cursor.getString(nID);
                                //Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
                                mDrawView.setBitmap(mImagePath);
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                break;
            default:
                break;
            };
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public class BackgroundColorListener implements ColorPickerDialog.OnColorChangedListener
    {
        public void onColorChanged(int color)
        {
            mDrawView.setBitmapColor(color);
            
        }
    }

    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFCCCCCC);
        //  mPaint.setStyle(Paint.Style.FILL);//起点→路径→终点, 包围起来的图形是填充的
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }
    
    public class PaintChangeListener implements PaintDialog.OnPaintChangedListener
    {
        public void onPaintChanged(Paint paint)
        {
            mDrawView.setPaint(paint);
            mPaint = paint;
        }
    }
    private void choosePaint()
    {
        PaintDialog dialog = new PaintDialog(MainActivity.this);
        dialog.initDialog(dialog.getContext(),mPaint);
        dialog.setOnPaintChangedListener( new PaintChangeListener() );
        
    }
}
