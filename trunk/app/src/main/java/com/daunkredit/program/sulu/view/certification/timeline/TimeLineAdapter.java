package com.daunkredit.program.sulu.view.certification.timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {
    BaseActivity activity;
    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(BaseActivity activity, List<TimeLineModel> feedList) {
        this.activity = activity;
        mFeedList = feedList;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view = mLayoutInflater.inflate(R.layout.item_timeline, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        final TimeLineModel model = mFeedList.get(position);
        OrderStatus lastState = OrderStatus.INACTIVE;
        if (position > 0) {
            lastState = mFeedList.get(position - 1).getStatus();
        }

        holder.injectView(model,position + 1,position == getItemCount() - 1,lastState);
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }


}
