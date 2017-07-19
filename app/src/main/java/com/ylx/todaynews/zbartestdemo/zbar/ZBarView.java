package com.ylx.todaynews.zbartestdemo.zbar;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ylx.todaynews.zbartestdemo.zbarcore.QRCodeView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import static android.R.attr.type;

public class ZBarView extends QRCodeView {

    static {
        System.loadLibrary("iconv");
    }

    private ImageScanner mScanner;

    public ZBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupScanner();
    }

    public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for (BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }

    @Override
    public String processData(byte[] data, int width, int height, boolean isRetry) {
        String result = null;
        Image barcode = new Image(width, height, "Y800");

        Rect rect = mScanBoxView.getScanBoxAreaRect(height);
        if (rect != null && !isRetry && rect.left + rect.width() <= width && rect.top + rect.height() <= height) {
            barcode.setCrop(rect.left, rect.top, rect.width(), rect.height());

        }

        barcode.setData(data);
        result = processData(barcode);

        return result;
    }

    private String processData(Image barcode) {
        String result = null;
        if (mScanner.scanImage(barcode) != 0) {
            SymbolSet syms = mScanner.getResults();
            for (Symbol sym : syms) {
                String symData = sym.getData();
                int mType = sym.getType();
                if (mType == Symbol.CODE128
                            || type == Symbol.QRCODE
                            || type == Symbol.CODABAR
                            || type == Symbol.ISBN13
                            || type == Symbol.DATABAR
                            || type == Symbol.DATABAR_EXP
                            || type == Symbol.I25
                            || type == Symbol.UPCA
                            || type == Symbol.UPCE
                            || type == Symbol.EAN8
                            || type == Symbol.EAN13
                            || type == Symbol.CODE128
                            ) {
                    if (!TextUtils.isEmpty(symData)) {
                        result = "识别类型：" + sym.getType() + "；识别内容:" + symData;
                        break;
                    }
                }

            }
        }
        return result;
    }
}