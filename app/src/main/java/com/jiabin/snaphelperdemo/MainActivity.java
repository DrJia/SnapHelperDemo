package com.jiabin.snaphelperdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecy;
    private View mEdgeShadow;
    private PubMusicAdapter adapter;
    private LinearLayoutManager manager;
    private PubMusicScrollListener mScrollChangeListener;

    private int padding = 30;

    private Button hide2Btn;
    private TextView text2;

    private RecyclerResizeManager mRecyclerResizeManager;

    boolean mHasData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.down_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateDownloading(0, true);
            }
        });

        findViewById(R.id.undown_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateDownloading(0, false);
            }
        });

        findViewById(R.id.down_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateDownloading(1, true);
            }
        });

        findViewById(R.id.undown_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateDownloading(1, false);
            }
        });

        findViewById(R.id.i_playing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateCurrentPlaying();
            }
        });

        findViewById(R.id.i_loading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateCurrentLoading();
            }
        });

        findViewById(R.id.i_pausing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateCurrentPause();
            }
        });

        findViewById(R.id.i_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateCurrentError();
            }
        });

        findViewById(R.id.loading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.showLoadingView("音乐加载中");
            }
        });

        findViewById(R.id.error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.showErrorView("加载失败，请重试");
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearCurrentActivatingPos();
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.reset();
            }
        });

        findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHasData){
                    boolean isVisible = mEdgeShadow.getVisibility() == View.VISIBLE;
                    mEdgeShadow.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                }
            }
        });

        text2 = findViewById(R.id.text2);

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.smoothScrollToPostion(manager, 0);
            }
        });

        findViewById(R.id.user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateUserMusicMeta(getRandomUser());
                //adapter.updateActivatingPos(0);
                //adapter.smoothScrollToPostion(manager, 0);
            }
        });

        hide2Btn = findViewById(R.id.hide2);
        String hide2String = text2.getVisibility() == View.GONE ? "show2" : "hide2";
        hide2Btn.setText(hide2String);
        hide2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text2.getVisibility() == View.GONE) {
                    text2.setVisibility(View.VISIBLE);
                } else {
                    text2.setVisibility(View.GONE);
                }
                String hide2String = text2.getVisibility() == View.GONE ? "show2" : "hide2";
                hide2Btn.setText(hide2String);

//                Log.d("jiabin","width:" + mRecy.getWidth());
//                adapter.setRecyclerViewWidth(mRecy.getWidth());
            }
        });

        findViewById(R.id.list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setRecommandList(getRandomList());
                //int curPage = mScrollChangeListener.getCurrentPage();
                //adapter.updateActivatingPos(curPage);
            }
        });

        mRecy = (RecyclerView) findViewById(R.id.recy);
        mEdgeShadow = findViewById(R.id.edge_shadow);

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mScrollChangeListener = new PubMusicScrollListener(manager, new PubMusicScrollListener.OnPageSelectedCallback() {
            @Override
            public void onPageSelected(int currentPos, int lastPos) {
                Log.d("jiabin", "currentPos:" + currentPos + " | lastPos:" + lastPos);
                //Toast.makeText(getApplicationContext(), "currentPos:" + currentPos + " | lastPos:" + lastPos, Toast.LENGTH_SHORT).show();
                adapter.updateActivatingPos(currentPos);
                //adapter.updateCurrentLoading();
                //mockLoading();
            }
        });

        adapter = new PubMusicAdapter(this, padding, mScrollChangeListener);

        adapter.setDataChangedListener(new PubMusicAdapter.DataChangedListener() {
            @Override
            public void onDataChanged(boolean hasData) {
                mHasData = hasData;
                mEdgeShadow.setVisibility(hasData ? View.VISIBLE : View.GONE);
            }
        });

        mRecy.setLayoutManager(manager);

        //mRecy.addItemDecoration(new ItemDecoration(30));

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add("" + i);
        }

        mRecy.setAdapter(adapter);

        PubMusicSnapHelper pubMusicSnapHelper = new PubMusicSnapHelper();
        pubMusicSnapHelper.attachToRecyclerView(mRecy);

        adapter.setItemClickListener(new PubMusicAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos, PubMusicState state) {
                adapter.smoothScrollToPostion(manager, pos);
                String log = state.toString();
                Toast.makeText(getApplicationContext(), log, Toast.LENGTH_SHORT).show();
                Log.d("jiabin", log);
            }

            @Override
            public void onErrorClick() {
                Toast.makeText(getApplicationContext(), "onErrorClick", Toast.LENGTH_SHORT).show();
            }
        });


        mRecy.addOnScrollListener(mScrollChangeListener);

        mRecyclerResizeManager = new RecyclerResizeManager(mRecy, adapter);
        mRecyclerResizeManager.addListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecy.removeOnScrollListener(mScrollChangeListener);
        mRecyclerResizeManager.removeListener();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //adapter.setRecyclerViewWidth(mRecy.getWidth());
    }

    private ArrayList<PubMusicMeta> getRandomList() {
        Random random = new Random();
        int num = random.nextInt(100);
        ArrayList<PubMusicMeta> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            PubMusicMeta meta = new PubMusicMeta();
            meta.name = (num + i) + ":歌名这么长歌名这么长歌名这么长";
            list.add(meta);
        }

        return list;
    }

    private PubMusicMeta getRandomUser() {
        Random random = new Random();
        int num = random.nextInt(100);
        PubMusicMeta meta = new PubMusicMeta();
        meta.name = num + ":自选自选自选自选自选自选自选自选";
        return meta;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.updateCurrentError();
                    break;
            }
        }
    };

    private void mockLoading() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

}
