package com.research.caninerobotstudy.userinterface;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandNode {
//    private String commandName = "";
//    private ArrayList<CommandNode> children = new ArrayList<CommandNode>();
    private Context context;
    private JSONObject data;
    private String currentCommand = "";
//    private ArrayList<String> previousCommand = new ArrayList<String>();
    private String section = ""; // used to form command to send to robot
    private String sectionKeyword = ""; // used to retrieve command from data json
//    private boolean fullFlag = true;

    public CommandNode(String currentSection, String sectionKey, Context currentContext){
        section = currentSection;
        sectionKeyword = sectionKey;
        context = currentContext;
        currentCommand = context.getString(R.string.robot_command_start);
        data = readJSONFromAsset();
    }

    private JSONObject readJSONFromAsset(){
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return new JSONObject(json).getJSONObject(sectionKeyword);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }  catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public CommandNode add(String command) {
//        CommandNode child = new CommandNode(command, context);
//        return add(child);
//    }
//
//    public CommandNode add(CommandNode command) {
//        children.add(command);
//        return command;
//    }

    public ArrayList<String> getChildren() {
        ArrayList<String> children = new ArrayList<String>();
        try {
             JSONArray jsonChildren = data.getJSONArray(currentCommand);
             for (int i = 0; i < jsonChildren.length(); i++) {
                 children.add(jsonChildren.getString(i));
             }
        } catch (JSONException e) {
            e.printStackTrace();
        };
        return children;
    }

    public String getCommand() {
        return currentCommand;
    }

    public String getFullCommand() {
        return section + context.getString(R.string.robot_command_deliminator) + currentCommand + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_full);
    }

    public String getRepeatCommand() {
        return section + context.getString(R.string.robot_command_deliminator) + currentCommand + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_repeat);
    }

    public boolean isFinish() {
        return getChildren().isEmpty();
    }

    public boolean isNextChoice() {
        return getChildren().size() > 1;
    }

    public boolean isStart() {
        return currentCommand.equals(context.getString(R.string.robot_command_start));
    }

    public void setNextCommand(String command) {
//        previousCommand.add(currentCommand);
        currentCommand = command;
    }

    public void reset() {
        currentCommand = context.getString(R.string.robot_command_start);
    }

//    public String reverseCommand() {
//        currentCommand = previousCommand.remove(previousCommand.size() - 1);
//        fullFlag = false;
//        return currentCommand;
//    }

}
