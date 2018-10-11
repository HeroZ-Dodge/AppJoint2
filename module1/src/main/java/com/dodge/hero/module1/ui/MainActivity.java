package com.dodge.hero.module1.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dodge.hero.z.library.AppJoint;
import com.dodge.hero.z.module1.R;
import com.dodge.hero.z.module1.router.IAppRouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_1_main);
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IAppRouter appRouter = AppJoint.router(IAppRouter.class);
                if (appRouter != null) {
                    appRouter.print();
                }
            }
        });

    }
}
