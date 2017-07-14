package com.ylx.todaynews.zbartestdemo.zbarcore;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

public class ProcessDataTask extends AsyncTask<Void, Void, String> {
    private Camera mCamera;
    private byte[] mData;

    public ProcessDataTask(Camera camera, byte[] data) {
        mCamera = camera;
        mData = data;
    }

    public ProcessDataTask perform() {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
        return this;
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected String doInBackground(Void... params) {
        ImageScanner mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取扫描图片的大小
        Camera.Size mSize = parameters.getPreviewSize();
        // 构造存储图片的Image
        Image mResult = new Image(mSize.width, mSize.height, "Y800");// 第三个参数不知道是干嘛的
        // 设置Image的数据资源
        mResult.setData(mData);
        // 获取扫描结果的代码
        int mResultCode = mScanner.scanImage(mResult);
        String m = "";
        // 如果代码不为0，表示扫描成功
        if (mResultCode != 0) {
            // 停止扫描
//            stopPreview();
            // 开始解析扫描图片
            SymbolSet Syms = mScanner.getResults();
            for (Symbol mSym : Syms) {
                // mSym.getType()方法可以获取扫描的类型，ZBar支持多种扫描类型,这里实现了条形码、二维码、ISBN码的识别
                int type = mSym.getType();
                if (type == Symbol.CODE128
                        || type == Symbol.QRCODE
                        || type == Symbol.CODABAR
                        || type == Symbol.ISBN10
                        || type == Symbol.ISBN13
                        || type == Symbol.DATABAR
                        || type == Symbol.DATABAR_EXP
                        || type == Symbol.I25
                        || type == Symbol.UPCA
                        || type == Symbol.UPCE
                        || type == Symbol.EAN8
                        || type == Symbol.EAN13) {

                     return mSym.getData();
                } else {
                    // 否则继续扫描
//                    startPreview();
                }
            }

        }  else {
            // 否则继续扫描
//            startPreview();
        }
        return "";

    }
}
