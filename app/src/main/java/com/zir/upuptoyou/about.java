package com.zir.upuptoyou;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class about extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textView = (TextView) findViewById(R.id.textView_about);
        textView.setAutoLinkMask(Linkify.ALL);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        PackageManager manager = getPackageManager();
        String packagename = this.getPackageName();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo( packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info != null ? info.versionName : null;
        ((TextView) findViewById(R.id.textView_author)).setText(version+ " by zirc0");
    }
}
