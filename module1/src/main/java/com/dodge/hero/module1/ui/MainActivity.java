package com.dodge.hero.module1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dodge.hero.z.library.AppJoint;
import com.dodge.hero.z.module1.R;
import com.dodge.hero.z.module1.router.IAppRouter;

public class MainActivity extends AppCompatActivity {

    private IAppRouter mAppRouter;
    private TextView mTvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_1_main);
        mAppRouter = AppJoint.router(IAppRouter.class);
        mTvLog = findViewById(R.id.tv_log);
        findViewById(R.id.btn_app_module_page).setOnClickListener(v -> mAppRouter.method1(this));

    }
}
