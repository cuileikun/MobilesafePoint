package com.cuileikun.mobilesave.activity.lostfind;

import android.content.Intent;
import android.os.Bundle;

import com.cuileikun.mobilesave.R;

public class SetUp1Activity extends SetUpBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up1);
    }

    @Override
    public boolean next_activity() {
        Intent intent = new Intent(this,SetUp2Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean pre_activity() {
        return true;
    }
}
