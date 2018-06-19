package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ActivityGalleries extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleries);
    }

    public void startIntroduction(View view) {
        Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
//        intent.putStringArrayListExtra(getString(R.string.robot_actions_collection_manager_command_extra), new RobotCommand().getCommands(getString(R.string.robot_command_section_introduction), getString(R.string.robot_command_section_introduction), getBaseContext()));
        intent.putExtra(getString(R.string.robot_command_section), getString(R.string.robot_command_section_introduction));
        intent.putExtra(getString(R.string.robot_command_section_keyword), getString(R.string.robot_command_section_introduction));
        startActivity(intent);
    }

    public void startWarmup(View view) {
        Log.d("[ActivityGalleries]", "startWarmup");
        Intent intent = new Intent(getApplicationContext(), RunTrials.class);
        intent.putExtra(getString(R.string.trial_type), getString(R.string.trial_type_warmup));
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
