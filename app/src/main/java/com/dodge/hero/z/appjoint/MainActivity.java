package com.dodge.hero.z.appjoint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.dodge.hero.z.library.AppJoint;
import com.dodge.hero.z.module1.router.IModuleRouter;

public class MainActivity extends AppCompatActivity {

    private IModuleRouter mModuleRouter;
    private TextView mTvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mModuleRouter = AppJoint.router(IModuleRouter.class);
        mTvLog = findViewById(R.id.tv_log);
        findViewById(R.id.btn_module_1_page).setOnClickListener(v -> {
            mModuleRouter.startActivity(this);
            addLog("跳转模块1页面");
        });
        findViewById(R.id.btn_module_1_method).setOnClickListener(v -> {
            mModuleRouter.method1(this);
            addLog("调用模块1方法");
        });
    }

    private void addLog(String log) {
        String content = mTvLog.getText().toString();
        if (TextUtils.isEmpty(content)) {
            content = log;
        } else {
            content = log + "\n" + content;
        }
        mTvLog.setText(content);
    }


}
