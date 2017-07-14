package com.ylx.todaynews.zbartestdemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ylx.todaynews.zbartestdemo.utils.BitmapUtils;
import com.ylx.todaynews.zbartestdemo.utils.ScanUtils;
import com.ylx.todaynews.zbartestdemo.utils.StringHelper;
import com.ylx.todaynews.zbartestdemo.zbarcore.QRCodeView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

public class TestScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = TestScanActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mQRCodeView = (QRCodeView) findViewById(R.id.zbarview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();

        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    private static final int REQUEST_CODE = 123;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot:
                mQRCodeView.startSpot();
                break;
            case R.id.stop_spot:
                mQRCodeView.stopSpot();
                break;
            case R.id.start_spot_showrect:
                mQRCodeView.startSpotAndShowRect();
                break;
            case R.id.stop_spot_hiddenrect:
                mQRCodeView.stopSpotAndHiddenRect();
                break;
            case R.id.show_rect:
                mQRCodeView.showScanRect();
                break;
            case R.id.hidden_rect:
                mQRCodeView.hiddenScanRect();
                break;
            case R.id.start_preview:
                mQRCodeView.startCamera();
                break;
            case R.id.stop_preview:
                mQRCodeView.stopCamera();
                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.scan_barcode:
                mQRCodeView.changeToScanBarcodeStyle();
                break;
            case R.id.scan_qrcode:
                mQRCodeView.changeToScanQRCodeStyle();
                break;
            case R.id.choose_qrcde_from_gallery: //从图库选择图片
                Intent innerIntent = new Intent();
                innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ImageScanner mScanner = new ImageScanner();
            mScanner.setConfig(0, Config.X_DENSITY, 3);
            mScanner.setConfig(0, Config.Y_DENSITY, 3);

            switch (requestCode) {
                case REQUEST_CODE:
                    // 从本地取出照片
                    String[] proj = {MediaStore.Images.Media.DATA};
                    // 获取选中图片的路径
                    String photoPath = null;
                    Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photoPath = cursor.getString(columnIndex);
                        if (photoPath == null) {
                            photoPath = ScanUtils.getPath(getApplicationContext(), data.getData());
                        }
                    }
                    cursor.close();
                    Bitmap bitmap = BitmapUtils.getCompressedBitmap(photoPath);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    Image image = new Image(width, height, "RGB4");
                    image.setData(pixels);
                    int result = mScanner.scanImage(image.convert("Y800"));
                    // 如果代码不为0，表示扫描成功
                    if (result != 0) {
                        // 停止扫描
//                        stopPreview();
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
                                // 添加震动效果，提示用户扫描完成
                                Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                mVibrator.vibrate(400);
                                String resultContent = "扫描类型:" + GetResultByCode(mSym.getType()) + "\n" + mSym.getData();
                                StringHelper stringHelper = new StringHelper(resultContent);
                                resultContent = stringHelper.SplitFormDict();
                                Toast.makeText(TestScanActivity.this, resultContent, Toast.LENGTH_LONG).show();
                                // 这里需要注意的是，getData方法才是最终返回识别结果的方法
                                // 但是这个方法是返回一个标识型的字符串，换言之，返回的值中包含每个字符串的含义
                                // 例如N代表姓名，URL代表一个Web地址等等，其它的暂时不清楚，如果可以对这个进行一个较好的分割
                                // 效果会更好，如果需要返回扫描的图片，可以对Image做一个合适的处理
                            }
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TestScanActivity.this, "图片格式有误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        }
    }

    // 根据返回的代码值来返回相应的格式化数据
    public String GetResultByCode(int CodeType) {
        String mResult = "";
        switch (CodeType) {
            // 条形码
            case Symbol.CODABAR:
                mResult = "条形码";
                break;
            // 128编码格式二维码)
            case Symbol.CODE128:
                mResult = "二维码";
                break;
            // QR码二维码
            case Symbol.QRCODE:
                mResult = "二维码";
                break;
            // ISBN10图书查询
            case Symbol.ISBN10:
                mResult = "图书ISBN号";
                break;
            // ISBN13图书查询
            case Symbol.ISBN13:
                mResult = "图书ISBN号";
                break;
        }
        return mResult;
    }
}