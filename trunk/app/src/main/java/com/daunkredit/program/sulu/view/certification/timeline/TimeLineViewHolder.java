package com.daunkredit.program.sulu.view.certification.timeline;

import android.graphics.Color;
import android.graphics.drawable.LevelListDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.hwangjr.rxbus.RxBus;
import com.x.leo.timelineview.TimeLineView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.panel)
    LinearLayout panel;
    @BindView(R.id.icon)
    ImageView    icon;
    @BindView(R.id.timeline_arc_button)
    ImageButton arcButton;
    @BindView(R.id.desc)
    TextView     desc;
    @BindView(R.id.time_marker)
    TimeLineView mTimelineView;
    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void injectView(final TimeLineModel model, int index, boolean isLast, OrderStatus lastState) {
        if (index == 1) {
            mTimelineView.setMarkPosition(TimeLineView.POINT_START);
        }else if(isLast){
            mTimelineView.setMarkPosition(TimeLineView.POINT_END);
        }else{
            mTimelineView.setMarkPosition(TimeLineView.POINT_MIDDLE);
        }
        int localLastState = TimeLineView.INACTIVE;
        switch (lastState) {
            case COMPLETED:
                localLastState = TimeLineView.COMPLETE;
                break;
            case INACTIVE:
                localLastState = TimeLineView.INACTIVE;
                break;
            case ACTIVE:
                localLastState = TimeLineView.ACTIVE;
        }
        mTimelineView.setLastState(localLastState);
        int ordinal = model.getStatus().ordinal();
        icon.setBackgroundResource(model.getIcon());
        desc.setTextColor(ordinal == 1 ? Color.WHITE:desc.getResources().getColor(R.color.ddsilvery));
        desc.setText(model.getDesc());
        LevelListDrawable levelDrawable = (LevelListDrawable)icon.getBackground();

        levelDrawable.setLevel(ordinal);

        levelDrawable = (LevelListDrawable)panel.getBackground();
        levelDrawable.setLevel(ordinal);

        levelDrawable = (LevelListDrawable) arcButton.getBackground();
        levelDrawable.setLevel(ordinal);
        mTimelineView.setText("" + index);
        if(model.getStatus() == OrderStatus.INACTIVE) {
            mTimelineView.setCurrentStatus(TimeLineView.INACTIVE);
        } else if(model.getStatus() == OrderStatus.ACTIVE) {
            mTimelineView.setCurrentStatus(TimeLineView.ACTIVE);
        } else {
            mTimelineView.setCurrentStatus(TimeLineView.COMPLETE);
        }

//        RxView.clicks(panel).throttleFirst(1, TimeUnit.SECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        RxBus.get().post(new CertItemClickedEvent(model.getCertType()));
//                    }
//                });

        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Logger.d(getClass().getName()+" "+ v.toString());
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, model.getDesc()));
                RxBus.get().post(new CertItemClickedEvent(model.getCertType()));
            }
        });
    }
    public static class CertItemClickedEvent{
        TimeLineModel.CertType certType;
        public CertItemClickedEvent(TimeLineModel.CertType certType){
            this.certType = certType;
        }
        public TimeLineModel.CertType getCertType(){
            return certType;
        }
    }
}
