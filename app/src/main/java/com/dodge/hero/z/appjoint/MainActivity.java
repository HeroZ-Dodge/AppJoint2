package com.dodge.hero.z.appjoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dodge.hero.z.annotation.ModuleSpec;


@ModuleSpec
public class MainActivity extends AppCompatActivity {

    @ModuleSpec
    private String str;

    @ModuleSpec
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
