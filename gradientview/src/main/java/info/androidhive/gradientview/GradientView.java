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
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class GradientView extends FrameLayout {
    private Bitmap maskBitmap;
    private Paint paint, maskPaint;
    private float cornerRadius;
    private int viewWidth, viewHeight, gradientAngle;
    private int startColor, centreColor, endColor;
    private int[] cs;
    private String gradientType;
    private float radialGradientRadius;

    // TODO - add these features
    // gradient direction
    // linear and radial and SweepGradient
    // array of colors
    // start, middle, end colors
    // start position, end position
    // Random color - optional
    // Test Project with CardView, RecyclerView, Random Colors

    // References
    // https://stackoverflow.com/questions/26074784/how-to-make-a-view-in-android-with-rounded-corners

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

        gradientAngle = a.getInteger(R.styleable.GradientView_gradient_angle, getResources().getInteger(R.integer.default_gradient_angle));
        gradientType = a.getString(R.styleable.GradientView_gradient_type);
        cornerRadius = a.getInteger(R.styleable.GradientView_corner_radius, getResources().getInteger(R.integer.default_corner_radius));
        startColor = a.getColor(R.styleable.GradientView_start_color, -1);
        centreColor = a.getColor(R.styleable.GradientView_center_color, -1);
        endColor = a.getColor(R.styleable.GradientView_end_color, -1);
        radialGradientRadius = a.getDimensionPixelSize(R.styleable.GradientView_gradient_radius, -1);

        int arrayId = a.getResourceId(R.styleable.GradientView_color_palette, 0);

        if (arrayId != 0) {
            cs = getResources().getIntArray(arrayId);
        }

        a.recycle();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap offscreenBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas offscreenCanvas = new Canvas(offscreenBitmap);

        Paint paint = new Paint();


        double angleInRadians = Math.toRadians(gradientAngle);

        float endX = (float) (Math.cos(angleInRadians) * viewWidth);
        float endY = (float) (Math.sin(angleInRadians) * viewHeight);

        if (cs == null) {
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

            cs = toIntArray(colors);
        }

        if (cs.length > 1) {
            Shader mShader;

            if (gradientType != null && gradientType.equals(getResources().getString(R.string.gradient_radial))) {
                if (radialGradientRadius == -1) {
                    radialGradientRadius = viewWidth / 2;
                }

                mShader = new RadialGradient(viewWidth / 2, viewHeight / 2, radialGradientRadius, cs, null, Shader.TileMode.CLAMP);
            } else if (gradientType != null && gradientType.equals(getResources().getString(R.string.gradient_sweep))) {
                mShader = new SweepGradient(viewWidth / 2, viewHeight / 2, cs, null);
            } else {
                mShader = new LinearGradient(0, 0, endX, endY,
                        cs,
                        null, Shader.TileMode.CLAMP);
            }

            paint.setShader(mShader);
        }

        offscreenCanvas.drawPaint(paint);

        super.draw(offscreenCanvas);

        if (maskBitmap == null) {
            maskBitmap = createMask(canvas.getWidth(), canvas.getHeight());
        }

        offscreenCanvas.drawBitmap(maskBitmap, 0f, 0f, maskPaint);

        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint);
    }

    int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);
        return ret;
    }

    private Bitmap createMask(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mask);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);

        canvas.drawRect(0, 0, width, height, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

        return mask;
    }
}
