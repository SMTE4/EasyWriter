package com.kargathia.easywriter;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.widget.TextView;

import net.sourceforge.tess4j.ITessAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
/**
 * Created by Kargathia on 04/04/2015.
 */
public class DrawingView extends View {
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
    private TextView display;
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }
    public void setOutput(TextView display) {
        this.display = display;
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
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
// display.setText(this.detectText(canvasBitmap));
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
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
//    private String detectText(Bitmap bitmap) {
//        String path = "assets/";
//        File tessdata = new File(path + "tessdata");
//        Log.i("fileinfo", String.valueOf(tessdata.isDirectory()));
//// try {
//// File trainData = new File(path);
//// if (trainData.length() == 0) {
//// InputStream in = getResources().openRawResource(R.raw.eng);
//// FileOutputStream out = new FileOutputStream(path);
//// byte[] buff = new byte[1024];
//// int read = 0;
////
//// try {
//// while ((read = in.read(buff)) > 0) {
//// out.write(buff, 0, read);
//// }
//// } finally {
//// in.close();
//// out.close();
//// }
//// }
//// } catch (Exception ex) {
//// Log.i("error", "exception has occured in detectText: " + ex.getMessage());
//// }
//        String tag = "detectText";
//        ITessAPI.TessBaseAPI baseApi = new ITessAPI.TessBaseAPI();
//        Log.i(tag, "baseAPI started");
//// InputStream input = getContext().getResources().openRawResource(R.raw.eng);
//// Log.i("path", String.valueOf(assets.listFiles().length));
//        baseApi.init(path, "eng");
//        Log.i(tag, "baseAPI init");
//        baseApi.setImage(bitmap);
//        String recognizedText = baseApi.getUTF8Text();
//        baseApi.end();
//        return recognizedText;
//    }
}