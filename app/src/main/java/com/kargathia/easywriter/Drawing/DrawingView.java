package com.kargathia.easywriter.Drawing;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.kargathia.easywriter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * How to use:
 * - (Optional) provide a TextView displaying individual letters recognised by the OCR engine.  <br>
 * - Use clearCommand() to wipe screen <br>
 * - Use acceptCommand() to wipe screen and get currently read letter <br>
 * No other input is neccessary, screen wiping and OCR API initialisation happens automatically. <br>
 * DrawingView keeps track of the currently (unaccepted) interpreted drawing.
 * If a TextView is provided, it will automatically update read letter. <br>
 * <p/>
 * Created by Kargathia on 04/04/2015.
 */
public class DrawingView extends View {
    private static final Object READING_LOCK = "";

    private static boolean isTessInit = false;
    private static TessBaseAPI baseAPI = null;

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint
            drawPaint,
            canvasPaint;
    //initial color
    private int paintColor = Color.BLACK;
    // canvas color
    private int canvasColor = Color.WHITE;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap = null;
    private TextView tv_display;
    private String
//            fullText = "",
            letterText = "";
    private boolean
            isReading = false,
            isDrawing = false;

    private Context context;

//    public String getFullText() {
//        return this.fullText;
//    }

    public String getLetterText() {
        return this.letterText;
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        this.context = context;

        if (!isTessInit && !isInEditMode()) {
            isTessInit = true;
            new TessInitiator().execute();
        }
    }

    /**
     * Initialises all variables to do with painting
     */
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

    /**
     * Wipes the canvas, setting it fully to canvasColor
     * @return false it canvas was already clear.
     */
    private boolean resetCanvas() {
        if (isDrawing) {
            canvasBitmap.eraseColor(canvasColor);
            letterText = "";
            isDrawing = false;
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Declares what textview should show interpreted letters
     *
     * @param display
     */
    public void setLetterDisplay(TextView display) {
        this.tv_display = display;
    }

    /**
     * Orders drawingView to clear screen
     *
     * @return true if screen was non-empty
     */
    public boolean clearCommand() {
        return this.resetCanvas();
    }

    /**
     * Accepts currently drawn letter, or adds a space if canvas was empty
     *
     * @return full text
     */
    public String acceptCommand() {
        String output = " ";
        if (!letterText.trim().isEmpty()) {
            output = this.letterText;
        }
        this.resetCanvas();
        return output;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        // reboots canvas
        isDrawing = true;
        resetCanvas();
    }

    /**
     * View is invalidated whenever user draws, calling this for interpretation.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        synchronized (READING_LOCK) {
            if (isReading) {
                letterText = detectText(canvasBitmap);
                isReading = false;
            }
            if (tv_display != null) {
                if (letterText == null) {
                    tv_display.setText(context.getString(R.string.loadingprompt));
                    letterText = "";
                } else if (letterText.isEmpty()) {
                    tv_display.setText(context.getString(R.string.whitespaceprompt));
                } else {
                    tv_display.setText(letterText);
                }
            }
        }
    }

    /**
     * Interprets touch events as drawing.
     * @param event
     * @return
     */
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

    /**
     * Calls on library to interpret given bitmap
     * @param bitmap
     * @return
     */
    private String detectText(Bitmap bitmap) {
        if (baseAPI == null) {
//            Log.i("detectText", "returning null");
            return null;
        }
        baseAPI.setImage(bitmap);
        String output = baseAPI.getUTF8Text();
//        Log.i("fullText", output);
        return output;
    }

    /**
     * Performs heavy lifting of initiating the library async.
     */
    private class TessInitiator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            TessBaseAPI api = new TessBaseAPI();
            api.init(copyAssets(), "eng");
            baseAPI = api;
            return null;
        }

        /**
         * Copies tessdata assets to internal phone memory, as accessing is done by path.
         */
        private String copyAssets() {
            context = getContext();
            String dataPath = null;

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
            for (String filename : files) {
//            Log.i("filename", filename);
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open("tessdata/" + filename);
                    File outFile = new File(dir, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
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
            return dataPath;
        }

        /**
         * Copies individual file
         *
         * @param in
         * @param out
         * @throws IOException
         */
        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }


}