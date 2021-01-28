package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.helpManager.helpController;
import com.darkweb.genesissearchengine.appManager.settingManager.logManager.settingLogController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.SimpleGestureFilter;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.Collections;
import java.util.List;

public class orbotLogController extends AppCompatActivity {

    /* PRIVATE VARIABLES */
    private orbotLogModel mOrbotModel;
    private orbotLogViewController mOrbotViewController;
    private RecyclerView mRecycleView;
    private orbotAdapter mOrbotAdapter;
    private NestedScrollView mMainScroll;
    private FloatingActionButton mFloatingScroller;

    private TextView mLogs;
    private boolean mActivityClosed = false;
    private int mLogCounter = 0;
    private GestureDetector mSwipeDirectionDetector;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.push_anim_out_reverse, R.anim.push_anim_in_reverse);

        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_log_view);

        viewsInitializations();
        onUpdateLogs();
        initializeLogs();
        onInitListener();
    }

    public void viewsInitializations() {
        mRecycleView = findViewById(R.id.pLogRecycleView);
        mLogs = findViewById(R.id.pLogs);
        mMainScroll = findViewById(R.id.mMainScroll);
        mFloatingScroller = findViewById(R.id.pFloatingScroller);

        activityContextManager.getInstance().setOrbotLogController(this);
        activityContextManager.getInstance().onStack(this);
        mOrbotViewController = new orbotLogViewController(this, mLogs, mRecycleView);
        mOrbotModel = new orbotLogModel();

    }

    public void initializeLogs(){
        mLogCounter = 0;
        if(status.sLogListView){
            mLogCounter = orbotLocalConstants.mTorLogsHistory.size();
            mOrbotModel.setList(orbotLocalConstants.mTorLogsHistory);
            LinearLayoutManager layoutManager = new LinearLayoutManager(orbotLogController.this);
            orbotAdapter adapter = new orbotAdapter(mOrbotModel.getList(),new orbotLogController.orbotModelCallback());
            mOrbotAdapter = adapter;
            layoutManager.setReverseLayout(true);

            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.log_layout_controller);
            mRecycleView.setLayoutAnimation(controller);

            mRecycleView.setAdapter(adapter);
            mRecycleView.setItemViewCacheSize(100);
            mRecycleView.setNestedScrollingEnabled(false);
            mRecycleView.setItemViewCacheSize(100);
            mRecycleView.setDrawingCacheEnabled(true);
            mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            mRecycleView.setLayoutManager(new LinearLayoutManager(orbotLogController.this));
            mOrbotAdapter.notifyDataSetChanged();
        }
        mOrbotViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_INIT_VIEWS, null);
        mRecycleView.smoothScrollToPosition(mOrbotModel.getList().size());
    }

    /* LISTENERS */

    private void onInitListener(){
        mMainScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = mMainScroll.getScrollY();
            if(scrollY>0){
                if(mFloatingScroller.getAlpha()==0){
                    mFloatingScroller.setVisibility(View.VISIBLE);
                    mFloatingScroller.animate().alpha(1);
                }
            }else {
                mFloatingScroller.animate().cancel();
                mFloatingScroller.animate().alpha(0).withEndAction(() -> mFloatingScroller.setVisibility(View.GONE));
            }
        });

        mSwipeDirectionDetector=new GestureDetector(this,new SimpleGestureFilter(){

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction==Direction.left || direction==Direction.right){
                    finish();
                    overridePendingTransition(R.anim.push_anim_in, R.anim.push_anim_out);
                }
                return true;
            }
        });
    }

    public void onUpdateLogs(){
        new Thread(){
            public void run(){
                while (true){
                    try {
                        if(mActivityClosed){
                            break;
                        }
                        if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                            if(!status.sLogListView){
                                sleep(0);
                            }else {
                                sleep(500);
                            }
                        }else {
                            sleep(500);
                        }

                        if(orbotLocalConstants.mTorLogsHistory.size()>0){
                            runOnUiThread(() -> {
                                if(orbotLocalConstants.mTorLogsHistory.size()>mLogCounter){
                                    mOrbotModel.getList().add(0,orbotLocalConstants.mTorLogsHistory.get(mLogCounter));
                                    if(!status.sLogListView){
                                        mOrbotViewController.onTrigger(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS, Collections.singletonList(orbotLocalConstants.mTorLogsHistory.get(mLogCounter).getLog()));
                                    }else {
                                        mOrbotAdapter.notifyItemInserted(0);
                                        mOrbotAdapter.notifyItemChanged(0);
                                    }
                                    mLogCounter+=1;
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void onScrollTop(View view) {
        mMainScroll.smoothScrollTo(0,0);
    }

    public void onOpenInfo(View view) {
        helperMethod.openActivity(settingLogController.class, constants.CONST_LIST_HISTORY, this,true);
    }

    public class orbotModelCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            return null;
        }
    }

    /* Helper Methods */

    public void onClose(View view){
        finish();
        activityContextManager.getInstance().onRemoveStack(this);
        overridePendingTransition(R.anim.push_anim_in, R.anim.push_anim_out);
        mActivityClosed = true;
    }

    /* LOCAL OVERRIDES */

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onClose(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mSwipeDirectionDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

}