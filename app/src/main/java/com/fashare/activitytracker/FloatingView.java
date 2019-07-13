package com.fashare.activitytracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class FloatingView extends LinearLayout {
    public static final String TAG = "FloatingView";

    private final Context mContext;
    private final WindowManager mWindowManager;
    private TextView mTvPackageName;
    private TextView mTvClassName;
    private ImageView mIvClose;

    public FloatingView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_floating, this);
        mTvPackageName = (TextView) findViewById(R.id.tv_package_name);
        mTvClassName = (TextView) findViewById(R.id.tv_class_name);
        mIvClose = (ImageView) findViewById(R.id.iv_close);

        mIvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "关闭悬浮框", Toast.LENGTH_SHORT).show();
                //启动服务  关闭悬浮框
                mContext.startService(new Intent(mContext, TrackerService.class)
                        .putExtra(TrackerService.COMMAND, TrackerService.COMMAND_CLOSE));
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 处理EventBus消息
     */
    public void onEventMainThread(TrackerService.ActivityChangedEvent event) {
        Log.d(TAG, "event:" + event.getPackageName() + ": " + event.getClassName());
        String packageName = event.getPackageName();
        String className = event.getClassName();
        mTvPackageName.setText(packageName);
        //展示类名   如果是和包名一样，去掉包名展示相对路径   如果不一样，展示完整路径
        mTvClassName.setText(
                className.startsWith(packageName) ?
                        className.substring(packageName.length()) :
                        className
        );
    }

    Point preP, curP;

    /**
     * 处理触摸事件   移动悬浮框位置
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preP = new Point((int) event.getRawX(), (int) event.getRawY());
                break;

            case MotionEvent.ACTION_MOVE:
                curP = new Point((int) event.getRawX(), (int) event.getRawY());

                int dx = curP.x - preP.x;
                int dy = curP.y - preP.y;

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
                layoutParams.x += dx;
                layoutParams.y += dy;
                mWindowManager.updateViewLayout(this, layoutParams);

                preP = curP;
                break;
        }
        return false;
    }
}
