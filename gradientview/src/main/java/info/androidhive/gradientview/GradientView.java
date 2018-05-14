package info.androidhive.gradientview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Reference:
 * https://stackoverflow.com/questions/26074784/how-to-make-a-view-in-android-with-rounded-corners
 */

public class GradientView extends LinearLayout {
    private Bitmap maskBitmap;
    private Paint maskPaint;
    private int viewWidth, viewHeight, gradientAngle;
    private int startColor, centreColor, endColor;
    private int[] colorPalette;
    private String gradientType;
    private float radialGradientRadius, cornerRadius;
    private boolean firstTimeDraw = true;
    private int calcHeight = 0;

    public GradientView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public GradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GradientView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GradientView, 0, 0);

        // receiving integers values
        gradientAngle = a.getInteger(R.styleable.GradientView_gradient_angle, getResources().getInteger(R.integer.default_gradient_angle));
        cornerRadius = a.getInteger(R.styleable.GradientView_corner_radius, getResources().getInteger(R.integer.default_corner_radius));

        // receiving string value
        // gradient type linear, radial or sweep
        gradientType = a.getString(R.styleable.GradientView_gradient_type);

        // receiving color values
        startColor = a.getColor(R.styleable.GradientView_start_color, -1);
        centreColor = a.getColor(R.styleable.GradientView_center_color, -1);
        endColor = a.getColor(R.styleable.GradientView_end_color, -1);


        // receiving dimen value
        radialGradientRadius = a.getDimensionPixelSize(R.styleable.GradientView_gradient_radius, -1);

        // receiving array value
        int arrayId = a.getResourceId(R.styleable.GradientView_color_palette, 0);
        if (arrayId != 0) {
            colorPalette = getResources().getIntArray(arrayId);
        }

        a.recycle();

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // view width and height
        viewWidth = xNew;
        viewHeight = yNew;
    }

    @Override
    public void draw(Canvas canvas) {
        if (firstTimeDraw) {
            //Iterating on the remaining children of relative layout to calculate the height
            for (int i = 1; i < ((ViewGroup) getParent()).getChildCount(); i++) {
                calcHeight += ((ViewGroup) getParent()).getChildAt(i).getHeight();
            }
            setMinimumHeight(calcHeight);
            firstTimeDraw = false;
        }
        //To make sure calcHeight is greater than 0
        calcHeight = calcHeight > 0 ? calcHeight : 1;
        Bitmap offscreenBitmap = Bitmap.createBitmap(canvas.getWidth() > 0 ? canvas.getWidth() : 1,
                calcHeight,
                Bitmap.Config.ARGB_8888);
        Canvas offscreenCanvas = new Canvas(offscreenBitmap);

        Paint paint = new Paint();

        // gradient direction angle
        double angleInRadians = Math.toRadians(gradientAngle);

        // generating end X, Y coordinates considering angle
        float endX = (float) (Math.cos(angleInRadians) * viewWidth);
        float endY = (float) (Math.sin(angleInRadians) * viewHeight);

        prepareColorPalette();

        // Gradient needs at least two colors
        if (colorPalette.length > 1) {
            Shader shader = getShader(endX, endY);
            paint.setShader(shader);
        }

        offscreenCanvas.drawPaint(paint);

        super.draw(offscreenCanvas);

        if (maskBitmap == null) {
            maskBitmap = createMask(canvas.getWidth(), calcHeight);
        }

        offscreenCanvas.drawBitmap(maskBitmap, 0f, 0f, maskPaint);

        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint);
    }

    /**
     * returns gradient type
     * linear, radial or sweep
     * TODO - room for improvement (start, end XY coordinates)
     */
    private Shader getShader(float endX, float endY) {
        // checking for gradient type
        if (gradientType != null && gradientType.equals(getResources().getString(R.string.gradient_radial))) {
            if (radialGradientRadius == -1) {
                // keeping default gradient radius to half of the view width
                radialGradientRadius = viewWidth / 2;
            }

            // radial gradient
            return new RadialGradient(viewWidth / 2, viewHeight / 2, radialGradientRadius, colorPalette, null, Shader.TileMode.CLAMP);

        } else if (gradientType != null && gradientType.equals(getResources().getString(R.string.gradient_sweep))) {

            // sweep gradient
            return new SweepGradient(viewWidth / 2, viewHeight / 2, colorPalette, null);

        } else {

            // defaults to linear gradient
            return new LinearGradient(0, 0, endX, endY,
                    colorPalette,
                    null, Shader.TileMode.CLAMP);
        }
    }

    /**
     * Checks for start, center and end colors
     * set from xml layout
     */
    private void prepareColorPalette() {
        if (colorPalette == null) {
            List<Integer> colors = new ArrayList<>();
            if (startColor != -1) {
                colors.add(startColor);
            }

            if (centreColor != -1) {
                colors.add(centreColor);
            }

            if (endColor != -1) {
                colors.add(endColor);
            }
            colorPalette = toIntArray(colors);
        }
    }

    int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);
        return ret;
    }

    /**
     * Creating masked bitmap with rounded corners
     */
    private Bitmap createMask(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mask);

        Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
        eraser.setColor(Color.WHITE);

        canvas.drawRect(0, 0, width, height, eraser);

        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, eraser);

        return mask;
    }

    // TODO - room for improvements
    public void setStartColor(int color) {
        if (color == -1) {
            startColor = -1;
            colorPalette = null;
            invalidate();
            return;
        }
        startColor = color;
        if (colorPalette != null && colorPalette.length > 0)
            colorPalette[0] = startColor;

        invalidate();
    }

    public void setCentreColor(int color) {
        if (color == -1) {
            centreColor = -1;
            colorPalette = null;
            invalidate();
            return;
        }

        centreColor = color;
        if (colorPalette != null && colorPalette.length > 1)
            colorPalette[1] = centreColor;

        invalidate();
    }

    public void setEndColor(int color) {
        if (color == -1) {
            endColor = -1;
            colorPalette = null;
            invalidate();
            return;
        }
        endColor = color;
        if (colorPalette != null && colorPalette.length > 2)
            colorPalette[2] = endColor;

        invalidate();
    }

    /**
     * renders the gradient with new color palette
     */
    public void setColorPalette(int[] colorPalette) {
        this.colorPalette = colorPalette;

        invalidate();
    }
}
