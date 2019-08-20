package com.veni.tools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.veni.tools.R;

/**
 * 2019/5/21 13:08
 * Here be dragons
 * 前方高能
 */
public class CornerImageView extends AppCompatImageView {
    private boolean isCircle = false;
    private int mBorderColor = Color.BLACK;// 边框颜色
    private int mBorderWidth = 0;// 边框宽度
    private boolean mBorderOverlay;//边框是否覆盖图片
    private int mFillColor = Color.TRANSPARENT;// 遮罩颜色
    private int corner_radius = 0;// 统一设置圆角半径，优先级高于单独设置每个角的半径
    private int topLeftRadius = 0;// 左上角圆角半径
    private int topRightRadius = 0;// 右上角圆角半径
    private int bottomLeftRadius = 0;// 左下角圆角半径
    private int bottomRightRadius = 0;// 右下角圆角半径

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private final RectF mDrawableRect = new RectF();// 图片占的矩形区域
    private final RectF mBorderRect = new RectF();// 边框的矩形区域

    private final Matrix mShaderMatrix = new Matrix();//矩阵
    private final Paint mBitmapPaint = new Paint();//图片画笔
    private final Paint mBorderPaint = new Paint();//边框画笔
    private final Paint mFillPaint = new Paint();//遮罩层画笔
    private Path path; // 用来裁剪图片的ptah

    private Bitmap mBitmap;//图片
    private BitmapShader mBitmapShader;//渲染图像，使用图像为绘制图形着色

    private int mBitmapWidth, mBitmapHeight;
    private float[] borderRadii;
    private float[] srcRadii;
    private float width, height;


    public CornerImageView(Context context) {
        this(context, null);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //获得这个控件对应的属性。
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView, 0, 0);
        //获得属性值
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CornerImageView_is_circle) {
                isCircle = typedArray.getBoolean(attr, isCircle);//圆角还是圆形模式
            } else if (attr == R.styleable.CornerImageView_corner_radius) {
                corner_radius = typedArray.getDimensionPixelSize(attr, corner_radius);//角度
            } else if (attr == R.styleable.CornerImageView_corner_top_left_radius) {
                topLeftRadius = typedArray.getDimensionPixelSize(attr, topLeftRadius);//角度
            } else if (attr == R.styleable.CornerImageView_corner_top_right_radius) {
                topRightRadius = typedArray.getDimensionPixelSize(attr, topRightRadius);//角度
            } else if (attr == R.styleable.CornerImageView_corner_bottom_left_radius) {
                bottomLeftRadius = typedArray.getDimensionPixelSize(attr, bottomLeftRadius);//角度
            } else if (attr == R.styleable.CornerImageView_corner_bottom_right_radius) {
                bottomRightRadius = typedArray.getDimensionPixelSize(attr, bottomRightRadius);//角度
            } else if (attr == R.styleable.CornerImageView_border_width) {
                mBorderWidth = typedArray.getDimensionPixelSize(attr, mBorderWidth);
            } else if (attr == R.styleable.CornerImageView_border_color) {
                mBorderColor = typedArray.getColor(attr, mBorderColor);
            } else if (attr == R.styleable.CornerImageView_border_overlay) {
                mBorderOverlay = typedArray.getBoolean(attr, mBorderOverlay);
            } else if (attr == R.styleable.CornerImageView_fill_color) {
                mFillColor = typedArray.getColor(attr, mFillColor);
            }

        }
        //回收这个对象
        typedArray.recycle();
        if (corner_radius > 0) {
            topLeftRadius = topRightRadius = bottomLeftRadius = bottomRightRadius = corner_radius;
        }
        path = new Path();
        super.setScaleType(SCALE_TYPE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        initializeBitmap();

        if (mBitmap == null) {
            return;
        }
        path.reset();
        if (isCircle) {
            path.addCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), srcRadii[0], Path.Direction.CCW);
            canvas.drawPath(path, mBitmapPaint);
            if (mFillColor != Color.TRANSPARENT) {
                path.addCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), srcRadii[0], Path.Direction.CCW);
                canvas.drawPath(path, mFillPaint);
            }
            if (mBorderWidth > 0) {
                path.addCircle(mBorderRect.centerX(), mBorderRect.centerY(), borderRadii[0], Path.Direction.CCW);
                canvas.drawPath(path, mBorderPaint);
            }
        } else {//圆角
            path.addRoundRect(mDrawableRect, srcRadii, Path.Direction.CCW);
            canvas.drawPath(path, mBitmapPaint);
            if (mFillColor != Color.TRANSPARENT) {
                path.addRoundRect(mDrawableRect, srcRadii, Path.Direction.CCW);
                canvas.drawPath(path, mFillPaint);
            }
            if (mBorderWidth > 0) {
                path.addRoundRect(mBorderRect, borderRadii, Path.Direction.CCW);
                canvas.drawPath(path, mBorderPaint);
            }
        }
        path.close();
    }

    private void initializeBitmap() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private void setup() {
        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        if (mBitmap == null) {
            return;
        }
        //设置 图片画笔
        insertBitmapPaint();
        //设置 边框画笔
        insertBorderPaint();
        //设置 遮罩层画笔
        insertFillPaint();
        //设置 图片矩阵
        insertDrawableRect();

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        updateShaderMatrix();
    }


    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            int canvasright = 0;
            int canvasbottom = 0;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, Bitmap.Config.ARGB_8888);
            } else {
                // 当设置不为图片，为颜色时，获取的drawable宽高会有问题，所有当为颜色时候获取控件的宽高
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                if (!isCircle) {
                    w = drawable.getIntrinsicWidth() <= 0 ? getWidth() : drawable.getIntrinsicWidth();
                    h = drawable.getIntrinsicHeight() <= 0 ? getHeight() : drawable.getIntrinsicHeight();
                    canvasright = w;
                    canvasbottom = h;
                }
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            canvasright = canvasright == 0 ? canvas.getWidth() : canvasright;
            canvasbottom = canvasbottom == 0 ? canvas.getHeight() : canvasbottom;
            drawable.setBounds(0, 0, canvasright, canvasbottom);
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //设置 图片画笔
    private void insertBitmapPaint() {
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
    }

    //设置 边框画笔
    private void insertBorderPaint() {
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    //设置 遮罩层画笔
    private void insertFillPaint() {
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);
    }


    //设置 图片矩阵
    private void insertDrawableRect() {
        insertBorderRect();
        if (isCircle) {
            mDrawableRect.set(mBorderRect);
        } else {
            mDrawableRect.set(0, 0, width, height);
        }
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f);
        }
        if (isCircle) {
            srcRadii = new float[1];
            srcRadii[0] = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);
        } else {
            srcRadii = new float[8];
            srcRadii[0] = srcRadii[1] = topLeftRadius - mBorderWidth / 2.0f;
            srcRadii[2] = srcRadii[3] = topRightRadius - mBorderWidth / 2.0f;
            srcRadii[4] = srcRadii[5] = bottomRightRadius - mBorderWidth / 2.0f;
            srcRadii[6] = srcRadii[7] = bottomLeftRadius - mBorderWidth / 2.0f;
        }
    }

    //设置 图片边框矩阵
    private void insertBorderRect() {
        mBorderRect.set(calculateBounds());
        if (isCircle) {
            borderRadii = new float[1];
            borderRadii[0] = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f);
        } else {
            borderRadii = new float[8];
            borderRadii[0] = borderRadii[1] = topLeftRadius;
            borderRadii[2] = borderRadii[3] = topRightRadius;
            borderRadii[4] = borderRadii[5] = bottomRightRadius;
            borderRadii[6] = borderRadii[7] = bottomLeftRadius;
        }
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;
        if (isCircle) {
            return new RectF(left, top, left + sideLength, top + sideLength);
        } else {
            return new RectF(mBorderWidth / 2.0f, mBorderWidth / 2.0f, width - mBorderWidth / 2.0f, height - mBorderWidth / 2.0f);
        }
    }

    //更新 图片阴影矩阵
    private void updateShaderMatrix() {
        float scale = 1.0f;
        float dx = 0;
        float dy = 0;

        if (isCircle) {
            mShaderMatrix.set(null);
            if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
                scale = mDrawableRect.height() / (float) mBitmapHeight;
                dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
            } else {
                scale = mDrawableRect.width() / (float) mBitmapWidth;
                dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
            }
        } else {
            if (!(mBitmapWidth == getWidth() && mBitmapHeight == getHeight())) {
                // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                scale = Math.max(getWidth() * 1.0f / mBitmapWidth,
                        getHeight() * 1.0f / mBitmapHeight);
            }
        }
        mShaderMatrix.setScale(scale, scale);
        if (isCircle) {
            mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);
        }
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        if (isCircle == circle) {
            return;
        }
        isCircle = circle;
        invalidate();
    }

    public int getCorner_radius() {
        return corner_radius;
    }

    public void setCorner_radius(int corner_radius) {
        if (this.corner_radius == corner_radius) {
            return;
        }
        if (corner_radius > 0) {
            topLeftRadius = topRightRadius = bottomLeftRadius = bottomRightRadius = corner_radius;
        }
        invalidate();
    }

    public void setRadius(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        if (this.topLeftRadius == topLeftRadius &&
                this.topRightRadius == topRightRadius &&
                this.bottomLeftRadius == bottomLeftRadius &&
                this.bottomRightRadius == bottomRightRadius) {
            return;
        }
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
        invalidate();
    }

    public int getTopLeftRadius() {
        return topLeftRadius;
    }

    public int getTopRightRadius() {
        return topRightRadius;
    }

    public int getBottomLeftRadius() {
        return bottomLeftRadius;
    }

    public int getBottomRightRadius() {
        return bottomRightRadius;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }
        mBorderColor = borderColor;
        invalidate();
    }

    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setBorderColor(getContext().getResources().getColor(borderColorRes));
    }

    public int getFillColor() {
        return mFillColor;
    }

    public void setFillColor(@ColorInt int fillColor) {
        if (fillColor == mFillColor) {
            return;
        }
        mFillColor = fillColor;
        invalidate();
    }

    public void setFillColorResource(@ColorRes int fillColorRes) {
        setFillColor(getContext().getResources().getColor(fillColorRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }
        mBorderWidth = borderWidth;
        invalidate();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }
        mBorderOverlay = borderOverlay;
        invalidate();
    }
}
