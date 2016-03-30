package com.example.ninasmacpro.mavigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView mMessageListView;
    private String mGroupObjectId = null;
    private ArrayList<String> mMessageContent = new ArrayList<String>();
    private ArrayList<String> mMessageSender = new ArrayList<String>();

    private ParseUser mParseUser = null;
    private Button mSendMessageButton;
    private EditText mMessageTextView;

    private TabActivity mTabActivity = null;
    Timer mTimer;
    MessageTask mMessageTask;

    public MessageFragment() {
        // Required empty public constructor
    }

    // when we first enter this fragment, get all previous messages from Parse (for this current group)
    // TODO: for later message, use push notification?
    public void getMessagesFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("objectId", mGroupObjectId);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messageList, ParseException e) {
                if (e == null) {
                    Log.w("get from Parse", "success");
                    mMessageContent.clear();
                    mMessageSender.clear();
                    for (ParseObject message: messageList) {
                        mMessageContent.add((String) message.get("content"));
                        mMessageSender.add((String) message.get("sender"));
                    }
                    displayMessages();
                } else {

                }
            }
        });
    }

    private void displayMessages() {
        mMessageSender.add("Nina"); // dummy values
        mMessageSender.add("Eric");
        mMessageSender.add("Carol");
        mMessageSender.add("Johny");
        mMessageContent.add("hi");
        mMessageContent.add("hi");
        mMessageContent.add("hi");
        mMessageContent.add("hi");
        mMessageSender.add("Nina");
        mMessageSender.add("Eric");
        mMessageSender.add("Carol");
        mMessageSender.add("Johny");
        mMessageContent.add("how's your day?");
        mMessageContent.add("soso");
        mMessageContent.add("good");
        mMessageContent.add("fine");
        ArrayAdapter<String> currentGroupMemberAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.message_list_layout, R.id.text3, mMessageSender) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.text3);
                TextView text2 = (TextView) view.findViewById(R.id.text4);

                text1.setText(mMessageSender.get(position));
                text2.setText(mMessageContent.get(position));
                return view;
            }
        };
        mMessageListView.setAdapter(currentGroupMemberAdapter);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabActivity = (TabActivity) this.getActivity();
        mGroupObjectId = mTabActivity.getMapFragment().getGroupObjectId();
        if (mGroupObjectId != null) {
            startRetrievingGroupMessages(mGroupObjectId);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        mMessageListView = (ListView) rootView.findViewById(R.id.messageListView);

        // see if user is in a group, if yes, pull previous messages from Parse
        mParseUser = ParseUser.getCurrentUser();
        mGroupObjectId = (String) mParseUser.get("groupObjectId");
        if (mGroupObjectId != null && mGroupObjectId != "") {
            getMessagesFromParse();
            checkSendButtonClick(rootView); // send button only work if the user is in a group
        } else { // FIXME: delete this part in real usage
            Log.w("dummy one", "success");
            displayMessages();
            checkSendButtonClick(rootView);
        }

        return rootView;
    }

    private void checkSendButtonClick(final View view) {
        mSendMessageButton = (Button) view.findViewById(R.id.sendMessageButton);
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the message from EditText
                mMessageTextView = (EditText) view.findViewById(R.id.enterMessage);
                String messageContent = mMessageTextView.getText().toString();
                mMessageTextView.setText("");

                // send this message to Parse
                ParseObject newMessage = new ParseObject("Message");
                newMessage.put("sender", mParseUser.get("nickName"));
                newMessage.put("content", messageContent);
                newMessage.put("groupObjectId", mGroupObjectId);
                newMessage.saveInBackground();
                // TODO: Now, how to trigger Parse to send push notification?

            }
        });
    }

    public void startRetrievingGroupMessages(String groupObjectId) {
        mGroupObjectId = groupObjectId;

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mMessageTask = new MessageTask(mTabActivity.getMessageFragment());
        mTimer.schedule(mMessageTask, 1000, 3000); //delay 1000ms, repeat in 3000ms

    }

    public void stopRetrievingGroupMessages() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mGroupObjectId = null;
    }

}
