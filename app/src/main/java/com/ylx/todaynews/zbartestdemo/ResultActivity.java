package com.ylx.todaynews.zbartestdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private static final String RESULT_CONTENT = "result_content";

    private TextView mContentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mContentTxt = (TextView) findViewById(R.id.content_txt);
        mContentTxt.setText(getIntent().getStringExtra(RESULT_CONTENT));

    }

    public static void jumpActivity(Activity mActivity, String content){
        Intent intent = new Intent(mActivity, ResultActivity.class);
        intent.putExtra(RESULT_CONTENT, content);
        mActivity.startActivity(intent);
    }
}
