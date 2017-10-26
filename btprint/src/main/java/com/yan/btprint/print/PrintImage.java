package com.yan.btprint.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

class PrintImage {
    private int width;
    private float length = 0.0F;
    private Canvas canvas = null;
    private Paint paint = null;
    private Bitmap bm = null;
    private byte[] bitBuf = null;

    public PrintImage(Bitmap bitmap) {
        init(bitmap);
    }

    private void init(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        int dstWidth = 32 * 8;
        int dstHeight = (int) ((float) bitmap.getHeight() / bitmap.getWidth() * dstWidth);

        initCanvas(dstWidth);

        if (null == paint) {
            initPaint();
        }

        drawImage(0, 0, Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false));
    }

    private void initCanvas(int w) {
        int h = 10 * w;

        this.bm = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        this.canvas = new Canvas(this.bm);

        this.canvas.drawColor(-1);
        this.width = w;
        this.bitBuf = new byte[this.width / 8];
    }

    private void initPaint() {
        this.paint = new Paint();// 新建一个画笔

        this.paint.setAntiAlias(true);//

        this.paint.setColor(0xff000000);

        this.paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * draw bitmap
     */
    private void drawImage(float x, float y, Bitmap bmp) {
        try {
            // Bitmap bmp = BitmapFactory.decodeFile(path);
            this.canvas.drawBitmap(bmp, x, y, null);
            if (this.length < y + bmp.getHeight())
                this.length = (y + bmp.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bmp) {
                bmp.recycle();
            }
        }
    }

    /**
     * 使用光栅位图打印
     *
     * @return 字节
     */
    @SuppressWarnings("ConstantConditions")
    byte[] getBytes() {

        if (bm == null) {
            return null;
        }

        Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, getLength());
        byte[] imgBuf = new byte[this.width / 8 * getLength() + 8];
        int s;

        // 打印光栅位图的指令
        imgBuf[0] = 29;// 十六进制0x1D
        imgBuf[1] = 118;// 十六进制0x76
        imgBuf[2] = 48;// 30
        imgBuf[3] = 0;// 位图模式 0,1,2,3
        // 表示水平方向位图字节数（xL+xH × 256）
        imgBuf[4] = (byte) (this.width / 8);
        imgBuf[5] = 0;
        // 表示垂直方向位图点数（ yL+ yH × 256）
        imgBuf[6] = (byte) (getLength() % 256);//
        imgBuf[7] = (byte) (getLength() / 256);

        s = 7;
        for (int i = 0; i < getLength(); i++) {// 循环位图的高度
            for (int k = 0; k < this.width / 8; k++) {// 循环位图的宽度
                int c0 = nbm.getPixel(k * 8, i);// 返回指定坐标的颜色
                int p0;
                if (c0 == -1)// 判断颜色是不是白色
                    p0 = 0;// 0,不打印该点
                else {
                    p0 = 1;// 1,打印该点
                }
                int c1 = nbm.getPixel(k * 8 + 1, i);
                int p1;
                if (c1 == -1)
                    p1 = 0;
                else {
                    p1 = 1;
                }
                int c2 = nbm.getPixel(k * 8 + 2, i);
                int p2;
                if (c2 == -1)
                    p2 = 0;
                else {
                    p2 = 1;
                }
                int c3 = nbm.getPixel(k * 8 + 3, i);
                int p3;
                if (c3 == -1)
                    p3 = 0;
                else {
                    p3 = 1;
                }
                int c4 = nbm.getPixel(k * 8 + 4, i);
                int p4;
                if (c4 == -1)
                    p4 = 0;
                else {
                    p4 = 1;
                }
                int c5 = nbm.getPixel(k * 8 + 5, i);
                int p5;
                if (c5 == -1)
                    p5 = 0;
                else {
                    p5 = 1;
                }
                int c6 = nbm.getPixel(k * 8 + 6, i);
                int p6;
                if (c6 == -1)
                    p6 = 0;
                else {
                    p6 = 1;
                }
                int c7 = nbm.getPixel(k * 8 + 7, i);
                int p7;
                if (c7 == -1)
                    p7 = 0;
                else {
                    p7 = 1;
                }
                int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8
                        + p5 * 4 + p6 * 2 + p7;
                this.bitBuf[k] = (byte) value;
            }

            for (int t = 0; t < this.width / 8; t++) {
                s++;
                imgBuf[s] = this.bitBuf[t];
            }
        }

        if (null != this.bm) {
            this.bm.recycle();
            this.bm = null;
        }

        if (null != nbm) {
            nbm.recycle();
        }

        return imgBuf;
    }

    private int getLength() {
        return (int) this.length + 20;
    }
}