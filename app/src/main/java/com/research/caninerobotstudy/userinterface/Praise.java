package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Praise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praise);
    }

    public void onClick(View view) {
        int viewId = view.getId();
        Intent returnIntent = new Intent();
        String result = "";
        if (viewId == R.id.correctButton) {
            result = getString(R.string.praise_result_correct);
        } else if (viewId == R.id.wrongButton) {
            result = getString(R.string.praise_result_wrong);
        }
        returnIntent.putExtra(getString(R.string.praise_result), result);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
