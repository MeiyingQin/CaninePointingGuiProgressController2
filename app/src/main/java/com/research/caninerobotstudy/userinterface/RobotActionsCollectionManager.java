package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RobotActionsCollectionManager extends AppCompatActivity {
    private CommandNode commands;
//    boolean isSkipBranch = false;
//    private int index = 0;
    private ArrayList<String> commandsToShow;
    private String currentCommand;
    private String previousExecutedCommand;
//    private final int choiceRequestCode = 3682;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_actions_collection_manager);

        Intent intent = getIntent();
        String section = intent.getStringExtra(getString(R.string.robot_command_section));
        String sectionKeyword = intent.getStringExtra(getString(R.string.robot_command_section_keyword));

        commands = new CommandNode(section, sectionKeyword, getBaseContext());
        currentCommand = commands.getChildren().get(0);
        commandsToShow = commands.getChildren();
        previousExecutedCommand = "";

        prepareNextCommand();
    }

    private void enableButtons(final boolean isEnable) {
        final boolean enable = isEnable;
        runOnUiThread(new Runnable() {
            public void run() {
//                findViewById(R.id.nextButton).setEnabled(enable);
                if (findViewById(R.id.repeatButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.repeatButton).setEnabled(enable);
                }
                if (findViewById(R.id.unexpectedButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.unexpectedButton).setEnabled(enable);
                }
                if (findViewById(R.id.skipButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.skipButton).setEnabled(enable);
                }
                if (findViewById(R.id.finishButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.finishButton).setEnabled(enable);
                }
                findViewById(R.id.choice_list_view).setEnabled(isEnable);
            }
        });
    }

    private void setUpButtons() {
        findViewById(R.id.repeatButton).setVisibility(View.VISIBLE);
        findViewById(R.id.skipButton).setVisibility(View.VISIBLE);
        findViewById(R.id.unexpectedButton).setVisibility(View.VISIBLE);
        findViewById(R.id.finishButton).setVisibility(View.VISIBLE);

        if (commands.isStart()) {
            findViewById(R.id.repeatButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.finishButton).setVisibility(View.INVISIBLE);
        }

        if (commands.isFinish()) {
            findViewById(R.id.skipButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.unexpectedButton).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.finishButton).setVisibility(View.INVISIBLE);
        }

        enableButtons(true);
    }

    private void setUpCommands() {
        ListView choiceListView = (ListView) findViewById(R.id.choice_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_custom, commandsToShow);
        choiceListView.setAdapter(arrayAdapter);

        choiceListView.setClickable(true);
        choiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enableButtons(false);
                currentCommand = commandsToShow.get(i);
                previousExecutedCommand = commandsToShow.get(i);
                commandsToShow = commands.getChildren();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RobotCommand robotCommand = new RobotCommand();
                        commands.setCurrentCommand(currentCommand);
                        robotCommand.sendInfoViaSocket(getBaseContext(), commands.getFullCommand());
                        prepareNextCommand();
                        enableButtons(true);
                    }
                }).start();
            }
        });
    }

    private void prepareNextCommand() {
        runOnUiThread(new Runnable() {
            public void run() {
                setUpButtons();
                setUpCommands();
            }
        });
    }

    public void onClick(View view) {
        final int viewId = view.getId();
        enableButtons(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (viewId == R.id.repeatButton) {
                    enableButtons(false);
                    RobotCommand robotCommand = new RobotCommand();
                    commands.setCurrentCommand(previousExecutedCommand);
                    robotCommand.sendInfoViaSocket(getBaseContext(), commands.getRepeatCommand());
                    enableButtons(true);
                } else if (viewId == R.id.skipButton) {
                    commands.setCurrentCommand(currentCommand);
                    commands.skip();
                    currentCommand = commands.getCurrentCommand();
                    commandsToShow = new ArrayList<String>();
                    commandsToShow.add(commands.getCurrentCommand());
                    prepareNextCommand();
                } else if (viewId == R.id.unexpectedButton) {
                    Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
                    intent.putExtra(getString(R.string.robot_command_section), getString(R.string.robot_command_section_unexpected_behaviour));
                    intent.putExtra(getString(R.string.robot_command_section_keyword), getString(R.string.robot_command_section_keyword_unexpected_behaviour));
                    startActivity(intent);
                    enableButtons(true);
                } else if (viewId == R.id.finishButton) {
                    finish();
                }
            }
        }).start();
    }
}
