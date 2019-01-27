package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ActivityGalleries extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleries);
    }

    public void startIntroduction(View view) {
        Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
        intent.putExtra(getString(R.string.robot_command_section), getString(R.string.robot_command_section_introduction));
        intent.putExtra(getString(R.string.robot_command_section_keyword), getString(R.string.robot_command_section_introduction));
        startActivity(intent);
    }

    public void startWarmup(View view) {
        Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
        intent.putExtra(getString(R.string.robot_command_section), getString(R.string.robot_command_section_warmup));
        intent.putExtra(getString(R.string.robot_command_section_keyword), getString(R.string.robot_command_section_keyword_warmup));
        startActivity(intent);
    }

    public void startExperiment(View view) {
        Intent intent = new Intent(getApplicationContext(), RunTrials.class);
        intent.putExtra(getString(R.string.trial_type), getString(R.string.trial_type_test));
        startActivity(intent);
    }

    public void testSystem(View view) {
        Intent intent = new Intent(getApplicationContext(), TestSystem.class);
        startActivity(intent);
    }
}
