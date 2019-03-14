package io.mrarm.irc.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

public class DoubleTapNickListener implements RecyclerView.OnItemTouchListener {

    private RecyclerView mRecyclerView;
    private Rect mTempRect = new Rect();

    int clickCount = 0;
    long startTime;
    long duration;
    static final int MAX_DURATION = 500;

    public DoubleTapNickListener(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    public void dblTap (RecyclerView recyclerView, MotionEvent motionEvent)
    {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        x = Math.max(Math.min(x, mRecyclerView.getWidth()), 0);
        y = Math.max(Math.min(y, mRecyclerView.getHeight()), 0);

        CharSequence textContent="";


        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            view.getHitRect(mTempRect);
            if (mTempRect.contains(x, y)) {
                TextView tv = ((TextView ) view);
                textContent = tv.getText();
                break;
            }
        }
        if (textContent.length()>0) {

            Toast.makeText(recyclerView.getContext(), textContent, Toast.LENGTH_SHORT).show();

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
                    if(duration<= MAX_DURATION)
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




