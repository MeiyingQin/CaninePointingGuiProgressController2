package com.research.caninerobotstudy.userinterface;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Meiying on 1/24/2018.
 */

public class RobotCommand {
    private static String ownerName = "";
    private static String dogName = "";
    private static String dogGender = "";
    private static Socket socket = null;
    private Context context;

    public RobotCommand() {

    }

    public RobotCommand(String ip, int port, String ownerNameInput, String dogNameInput, String dogGenderInput) {
        if (ownerName.isEmpty()) {
            ownerName = ownerNameInput;
        }
        if (dogName.isEmpty()) {
            dogName = dogNameInput;
        }
        if (dogGender.isEmpty()) {
            dogGender = dogGenderInput;
        }

        if (socket == null) {
            try {
                InetAddress serverAddress = InetAddress.getByName(ip);
                socket = new Socket(serverAddress, port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendInfoViaSocket(Context context, String message) {
        Log.d("[sendInfoSocketSTART]", Calendar.getInstance().getTime().toString());

        message = message.replaceAll(context.getString(R.string.robot_dog_name_placeholder), dogName);
        message = message.replaceAll(context.getString(R.string.robot_owner_name_placeholder), ownerName);
        message = message.replaceAll(context.getString(R.string.robot_dog_gender_placeholder), dogGender);

        final String requestMessage = message + "\n";
        final Context currentContext = context;
//        String serverMessage = "";

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable<Boolean> callableCommand = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                boolean returnValue = false;
                try{
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println(requestMessage);
                    out.flush();

                    String serverMessage = in.readLine();
                    Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

                    returnValue = serverMessage.equals(currentContext.getString(R.string.connection_respond));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return returnValue;
            }
        };
        Future<Boolean> result = executorService.submit(callableCommand);
        boolean returnValue = false;
        try {
            returnValue = result.get().booleanValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("[sendInfoSocketEND]", Calendar.getInstance().getTime().toString());

        return returnValue;
    }

    public CommandNode getCommands(String section, String sectionKey, Context context) {
        return new CommandNode(section, sectionKey, context);
    }

//    public CommandNode introductionSession(Context context) {
//        CommandNode start = new CommandNode("", context);
//
////        ArrayList<String> commands = new ArrayList<String>();
////        commands.add(context.getString(R.string.robot_command_stop_idle));
////        commands.add(context.getString(R.string.robot_command_stand));
////        commands.add(context.getString(R.string.robot_command_introduction_1) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_owner_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_introduction_1_description));
////        commands.add(context.getString(R.string.robot_command_introduction_2) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_owner_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_introduction_2_description));
////        commands.add(context.getString(R.string.robot_command_introduction_3) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_owner_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_introduction_3_description));
////        commands.add(context.getString(R.string.robot_command_introduction_4) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_owner_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_introduction_4_description));
////        commands.add(context.getString(R.string.robot_command_introduction_5) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_owner_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_introduction_5_description));
////        commands.addAll(bait(context, true, false, true, false));
////        commands.add(context.getString(R.string.robot_command_introduction_6) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_owner_name_placeholder) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_command_introduction_6_description));
////        commands.add(context.getString(R.string.robot_command_start_idle));
//        return start;
//    }
//
//    public CommandNode getDogScaredCommands(Context context) {
//        CommandNode start = new CommandNode("", context);
//        // add in commands
//        return start;
//    }
//
//    public ArrayList<String> warmupSessionIntro(Context context) {
//        ArrayList<String> commands = new ArrayList<String>();
//        return commands;
//    }
//
//    public ArrayList<String> testSessionIntro(Context context) {
//        ArrayList<String> commands = new ArrayList<String>();
//        return commands;
//    }

//    public ArrayList<String> bait(Context context, boolean isLeft, boolean isWarmup, boolean isIntroduction, boolean isSetIdle) {
//        ArrayList<String> commands = new ArrayList<String>();
//        if (isSetIdle) {
//            commands.add(context.getString(R.string.robot_command_stop_idle));
//        }
//        commands.add(context.getString(R.string.robot_command_stand));
//
//        if (isWarmup) {
//            commands.add(context.getString(R.string.robot_command_get_treat));
//            commands.add(context.getString(R.string.robot_command_get_spoon));
//            if (isLeft) {
//                commands.add(context.getString(R.string.robot_command_warmup_bait_left));
//            } else {
//                commands.add(context.getString(R.string.robot_command_warmup_bait_right));
//            }
//            commands.add(context.getString(R.string.robot_command_release_spoon));
//            commands.add(context.getString(R.string.robot_command_release_dog));
//        }
//
//        if (isIntroduction) {
//            commands.add(context.getString(R.string.robot_command_release_spoon));
//        }
//
//        if (!isWarmup && !isIntroduction) { // testing
////            commands.add(context.getString(R.string.robot_command_release_spoon));
//            if (isLeft) {
//                commands.add(context.getString(R.string.robot_command_point_left) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder));
//            } else {
//                commands.add(context.getString(R.string.robot_command_point_right) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_name_placeholder));
//            }
//        }
////        commands.add(context.getString(R.string.robot_command_praise) + context.getString(R.string.robot_command_deliminator) + context.getString(R.string.robot_dog_gender_placeholder));
//        commands.add(context.getString(R.string.robot_command_stand_back));
//
//        if (isSetIdle) {
//            commands.add(context.getString(R.string.robot_command_start_idle));
//        }
//
//        return commands;
//    }

}
