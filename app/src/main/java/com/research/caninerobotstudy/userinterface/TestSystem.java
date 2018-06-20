package com.research.caninerobotstudy.userinterface;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class TestSystem extends AppCompatActivity {
    private ArrayList<Button> buttons =new ArrayList<>();
    private CommandNode commands = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_system);
        buttons.add((Button)findViewById(R.id.unexpectedButton));
        buttons.add((Button)findViewById(R.id.dogScaredButton));
        buttons.add((Button)findViewById(R.id.introductionSessionButton));

        String section = getString(R.string.robot_command_section_test_system);
        String sectionKeyword = getString(R.string.robot_command_section_keyword_test_system);

        Context context = getApplicationContext();
        commands = new CommandNode(section, sectionKeyword, context);

        final ArrayList<String> children = commands.getChildren();
        ListView choiceListView = findViewById(R.id.command_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.listview_custom, children);
        choiceListView.setAdapter(arrayAdapter);

        choiceListView.setClickable(true);
        choiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enableButtons(false);
                String currentCommand = children.get(i);
                commands.setCurrentCommand(currentCommand);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RobotCommand robotCommand = new RobotCommand();
                        robotCommand.sendInfoViaSocket(getBaseContext(), commands.getFullCommand());
                        commands.reset();
                        enableButtons(true);
                    }
                }).start();
            }
        });
    }

    private void enableButtons(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            public void run() {
                for (int i = 0; i < buttons.size(); i++) {
                    buttons.get(i).setEnabled(isEnable);
                }
                findViewById(R.id.command_list_view).setEnabled(isEnable);
            }
        });
    }

    public void onClick(View view) {
        final int viewId = view.getId();
        enableButtons(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String section = "";
                String sectionKeyword = "";
                if (viewId == R.id.unexpectedButton) {
                    section = getString(R.string.robot_command_section_unexpected_behaviour);
                    sectionKeyword = getString(R.string.robot_command_section_keyword_unexpected_behaviour);
                } else if (viewId == R.id.dogScaredButton) {
                    section = getString(R.string.robot_command_section_dog_scared);
                    sectionKeyword = getString(R.string.robot_command_section_keyword_dog_scared);
                } else if (viewId == R.id.introductionSessionButton) {
                    section = getString(R.string.robot_command_section_introduction);
                    sectionKeyword = getString(R.string.robot_command_section_keyword_introduction);
                }
                Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
                intent.putExtra(getString(R.string.robot_command_section), section);
                intent.putExtra(getString(R.string.robot_command_section_keyword), sectionKeyword);
                startActivity(intent);
                enableButtons(true);
            }
        }).start();
    }

}
