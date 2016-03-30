package com.example.ninasmacpro.mavigation;

import java.util.TimerTask;

/**
 * Created by ninasmacpro on 16/3/29.
 */
public class MessageTask extends TimerTask {
    private MessageFragment mMessageFragment = null;


    MessageTask(MessageFragment messageFragment) {
        mMessageFragment = messageFragment;
    }

    @Override
    public void run() {
        mMessageFragment.getMessagesFromParse();
    }
}