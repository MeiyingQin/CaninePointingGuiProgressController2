package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RobotActionsCollectionManager extends AppCompatActivity {
    private CommandNode commands;
    private ArrayList<String> commandsToShow;
    private String previousExecutedCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_actions_collection_manager);

        Intent intent = getIntent();
        String section = intent.getStringExtra(getString(R.string.robot_command_section));
        String sectionKeyword = intent.getStringExtra(getString(R.string.robot_command_section_keyword));

        commands = new CommandNode(section, sectionKeyword, getBaseContext());
        commandsToShow = commands.getChildren();
        previousExecutedCommand = "";

        prepareNextCommand();
    }

    private void enableButtons(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (findViewById(R.id.repeatButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.repeatButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.unexpectedButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.unexpectedButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.skipButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.skipButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.finishButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.finishButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.dogScaredButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.dogScaredButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.dispenserRotateButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.dispenserRotateButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.dispenserLeftRotateButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.dispenserLeftRotateButton).setEnabled(isEnable);
                }
                if (findViewById(R.id.dispenserRightRotateButton).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.dispenserRightRotateButton).setEnabled(isEnable);
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
        findViewById(R.id.dogScaredButton).setVisibility(View.VISIBLE);
        findViewById(R.id.dispenserRotateButton).setVisibility(View.VISIBLE);
        findViewById(R.id.dispenserLeftRotateButton).setVisibility(View.VISIBLE);
        findViewById(R.id.dispenserRightRotateButton).setVisibility(View.VISIBLE);

        if (previousExecutedCommand.isEmpty()) {
            findViewById(R.id.repeatButton).setVisibility(View.INVISIBLE);
        }

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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.listview_custom, commandsToShow);
        choiceListView.setAdapter(arrayAdapter);

        choiceListView.setClickable(true);
        choiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enableButtons(false);
                previousExecutedCommand = commandsToShow.get(i);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RobotCommand robotCommand = new RobotCommand();
                        commands.setCurrentCommand(previousExecutedCommand);
                        commandsToShow = commands.getChildren();
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
                    String currentCommand = commands.getCurrentCommand();
                    commands.setCurrentCommand(previousExecutedCommand);
                    robotCommand.sendInfoViaSocket(getBaseContext(), commands.getRepeatCommand());
                    commands.setCurrentCommand(currentCommand);
                    enableButtons(true);
                } else if (viewId == R.id.skipButton) {
                    boolean isAddCurrent = commands.isNextQuestion() || commands.isNextChoice();
                    if (isAddCurrent) {
                        commands.setCurrentCommand(commands.getChildren().get(0));
                    }
                    commands.skip();
                    commandsToShow = new ArrayList<>();
                    String currentCommand = commands.getCurrentCommand();
                    if (isAddCurrent) {
                        String parent = commands.getParent(currentCommand);
                        commands.setCurrentCommand(parent);
                    } else if (!commands.getChildren().isEmpty()) {
                        currentCommand = commands.getChildren().get(0);
                    } else {
                        currentCommand = "";
                    }
                    if (!currentCommand.isEmpty()) {
                        commandsToShow.add(currentCommand);
                    }
                    prepareNextCommand();
                } else if (viewId == R.id.unexpectedButton) {
                    Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
                    intent.putExtra(getString(R.string.robot_command_section), getString(R.string.robot_command_section_unexpected_behaviour));
                    intent.putExtra(getString(R.string.robot_command_section_keyword), getString(R.string.robot_command_section_keyword_unexpected_behaviour));
                    startActivity(intent);
                    enableButtons(true);
                } else if (viewId == R.id.dogScaredButton) {
                    Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
                    intent.putExtra(getString(R.string.robot_command_section), getString(R.string.robot_command_section_dog_scared));
                    intent.putExtra(getString(R.string.robot_command_section_keyword), getString(R.string.robot_command_section_keyword_dog_scared));
                    startActivity(intent);
                    enableButtons(true);
                } else if (viewId == R.id.finishButton) {
                    finish();
                } else if (viewId == R.id.dispenserRotateButton) {
                    DispenserController controller = new DispenserController(getApplicationContext());
                    controller.rotate();
                    enableButtons(true);
                }  else if (viewId == R.id.dispenserLeftRotateButton) {
                    DispenserController controller = new DispenserController(getApplicationContext());
                    controller.left_rotate();
                    enableButtons(true);
                } else if (viewId == R.id.dispenserRightRotateButton) {
                    DispenserController controller = new DispenserController(getApplicationContext());
                    controller.right_rotate();
                    enableButtons(true);
                }
            }
        }).start();
    }
}
