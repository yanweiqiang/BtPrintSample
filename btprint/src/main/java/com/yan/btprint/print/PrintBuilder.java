package com.yan.btprint.print;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanweiqiang on 2017/10/19.
 */

public class PrintBuilder {
    private String encoding = "GBK";
    private List<byte[]> bytesList;

    public PrintBuilder() {
        super();
        bytesList = new ArrayList<>();
    }

    public PrintBuilder alignLeft() {
        bytesList.add(PrintCmd.left);
        return this;
    }

    public PrintBuilder alignCenter() {
        bytesList.add(PrintCmd.center);
        return this;
    }

    public PrintBuilder alignRight() {
        bytesList.add(PrintCmd.right);
        return this;
    }

    public PrintBuilder bold() {
        bytesList.add(PrintCmd.bold);
        return this;
    }

    public PrintBuilder boldCancel() {
        bytesList.add(PrintCmd.bold_cancel);
        return this;
    }

    public PrintBuilder underline() {
        bytesList.add(PrintCmd.underline);
        return this;
    }

    public PrintBuilder underlineCancel() {
        bytesList.add(PrintCmd.underline_cancel);
        return this;
    }

    public PrintBuilder textNormal() {
        bytesList.add(PrintCmd.text_normal);
        return this;
    }

    public PrintBuilder textBig() {
        bytesList.add(PrintCmd.text_big);
        return this;
    }

    public PrintBuilder textBigWidth() {
        bytesList.add(PrintCmd.text_big_width);
        return this;
    }

    public PrintBuilder textBigHeight() {
        bytesList.add(PrintCmd.text_big_height);
        return this;
    }

    public PrintBuilder walkPaper(byte n) {
        bytesList.add(PrintCmd.walkPaper(n));
        return this;
    }

    public PrintBuilder move(byte x, byte y) {
        bytesList.add(PrintCmd.move(x, y));
        return this;
    }

    public PrintBuilder reset() {
        bytesList.add(PrintCmd.reset);
        return this;
    }

    public PrintBuilder print() {
        bytesList.add(PrintCmd.print);
        return this;
    }

    public PrintBuilder text(String text) {
        try {
            bytesList.add(text.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * @param texts   [t1,t2]
     * @param columns [16,16]
     * @param aligns  [1,1] 0 left, 1 center, 2 right.
     * @return this
     */
    public PrintBuilder row(String[] texts, int[] columns, int[] aligns) {
        try {
            bytesList.add(getRow(columns, aligns, texts).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PrintBuilder bitmap(Bitmap bitmap) {
        bytesList.add(new PrintImage(bitmap).getBytes());
        return this;
    }

    public List<byte[]> build() {
        return bytesList;
    }


    private String getRow(int[] columns, int[] aligns, String[] texts) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            int column = columns[i];
            int align = aligns[i];
            String text = texts[i];
            int length = text.getBytes(encoding).length;

            if (length > column) {
                text = text.substring(0, column - 2);
                text += "..";
            }

            getColumn(sb, column, align, text);
        }
        return sb.toString();
    }

    private void getColumn(StringBuilder sb, int column, int align, String text) throws UnsupportedEncodingException {

        int dif = column - text.getBytes(encoding).length;

        if (align == 0) {
            sb.append(text);
            for (int j = 0; j < dif; j++) {
                sb.append(" ");
            }
        } else if (align == 1) {
            for (int j = 0; j < dif / 2; j++) {
                sb.append(" ");
            }
            sb.append(text);
            for (int j = 0; j < dif - dif / 2; j++) {
                sb.append(" ");
            }
        } else if (align == 2) {
            for (int j = 0; j < dif; j++) {
                sb.append(" ");
            }
            sb.append(text);
        }
    }
}
