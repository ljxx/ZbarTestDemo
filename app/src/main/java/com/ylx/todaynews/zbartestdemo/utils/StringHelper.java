package com.ylx.todaynews.zbartestdemo.utils;

/**
 * Created by HJK on 16/7/15.
 */
public class StringHelper {
    private String[] mDictionary = new String[] { "N", "ORG", "TIL", "URL",
            "ENAIL", "NOTE", "ADR" };// 定义数据词典
    private String[] mValues = new String[] { "姓名", "组织", "职位", "电子邮件", "号码",
            "地址" };// 定义词典对应值
    private StringBuilder mBuilder;
    private String mString;

    // 构造函数
    public StringHelper(String s) {
        this.mString = s;
        mBuilder = new StringBuilder();
    }

    // 解析函数
    public String SplitFormDict() {
        int mStartIndex = 0;
        int mEndIndex = 0;
        // 如果存在姓名
        for (int i = 0; i < mDictionary.length; i++) {
            if (mString.indexOf(mDictionary[i]) > -1) {
                mStartIndex = mString.indexOf(mDictionary[0]);
                mEndIndex = mString.indexOf(";", mStartIndex);
                mBuilder.append(mValues[i] + ":"
                        + mString.substring(mStartIndex, mEndIndex) + "\n");
            } else {
                return mString;
            }
        }
        return mBuilder.toString();
    }
}
