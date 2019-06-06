package com.piya.driveMode;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import static android.content.ContentValues.TAG;

public class NotificationReceiver extends NotificationListenerService {
    static boolean isRunning = false;//is Running Bool
    static Notification notification;
    static Bundle bundle;
    static ArrayList<RemoteInput> remoteInputs;
    static Cursor cursor;
    static Context mContext;

    SharedPreferences sharedPreferences;
    SharedPreferences callSharedPreferences;
    SharedPreferences callSharedPreferences1;
    MainActivity mn = new MainActivity();

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        //super.onNotificationPosted(sbn);

         if(Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners").contains(getPackageName())// Check for Notification Permission
                        && isOn() && sbn != null && !sbn.isOngoing() && sbn.getPackageName().equals(Const.PKG)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                notification = sbn.getNotification();//Latest Notification of WhatsApp
                                if(notification != null){
                                    bundle = notification.extras;
                                    //logBundle(bundle);
                                    remoteInputs = getRemoteInputs(notification);
                                    if(remoteInputs != null && remoteInputs.size() > 0){
                                        Object isGroupConversation = bundle.get("android.isGroupConversation");
                                        String conversationTitle = bundle.getString("android.conversationTitle");
                                        Log.v(TAG, "+++ ON CREATE +++");
                                        if(isGroupConversation != null){
                                            boolean isGroup = (((boolean) isGroupConversation) && (conversationTitle != null));//Group Params
                                            Object title = bundle.get("android.title");//Chat Title
                                            Object text = bundle.get("android.text");//Chat Text

                                            if((title != null && text != null)&&!isGroup) {



                                                //Common Replies
                                                if(text.equals("#locate")){
                                                    String lat=callSharedPreferences.getString("lat", "null");
                                                    String lng=callSharedPreferences1.getString("lng", "null");
                                                    if(lat.equals("null") || lng.equals("null")){
                                                        sendMsg("Unable to fetch location try after sometime\n\n *Piya bot*");
                                                    }
                                                    else {


                                                            sendMsg("http://maps.google.com/maps?f=q&q="+String.valueOf(lat)+","+String.valueOf(lng));

                                                    }

                                                }
                                                else if (text.equals("#help")) {
                                                    sendMsg("*Piya Bot Service* :-\n\n*#email* - to get my official EMail Address.\n\n *#locate* - To know my location.\n\n *Hi* - To get Hi by my Bot");
                                                } else if (text.equals("#email")) {
                                                    sendMsg("*Piyush Official EMail* :- \n\nkantmaav@gmail.com");
                                                } else if (text.equals("Hi")) {
                                                    sendMsg("Hi\n\n *BY PIYA BOT*");
                                                }else{
                                                    sendMsg("I'm Driving Now. Talk with you later\n\n To know more about Bot Services type *#help*\n\n *BY PIYA BOT*");
                                                }

                                            }
                                        }
                                    }
                                }
                            } catch (Exception e){
                                notification = null;
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }



        public void whatsapCall(String title){

        ContentResolver resolver = mContext.getContentResolver();
            String mimeString = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call";
        cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME);

//Now read data from cursor like

        while (cursor.moveToNext()) {
            long _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

           if(mimeType.equals(mimeString) && displayName.equals(title)){
               Intent intent = new Intent();
               intent.setAction(Intent.ACTION_VIEW);

// the _ids you save goes here at the end of /data/12562
               intent.setDataAndType(Uri.parse("content://com.android.contacts/data/"+String.valueOf(_id)),
                       "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
               intent.setPackage("com.whatsapp");

               startActivity(intent);
           }

        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        sharedPreferences = getSharedPreferences(Const.BOT, Context.MODE_PRIVATE);
        callSharedPreferences = getSharedPreferences(Const.CALLBOT, Context.MODE_PRIVATE);
        callSharedPreferences1 = getSharedPreferences(Const.CALLBOT1, Context.MODE_PRIVATE);

    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private boolean isOn(){
        return sharedPreferences.getBoolean(Const.STATUS, false);
    }

    private String context(){
        return callSharedPreferences.getString("context", null);
    }


    private ArrayList<RemoteInput> getRemoteInputs(Notification notification){
        ArrayList<RemoteInput> remoteInputs = new ArrayList<>();
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(notification);
        for(NotificationCompat.Action act : wearableExtender.getActions()) {
            if(act != null && act.getRemoteInputs() != null) {
                remoteInputs.addAll(Arrays.asList(act.getRemoteInputs()));
            }
        }
        return remoteInputs;
    }

    private void sendMsg(String msg){
        RemoteInput[] allremoteInputs = new RemoteInput[remoteInputs.size()];
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Iterator it = remoteInputs.iterator();
        int i=0;
        while (it.hasNext()) {
            allremoteInputs[i] = (RemoteInput) it.next();
            bundle.putCharSequence(allremoteInputs[i].getResultKey(), msg);//This work, apart from Hangouts as probably they need additional parameter (notification_tag?)
            i++;
        }
        RemoteInput.addResultsToIntent(allremoteInputs, localIntent, bundle);
        try {
            Objects.requireNonNull(replyAction(notification)).actionIntent.send(this, 0, localIntent);
        } catch (PendingIntent.CanceledException e) {
            Log.e(Const.LOG, "replyToLastNotification error: " + e.getLocalizedMessage());
        }
    }


    private NotificationCompat.Action replyAction(Notification notification) {
        NotificationCompat.Action action;
        for (NotificationCompat.Action action2 : new NotificationCompat.WearableExtender(notification).getActions()) {
            if (isAllowFreeFormInput(action2)) {
                return action2;
            }
        }
        if (!(notification == null || notification.actions == null)) {
            for (int i = 0; i < NotificationCompat.getActionCount(notification); i++) {
                action = NotificationCompat.getAction(notification, i);
                if (isAllowFreeFormInput(action)) {
                    return action;
                }
            }
        }
        return null;
    }

    private boolean isAllowFreeFormInput(NotificationCompat.Action action) {
        if (action.getRemoteInputs() == null) {
            return false;
        }
        for (RemoteInput allowFreeFormInput : action.getRemoteInputs()) {
            if (allowFreeFormInput.getAllowFreeFormInput()) {
                return true;
            }
        }
        return false;
    }


}
