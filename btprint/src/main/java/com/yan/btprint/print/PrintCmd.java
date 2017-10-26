package com.yan.btprint.print;

/**
 * Created by yefeng on 6/2/15.
 * github:yefengfreedom
 * <p/>
 * SUNMI command
 */
public class PrintCmd {

    public static final byte[] left = new byte[]{0x1b, 0x61, 0x00};// 靠左
    public static final byte[] center = new byte[]{0x1b, 0x61, 0x01};// 居中
    public static final byte[] right = new byte[]{0x1b, 0x61, 0x02};// 靠右
    public static final byte[] bold = new byte[]{0x1b, 0x45, 0x01};// 选择加粗模式
    public static final byte[] bold_cancel = new byte[]{0x1b, 0x45, 0x00};// 取消加粗模式
    public static final byte[] underline = new byte[]{0x1b, 0x2d, 2};//下划线
    public static final byte[] underline_cancel = new byte[]{0x1b, 0x2d, 0};//下划线
    public static final byte[] text_normal = new byte[]{0x1d, 0x21, 0x00};// 字体不放大]
    public static final byte[] text_big = new byte[]{0x1d, 0x21, 0x11};// 宽高加倍
    public static final byte[] text_big_width = new byte[]{0x1b, 0x21, 0x01};// 宽加倍
    public static final byte[] text_big_height = new byte[]{0x1b, 0x21, 0x10};// 高加倍
    public static final byte[] reset = new byte[]{0x1b, 0x40};//复位打印机
    public static final byte[] print = new byte[]{0x0a};//打印并换行

    /**
     * 走纸
     *
     * @param n 行数
     * @return 命令
     */
    public static byte[] walkPaper(byte n) {
        return new byte[]{0x1b, 0x64, n};
    }

    /**
     * 设置横向和纵向移动单位
     *
     * @param x 横向移动
     * @param y 纵向移动
     * @return 命令
     */
    public static byte[] move(byte x, byte y) {
        return new byte[]{0x1d, 0x50, x, y};
    }
}
