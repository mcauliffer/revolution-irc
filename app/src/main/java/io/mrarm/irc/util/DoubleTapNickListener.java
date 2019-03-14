package io.mrarm.irc.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import io.mrarm.irc.R;
//import io.mrarm.irc.chat.ChatFragmentSendMessageHelper;
import io.mrarm.irc.chat.ChatFragment;
import io.mrarm.irc.chat.ChatFragmentSendMessageHelper;
import io.mrarm.irc.chat.ChatSuggestionsAdapter;
import io.mrarm.irc.config.NickAutocompleteSettings;
import io.mrarm.irc.view.ChatAutoCompleteEditText;

import android.widget.TextView;
import android.widget.Toast;

public class DoubleTapNickListener implements RecyclerView.OnItemTouchListener {

    private RecyclerView mRecyclerView;
    private ChatFragmentSendMessageHelper mChatFragmentSendMessageHelper;
    private Rect mTempRect = new Rect();

    private ChatAutoCompleteEditText mSendText;

    int clickCount = 0;
    long startTime;
    long duration;
    static final int MAX_DURATION = 500;
    static final int MIN_DURATION = 50;

    public DoubleTapNickListener(RecyclerView recyclerView, ChatFragmentSendMessageHelper chatFragmentSendMessageHelper) {
        mRecyclerView = recyclerView;
        mChatFragmentSendMessageHelper = chatFragmentSendMessageHelper;
    }


    public void dblTap (RecyclerView recyclerView, MotionEvent motionEvent)
    {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        x = Math.max(Math.min(x, mRecyclerView.getWidth()), 0);
        y = Math.max(Math.min(y, mRecyclerView.getHeight()), 0);

        String textContent="";

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            view.getHitRect(mTempRect);
            if (mTempRect.contains(x, y)) {
                TextView tv = ((TextView ) view);
                textContent = tv.getText().toString();
                break;
            }
        }
        if (textContent.length()>0) {
            String[] parts = textContent.trim().replaceAll(" +", " ").split(" ");

            if(parts.length>0) {
                String nick = parts[1];
                if ((nick.equals("*"))&&(parts.length>1)) {
                    nick = parts[2];
                }
                mChatFragmentSendMessageHelper.setMessageText(nick);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        switch(motionEvent.getAction() & motionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                clickCount++;
                break;
            case MotionEvent.ACTION_UP:
                long time = System.currentTimeMillis() - startTime;
                duration=  duration + time;
                if(clickCount == 2)
                {
                    if((duration <= MAX_DURATION)&&(duration >= MIN_DURATION))
                    {
                        dblTap(recyclerView,motionEvent);
                    }
                    clickCount = 0;
                    duration = 0;
                    break;
                }
        }

        return false;
    }


    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        onInterceptTouchEvent(recyclerView, motionEvent);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {
    }

}




