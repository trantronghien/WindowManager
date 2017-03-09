package com.example.admin.windowmanager;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Process;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by admin on 3/9/2017.
 */

public class MyService extends Service implements View.OnTouchListener,View.OnClickListener,SmartEditText.OnRemoveFocusListerner{
    private WindowManager windowManager;
    private MyViewGroup myViewGroup;
    private WindowManager.LayoutParams mParams;
    private View subView;
    private TextView tv_message;
    private SmartEditText edt_message;
    private Button bt_send;
    Button btn_close;
    private int DOWN_X, DOWN_Y,MOVE_X,MOVE_Y,xparam,yparam;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initView();

    }

    private void initView() {
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        myViewGroup = new MyViewGroup(getBaseContext());
        LayoutInflater minflater = LayoutInflater.from(this);
        subView = minflater.inflate(R.layout.windowmanager,myViewGroup);// nhet cai main vao cai viewGroup, de anh xa ra subView
        //dinh nghia param
        mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER;
        mParams.format = PixelFormat.TRANSLUCENT;//trong suot
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;// noi tren all be mat
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// khong bi gioi han boi man hinh|Su duoc nut home
        windowManager.addView(myViewGroup,mParams);
        subView.setOnTouchListener(this);

        tv_message = (TextView)subView.findViewById(R.id.tv_message);
        edt_message = (SmartEditText)subView.findViewById(R.id.edt_edittext);
        edt_message.setOnClickListener(this);
        edt_message.setOnRemoveFocusListener(MyService.this);
        btn_close = (Button)subView.findViewById(R.id.btn_close); btn_close.setOnClickListener(this);
        bt_send = (Button)subView.findViewById(R.id.bt_send);bt_send.setOnClickListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xparam = mParams.x;
                yparam = mParams.y;
                DOWN_X = (int)event.getRawX();
                DOWN_Y = (int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                MOVE_X = (int) event.getRawX()- DOWN_X ;
                MOVE_Y = (int)event.getRawY()- DOWN_Y ;
                updateView(MOVE_X, MOVE_Y);
                break;
        }
        return true;
    }

    private void updateView(int x, int y) {
        mParams.x = x+xparam;
        mParams.y=y+yparam;
        windowManager.updateViewLayout(myViewGroup,mParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edt_edittext:
                focusToView();
                break;
            case R.id.bt_send:
                tv_message.setText(edt_message.getText().toString());
                edt_message.setText("");
                break;
            case R.id.btn_close:
                this.stopSelf();
                break;
        }
    }

    public void focusToView(){
        mParams.flags = mParams.flags &~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManager.updateViewLayout(myViewGroup,mParams);
    }
    public void removeFocusInView(){
        mParams.flags = mParams.flags|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    @Override
    public void removeFocus() {
        removeFocusInView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        System.exit(0);
        Process.killProcess(Process.myPid());
    }
}