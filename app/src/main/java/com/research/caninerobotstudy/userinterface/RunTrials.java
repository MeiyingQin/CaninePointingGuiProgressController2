package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class RunTrials extends AppCompatActivity {
    private int trialIndex = 1;
    private int totalTrials = 0;

    private static final int warmUpTotalTrials = 2;
    private static final int testTotalTrials = 8;

    private static final int introductionFinishRequest = 5829;
    private static final int pointFinishRequest = 2357;
    private static final int praiseFinishRequest = 4724;
    private static final int trialFinishRequest = 2313;
    private static final int finishFinishRequest = 6235;

    private String section = "";
    private String introductionSectionKeyword = "";
    private String pointSectionKeyword = "";
    private String trialFinishSectionKeyword = "";
    private String finishSectionKeyword = "";

    private int correctTrials = 0;
    private int wrongTrials = 0;
    private ArrayList<String> correctPraises = null;
    private ArrayList<String> wrongPraises = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_trials);

        Intent intent = getIntent();
        String trialType = intent.getStringExtra(getString(R.string.trial_type));

        CommandNode praises = null;
        if (trialType.equals(getString(R.string.trial_type_warmup))) {
            section = getString(R.string.robot_command_section_warmup);
            introductionSectionKeyword = getString(R.string.robot_command_section_keyword_warmup_introduction);
            pointSectionKeyword = getString(R.string.robot_command_section_keyword_warmup_point);
            trialFinishSectionKeyword = getString(R.string.robot_command_section_keyword_warmup_trial_finish);
            finishSectionKeyword = getString(R.string.robot_command_section_keyword_warmup_finish);
            totalTrials = warmUpTotalTrials;
            praises = new CommandNode(section, getString(R.string.robot_command_section_keyword_warmup_praise), getBaseContext());
        } else if (trialType.equals(getString(R.string.trial_type_test))) {
            section = getString(R.string.robot_command_section_test);
            introductionSectionKeyword = getString(R.string.robot_command_section_keyword_test_introduction);
            pointSectionKeyword = getString(R.string.robot_command_section_keyword_test_point);
            trialFinishSectionKeyword = getString(R.string.robot_command_section_keyword_test_trial_finish);
            finishSectionKeyword = getString(R.string.robot_command_section_keyword_test_finish);
            totalTrials = testTotalTrials;
            praises = new CommandNode(section, getString(R.string.robot_command_section_keyword_test_praise), getBaseContext());
        }

        if (praises != null) {
            praises.setCurrentCommand(getString(R.string.robot_command_section_keyword_praise_correct));
            correctPraises = praises.getChildren();
            praises.setCurrentCommand(getString(R.string.robot_command_section_keyword_praise_wrong));
            wrongPraises = praises.getChildren();

            Intent introductionIntent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
            introductionIntent.putExtra(getString(R.string.robot_command_section), section);
            introductionIntent.putExtra(getString(R.string.robot_command_section_keyword), introductionSectionKeyword);
            startActivityForResult(introductionIntent, introductionFinishRequest);
        } else {
            finish();
        }
    }

    private void enableButtons(boolean isEnable) {
        final boolean enable = isEnable;
        runOnUiThread(new Runnable() {
            public void run() {
                findViewById(R.id.nextTrialButton).setEnabled(enable);
                findViewById(R.id.repeatTrialButton).setEnabled(enable);
            }
        });
    }

    private void setNextTrialButtonName() {
        enableButtons(true);
        ((Button)findViewById(R.id.nextTrialButton)).setText(String.format(getString(R.string.trial_next_trial_button_name), trialIndex));
    }

    private void praise(final boolean isCorrect) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String command = "";
                if (isCorrect) {
                    command += correctPraises.get(correctTrials) + getString(R.string.robot_command_deliminator);
                    correctTrials++;
                } else {
                    command += wrongPraises.get(wrongTrials) + getString(R.string.robot_command_deliminator);
                    wrongTrials++;
                }
                command += getString(R.string.robot_command_full);
                RobotCommand robotCommand = new RobotCommand();
                robotCommand.sendInfoViaSocket(getBaseContext(), command);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode == introductionFinishRequest) {
            setNextTrialButtonName();
        } else if(requestCode == pointFinishRequest) {
            Intent intent = new Intent(this, Praise.class);
            startActivityForResult(intent, praiseFinishRequest);
        } else if (requestCode == praiseFinishRequest) {
            if (resultCode == RESULT_OK) {
                boolean isCorrect = data.getStringExtra(getString(R.string.praise_result)).equals(getString(R.string.praise_result_correct));
                praise(isCorrect);
            }
            Intent finishIntent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
            finishIntent.putExtra(getString(R.string.robot_command_section), section);
            finishIntent.putExtra(getString(R.string.robot_command_section_keyword), trialFinishSectionKeyword);
            startActivityForResult(finishIntent, trialFinishRequest);
        } else if (requestCode == trialFinishRequest) {
            if (trialIndex <= totalTrials) {
                setNextTrialButtonName();
            } else {
                Intent finishIntent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
                finishIntent.putExtra(getString(R.string.robot_command_section), section);
                finishIntent.putExtra(getString(R.string.robot_command_section_keyword), finishSectionKeyword);
                startActivityForResult(finishIntent, finishFinishRequest);
            }
        } else if (requestCode == finishFinishRequest) {
                finish();
        }
    }

    public void onClick(View view) {
        enableButtons(false);
        if (view.getId() == R.id.nextTrialButton) {
            trialIndex++;
            Intent trialIntent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
            trialIntent.putExtra(getString(R.string.robot_command_section), section);
            trialIntent.putExtra(getString(R.string.robot_command_section_keyword), pointSectionKeyword);
            startActivityForResult(trialIntent, pointFinishRequest);
        } else if (view.getId() == R.id.repeatTrialButton) {
            Intent trialIntent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
            trialIntent.putExtra(getString(R.string.robot_command_section), section);
            trialIntent.putExtra(getString(R.string.robot_command_section_keyword), pointSectionKeyword);
            startActivityForResult(trialIntent, pointFinishRequest);
        }

    }

}
