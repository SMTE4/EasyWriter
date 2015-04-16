package com.kargathia.easywriter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kargathia on 04/04/2015.
 */
public class DrawingView extends View {
    private static final Object READING_LOCK = "";

    private static boolean isTessInit = false;
    private static TessBaseAPI baseAPI;
    private static String dataPath;

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF000000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap = null;
    private TextView display = null;
    private String recognisedText = "unchanged";
    private boolean
            isReading = false,
            isDrawing = false;
//    private String dataPath;
//    private TessBaseAPI baseApi;

    private Context context;

    public String getRecognisedText(){
        return this.recognisedText;
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        this.context = context;


    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setOutput(TextView display) {
        this.display = display;
        this.setOutputText(" ");

            if(!isTessInit){
                isTessInit = true;
                this.copyAssets();
                baseAPI = new TessBaseAPI();
                baseAPI.init(dataPath, "eng");
            }
    }

    public boolean resetCanvas(){
        if(isDrawing){
            canvasBitmap.eraseColor(Color.WHITE);
            setOutputText(" ");
            isDrawing = false;
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        // reboots canvas, recognising gestures
        // I literally have no idea why this works.
        isDrawing = true;
        resetCanvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        synchronized(READING_LOCK){
            if(isReading){
                setOutputText(detectText(canvasBitmap));
                isReading = false;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                isReading = true;
                isDrawing = true;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }


    private String detectText(Bitmap bitmap) {
        baseAPI.setImage(bitmap);
        String output = baseAPI.getUTF8Text();
        Log.i("recognisedText", output);
        return output;
    }

    private void setOutputText(String input){
        this.recognisedText = input;
        if(recognisedText.trim().isEmpty()){
            display.setText("[SPACE]");
        } else {
            display.setText(recognisedText);
        }
//        display.append(" >");
    }

    /**
     * Copies tessdata assets to internal phone memory, as accessing is done by path.
     */
    private void copyAssets() {
        context = getContext();

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        File dir = null;
        try {
            files = assetManager.list("tessdata");
            dir = new File(context.getFilesDir(), "tessdata");
            dir.mkdir();
            dataPath = context.getFilesDir().getPath();
            Log.i("datapath", dataPath);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            Log.i("filename", filename);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("tessdata/" + filename);
                File outFile = new File(dir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


}