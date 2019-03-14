package io.mrarm.irc.util;

import android.content.res.Resources;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.mrarm.irc.config.ChatSettings;
import io.mrarm.irc.config.NickAutocompleteSettings;
import io.mrarm.irc.config.SettingsHelper;

import android.widget.Toast;

public class DoubleTapNickListener implements RecyclerView.OnItemTouchListener {

    private boolean mSelectMode = false;
    private RecyclerView mRecyclerView;
    private Listener mListener;
    private Rect mTempRect = new Rect();
    private long mStartElementId = -1;
    private long mEndElementId = -1;
    private RecyclerViewScrollerRunnable mScroller;

    int clickCount = 0;
    long startTime;
    long duration;
    static final int MAX_DURATION = 500;

    public DoubleTapNickListener(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mScroller = new RecyclerViewScrollerRunnable(recyclerView, (int scrollDir) -> {
            LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            updateHighlightedElements(mRecyclerView, mRecyclerView.getAdapter().getItemId(
                    scrollDir > 0
                            ? llm.findLastCompletelyVisibleItemPosition()
                            : llm.findFirstCompletelyVisibleItemPosition()));
        });
    }

    public void startSelectMode(long startPos) {
        mSelectMode = true;
        mStartElementId = startPos;
        mEndElementId = -1;
        mListener.onElementSelected(mRecyclerView, startPos);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public boolean isElementHighlighted(long id) {
        return id == mStartElementId ||
                (id >= mStartElementId && id <= mEndElementId) ||
                (id <= mStartElementId && id >= mEndElementId && mEndElementId != -1);
    }

    private void updateHighlightedElements(RecyclerView recyclerView, long endId) {
        if (mStartElementId == -1) {
            mStartElementId = endId;
            mListener.onElementHighlighted(recyclerView, mStartElementId, true);
            return;
        }
        for (long i = Math.max(mEndElementId, mStartElementId) + 1; i <= endId; i++)
            mListener.onElementHighlighted(recyclerView, i, true);
        for (long i = Math.min(mEndElementId == -1 ? mStartElementId : mEndElementId,
                mStartElementId) - 1; i >= endId; i--)
            mListener.onElementHighlighted(recyclerView, i, true);

        if (mEndElementId != -1) {
            for (long i = Math.max(endId, mStartElementId) + 1; i <= mEndElementId; i++)
                mListener.onElementHighlighted(recyclerView, i, false);
            for (long i = Math.min(endId, mStartElementId) - 1; i >= mEndElementId; i--)
                mListener.onElementHighlighted(recyclerView, i, false);
        }

        mEndElementId = endId;
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
                        Toast.makeText(recyclerView.getContext(), "DblClick", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(recyclerView.getContext(), "Touch", Toast.LENGTH_SHORT).show();

        onInterceptTouchEvent(recyclerView, motionEvent);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {
    }

    public interface Listener {

        void onElementSelected(RecyclerView recyclerView, long adapterPos);

        void onElementHighlighted(RecyclerView recyclerView, long adapterId, boolean highlight);
    }



}
