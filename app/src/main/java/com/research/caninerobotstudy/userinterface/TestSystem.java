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
    private ArrayList<Button> buttons =new ArrayList<Button>();
    private CommandNode commands = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_system);
        buttons.add((Button)findViewById(R.id.unexpectedButton));
        buttons.add((Button)findViewById(R.id.dogScaredButton));
        buttons.add((Button)findViewById(R.id.introductionSessionButton));
//        buttons.add((Button)findViewById(R.id.warmupButton));
//        buttons.add((Button)findViewById(R.id.testButton));

        String section = getString(R.string.robot_command_section_test_system);
        String sectionKeyword = getString(R.string.robot_command_section_keyword_test_system);

        Context context = getApplicationContext();
        commands = new CommandNode(section, sectionKeyword, context);

        final ArrayList<String> children = commands.getChildren();
        ListView choiceListView = (ListView) findViewById(R.id.command_list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_custom, children);
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
        final boolean enable = isEnable;
        runOnUiThread(new Runnable() {
            public void run() {
                for (int i = 0; i < buttons.size(); i++) {
                    buttons.get(i).setEnabled(enable);
                }
                findViewById(R.id.command_list_view).setEnabled(isEnable);
            }
        });
    }

//    private class CommunicateRobot extends AsyncTask<String, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(String... commands) {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            Log.d("[onPostExecuteEND]", Calendar.getInstance().getTime().toString());
//            if (result) {
//                enableButtons(true);
//            }
//        }
//    }

//    public void executeCommand(View view) {
//
//        enableButtons(false);
//
//        Log.d("[executeCommandSTART]", Calendar.getInstance().getTime().toString());

//        CommunicateRobot task = new CommunicateRobot();
//        task.execute("command");
//        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        Callable<Boolean> callableCommand = new Callable<Boolean>() {
//            @Override
//            public Boolean call() {
//                try {
////                    RobotCommand robotCommand = new RobotCommand();
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        };
//        Future<Boolean> result = executorService.submit(callableCommand);
//        boolean returnValue = false;
//        try {
//            returnValue = result.get().booleanValue();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        final View viewThread = view;
//        new Thread(new Runnable() {
//            public void run() {
//                RobotCommand robotCommand = new RobotCommand();
//                if (viewThread.getId() == R.id.sayDogNameButton) {
//                    String message = getString(R.string.robot_command_command_talk) + getString(R.string.robot_command_deliminator) + getString(R.string.robot_dog_name_placeholder);
//                    robotCommand.sendInfoViaSocket(getBaseContext(), message);
//                } else if (viewThread.getId() == R.id.sayOwnerNameButton) {
//                    String message = getString(R.string.robot_command_command_talk) + getString(R.string.robot_command_deliminator) + getString(R.string.robot_owner_name_placeholder);
//                    robotCommand.sendInfoViaSocket(getBaseContext(), message);
//                } else if (viewThread.getId() == R.id.standButton) {
//                    robotCommand.sendInfoViaSocket(getBaseContext(), getString(R.string.robot_command_stand));
//                } else if (viewThread.getId() == R.id.restButton) {
//                    robotCommand.sendInfoViaSocket(getBaseContext(), getString(R.string.robot_command_rest));
//                } else if (viewThread.getId() == R.id.startIdleButton) {
//                    robotCommand.sendInfoViaSocket(getBaseContext(), getString(R.string.robot_command_start_idle));
//                } else if (viewThread.getId() == R.id.stopIdleButton) {
//                    robotCommand.sendInfoViaSocket(getBaseContext(), getString(R.string.robot_command_stop_idle));
//                }else {
//                    ArrayList<String> commands = getCommands(viewThread.getId(), robotCommand);
//                    Intent intent = new Intent(getApplicationContext(), RobotActionsCollectionManager.class);
//                    intent.putStringArrayListExtra(getString(R.string.robot_actions_collection_manager_command_extra), commands);
//                    startActivity(intent);
//                }
//                enableButtons(true);
//            }
//        }).start();
//
//        Log.d("[executeCommandEND]", Calendar.getInstance().getTime().toString());
//    }

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
