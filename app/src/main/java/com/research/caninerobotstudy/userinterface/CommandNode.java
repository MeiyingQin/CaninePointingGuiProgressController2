package com.research.caninerobotstudy.userinterface;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandNode {
    private Context context;
    private JSONObject data;
    private String currentCommand;
    private String section; // used to form command to send to robot
    private String sectionKeyword; // used to retrieve command from data json

    CommandNode(String currentSection, String sectionKey, Context currentContext){
        section = currentSection;
        sectionKeyword = sectionKey;
        context = currentContext;
        currentCommand = context.getString(R.string.robot_command_start);
        data = readJSONFromAsset();
    }

    private JSONObject readJSONFromAsset(){
        JSONObject jsonObject = null;
        try {
            InputStream is;
            is = context.getAssets().open("data_shortened.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();
            if (read > 0) {
                String json = new String(buffer, "UTF-8");
                jsonObject = new JSONObject(json).getJSONObject(sectionKeyword);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    private String findIntercept() {
        String intercept = "";

        if (isNextChoice()) {
            int index = 0;
            int branches = getChildren().size();
            ArrayList<String> candidates = new ArrayList<>(getChildren());
            boolean isFound = false;
            HashMap<String, Integer> intersect = new HashMap<>();
            while (!isFound && index < candidates.size()) {
                setCurrentCommand(candidates.get(index));
                if (isFinish()) {
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
        } else if (getChildren().size() == 1 ){
            intercept = getChildren().get(0);
        } else {
            intercept = "";
        }

        return intercept;
    }

    private String getParentHelper(String child) {
        String parent = "";
        if (!getChildren().contains(child)) {
            for(String offspring : getChildren()) {
                currentCommand = offspring;
                parent = getParentHelper(child);
                if (!parent.isEmpty()) {
                    break;
                }
            }
        } else {
            parent = currentCommand;
        }
        return parent;
    }

    public String getCurrentCommand() {
        return currentCommand;
    }

    public ArrayList<String> getChildren() {
        ArrayList<String> children = new ArrayList<>();
        try {
             JSONArray jsonChildren = data.getJSONArray(currentCommand);
             for (int i = 0; i < jsonChildren.length(); i++) {
                 children.add(jsonChildren.getString(i));
             }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return children;
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

    public boolean isNextQuestion() {
        boolean isQuestion = false;
        if (getChildren().size() == 1) {
            String current = currentCommand;
            currentCommand = getChildren().get(0);
            if (isNextChoice()) {
                isQuestion = true;
            }
            currentCommand = current;
        }
        return isQuestion;
    }

    public boolean isNextChoice() {
        return getChildren().size() > 1;
    }

    public boolean isStart() {
        return currentCommand.equals(context.getString(R.string.robot_command_start));
    }

    public void setCurrentCommand(String command) {
        currentCommand = command;
    }

    public void reset() {
        currentCommand = context.getString(R.string.robot_command_start);
    }

    public String getParent(String child) {
        String current = currentCommand;
        reset();
        String parent = getParentHelper(child);
        currentCommand = current;
        return parent;
    }

    public void skip() {
        currentCommand = findIntercept();
    }

}
