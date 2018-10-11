package com.dodge.hero.z.appjoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dodge.hero.z.library.AppJoint;
import com.dodge.hero.z.module1.router.IModuleRouter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_module).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IModuleRouter router = AppJoint.router(IModuleRouter.class);
                if (router != null) {
                    router.startActivity(MainActivity.this);
                }

            }
        });


        findViewById(R.id.btn_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IModuleRouter router = AppJoint.router(IModuleRouter.class);
                if (router != null) {
                    router.print();
                }

            }
        });




    }
}
