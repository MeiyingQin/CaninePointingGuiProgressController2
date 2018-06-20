package com.research.caninerobotstudy.userinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class RobotActionsCollectionManager extends AppCompatActivity {
    private CommandNode commands;
    boolean isSkipBranch = false;
//    private int index = 0;
    private String currentCommand;
    private String previousCommand;
//    private final int choiceRequestCode = 3682;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_actions_collection_manager);

        Intent intent = getIntent();
        String section = intent.getStringExtra(getString(R.string.robot_command_section));
        String sectionKeyword = intent.getStringExtra(getString(R.string.robot_command_section_keyword));

        Log.d("[RobotActionsCollectionManager - section]", section);
        Log.d("[RobotActionsCollectionManager - sectionKeyword]", sectionKeyword);

        commands = new CommandNode(section, sectionKeyword, getBaseContext());

//        onClick(findViewById(R.id.nextButton));

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

//        if (commands.isNextChoice()) {
//            findViewById(R.id.skipButton).setVisibility(View.INVISIBLE);
//        }

        if (commands.isFinish()) {
            findViewById(R.id.skipButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.unexpectedButton).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.finishButton).setVisibility(View.INVISIBLE);
        }

        enableButtons(true);
    }

    private void setUpCommands() {
        ArrayList<String> children;
        if (isSkipBranch) {
            children = new ArrayList<String>();
            children.add(currentCommand);
            isSkipBranch = false;
        } else {
            children = commands.getChildren();
        }
        ListView choiceListView = (ListView) findViewById(R.id.choice_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_custom, children);
        choiceListView.setAdapter(arrayAdapter);

        final ArrayList<String> nextCommands = children;

        choiceListView.setClickable(true);
        choiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enableButtons(false);
                commands.setNextCommand(nextCommands.get(i));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RobotCommand robotCommand = new RobotCommand();
                        robotCommand.sendInfoViaSocket(getBaseContext(), commands.getFullCommand());
                        prepareNextCommand();
                        previousCommand = commands.getCurrentCommand();
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

    private String findIntercept() {
        String intercept = "";
        if (commands.isNextChoice()) {
            isSkipBranch = true;
            int index = 0;
            int branches = commands.getChildren().size();
            ArrayList<String> candidates = new ArrayList<String>();
            candidates.addAll(commands.getChildren());
            boolean isFound = false;
            HashMap<String, Integer> intersect = new HashMap<String, Integer>();
            while (!isFound && index < candidates.size()) {
                commands.setNextCommand(candidates.get(index));
                if (commands.isFinish()) {
                    isFound = true;
                } else {
                    String child = findIntercept();
                    if (intersect.containsKey(child)) {
                        intersect.put(child, intersect.get(child) + 1);
                        if (intersect.get(child) == branches) {
                            intercept = child;
                            isFound = true;
                        }
                    } else {
                        intersect.put(child, 1);
                    }
                    candidates.add(child);
                }
                index++;
            }
        } else {
            ArrayList<String> children = commands.getChildren();
            intercept = children.get(0);
        }
        return intercept;
    }

    private boolean isNextQuestion() {
        String currentNode = commands.getCurrentCommand();
        boolean isQuestions = false;
        if (!commands.isNextChoice()) {
            commands.setNextCommand(commands.getChildren().get(0));
            if (commands.isNextChoice()) {
                isQuestions = true;
            }
        }
        commands.setNextCommand(currentNode);
        return isQuestions;
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
                    String command = commands.getCurrentCommand();
                    commands.setNextCommand(previousCommand);
                    robotCommand.sendInfoViaSocket(getBaseContext(), commands.getRepeatCommand());
                    commands.setNextCommand(command);
                    enableButtons(true);
                } else if (viewId == R.id.skipButton) {
                    if (!commands.isNextChoice() && isNextQuestion()) {
                        commands.setNextCommand(commands.getChildren().get(0));
                    }
                    String nextCommand = findIntercept();
                    if (isSkipBranch) {
                        currentCommand = nextCommand;
                    } else {
                        commands.setNextCommand(nextCommand);
                    }
                    prepareNextCommand();
                } else if (viewId == R.id.unexpectedButton) {
//                    Intent intent = new Intent(getApplicationContext(), UnexpectedResponse.class);
//                    startActivity(intent);
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
