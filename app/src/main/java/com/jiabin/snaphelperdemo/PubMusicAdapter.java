package com.jiabin.snaphelperdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PubMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PubMusicMeta> mRecList = new ArrayList<>();
    private ArrayList<PubMusicMeta> mtotalList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private int mRecyclerViewWidth = 0;
    private int mItemWidth = 0;
    private int mFakeWidth;
    private int mPadding;

    public static final int TYPE_MUSIC = 101;
    public static final int TYPE_FAKE = 102;

    private ItemClickListener mItemClickListener;

    private TopSmoothScroller topSmoothScroller;
    private TopSmoothScroller0 topSmoothScroller0;

    private PubMusicMeta mUserPubMusicMeta;

    private PubMusicScrollListener mPubMusicScrollListener;

    private String mLoadingText;
    private String mErrorText;


    public PubMusicAdapter(@NonNull Context context, int padding, @NonNull PubMusicScrollListener listener) {
        mContext = context;
        mPadding = padding;
        mPubMusicScrollListener = listener;
        mInflater = LayoutInflater.from(context);
        topSmoothScroller = new TopSmoothScroller(mContext);
        topSmoothScroller0 = new TopSmoothScroller0(mContext);
    }

    public void setTotalList(@NonNull ArrayList<PubMusicMeta> list) {
        mtotalList = list;
        notifyDataSetChanged();
    }

    public ArrayList<PubMusicMeta> getTotalList() {
        return mtotalList;
    }

    public void setRecommandList(@NonNull ArrayList<PubMusicMeta> list) {
        mRecList = list;
        if (mUserPubMusicMeta == null) {
            mtotalList.clear();
            mtotalList.addAll(mRecList);
            notifyItemRangeChanged(0, getItemCount(), "payload");
        } else {
            PubMusicMeta tmp = mtotalList.get(0);
            mtotalList.clear();
            mtotalList.add(tmp);
            mtotalList.addAll(mRecList);
            notifyItemRangeChanged(1, getItemCount() - 1, "payload");
        }
    }

    public ArrayList<PubMusicMeta> getRecommandList() {
        return mRecList;
    }

    public void updateUserMusicMeta(@NonNull PubMusicMeta userPubMusicMeta) {
        if (mUserPubMusicMeta == null) {
            mUserPubMusicMeta = userPubMusicMeta;
            mtotalList.add(0, mUserPubMusicMeta);
            if (mtotalList.size() == 1) {
                notifyItemRangeChanged(0, getItemCount(), "payload");
            } else {
                notifyItemInserted(0);
            }
        } else {
            mUserPubMusicMeta = userPubMusicMeta;
            mtotalList.set(0, mUserPubMusicMeta);
            notifyItemChanged(0, "payload");
        }
    }

    public PubMusicMeta getUserMusicMeta() {
        return mUserPubMusicMeta;
    }

    public void setRecyclerViewWidth(int width) {
        if (width == mRecyclerViewWidth) {
            return;
        }
        mRecyclerViewWidth = width;
        //Log.d("jiabin", "mRecyclerViewWidth:" + mRecyclerViewWidth);
        if (mItemWidth == 0) {
            mItemWidth = (int) ((mRecyclerViewWidth - 2 * mPadding) / (1 + 0.7f));// 1 : 0.7
        }
        mFakeWidth = mRecyclerViewWidth - mItemWidth - mPadding * 2;
        notifyDataSetChanged();
        int curPage = mPubMusicScrollListener.getCurrentPage();
        smoothScrollToPostion(mPubMusicScrollListener.getLayoutManager(), curPage);
    }

    public void updateActivatingPos(int pos) {
        if (pos < 0 || mtotalList == null || mtotalList.isEmpty()) {
            return;
        }
        int curPage = mPubMusicScrollListener.getCurrentPage();
        int count = mtotalList.size();
        for (int i = 0; i < count; i++) {
            if (i == pos) {
                mtotalList.get(i).isActivating = true;
            } else {
                mtotalList.get(i).isActivating = false;
            }
            if(i != curPage){
                mtotalList.get(i).isLoading = false;
                mtotalList.get(i).isError = false;
                mtotalList.get(i).isPlaying = false;
            }
        }
        notifyItemRangeChanged(0, mtotalList.size(), "payload");
    }

    public void showLoadingView(@NonNull String loadingText) {
        mLoadingText = loadingText;
        mErrorText = null;
        if (mRecList.isEmpty()) {
            notifyItemChanged(getItemCount() - 1, "payload");
        }
    }

    public void showErrorView(@NonNull String errorText) {
        mLoadingText = null;
        mErrorText = errorText;
        if (mRecList.isEmpty()) {
            notifyItemChanged(getItemCount() - 1, "payload");
        }
    }

    public void updateCurrentLoading() {
        int curPage = mPubMusicScrollListener.getCurrentPage();
        int count = mtotalList.size();
        for (int i = 0; i < count; i++) {
            if (i == curPage) {
                mtotalList.get(i).isLoading = true;
                mtotalList.get(i).isError = false;
                mtotalList.get(i).isPlaying = false;
                break;
            }
        }
        notifyItemChanged(curPage, "payload");
    }

    public void updateCurrentError() {
        int curPage = mPubMusicScrollListener.getCurrentPage();
        int count = mtotalList.size();
        for (int i = 0; i < count; i++) {
            if (i == curPage) {
                mtotalList.get(i).isLoading = false;
                mtotalList.get(i).isError = true;
                mtotalList.get(i).isPlaying = false;
                break;
            }
        }
        notifyItemChanged(curPage, "payload");
    }

    public void updateCurrentPlaying() {
        int curPage = mPubMusicScrollListener.getCurrentPage();
        int count = mtotalList.size();
        for (int i = 0; i < count; i++) {
            if (i == curPage) {
                mtotalList.get(i).isLoading = false;
                mtotalList.get(i).isError = false;
                mtotalList.get(i).isPlaying = true;
                break;
            }
        }
        notifyItemChanged(curPage, "payload");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MUSIC) {
            View view = mInflater.inflate(R.layout.item_pub_music, parent, false);
            return new MusicViewHolder(view);
        } else if (viewType == TYPE_FAKE) {
            //View view = new View(mContext);
            //view.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            View view = mInflater.inflate(R.layout.item_pub_music_fake, parent, false);
            return new FakeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == TYPE_MUSIC) {
            MusicViewHolder musicViewHolder = (MusicViewHolder) holder;
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) musicViewHolder.itemView.getLayoutParams();
            layoutParams.leftMargin = mPadding;
            layoutParams.width = mItemWidth;
            PubMusicMeta meta = mtotalList.get(position);
            if (meta.isActivating) {
                musicViewHolder.itemView.setBackgroundColor(0xff00ff00);
                musicViewHolder.txt.setSelected(true);
            } else {
                musicViewHolder.itemView.setBackgroundColor(0xffff0000);
                musicViewHolder.txt.setSelected(false);
            }

            String state = "";
            if (meta.isPlaying) {
                state = "playing";
            } else if (meta.isLoading) {
                state = "loading";
            } else if (meta.isError) {
                state = "error";
            }
            musicViewHolder.txt2.setText(state);

            musicViewHolder.txt.setText(meta.name);
        } else if (viewType == TYPE_FAKE) {
            FakeViewHolder fakeViewHolder = (FakeViewHolder) holder;
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) fakeViewHolder.itemView.getLayoutParams();

            if (position == 0) {
                layoutParams.leftMargin = 0;
                layoutParams.width = mRecyclerViewWidth;
            } else {
                layoutParams.leftMargin = mPadding;
                layoutParams.width = mFakeWidth;
            }
            if (position == 0 || position == 1) {
                fakeViewHolder.txt.setVisibility(View.VISIBLE);
                String showText = "";
                if (mLoadingText != null) {
                    showText = mLoadingText;
                } else if (mErrorText != null) {
                    showText = mErrorText;
                }
                fakeViewHolder.txt.setText(showText);
            } else {
                fakeViewHolder.txt.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mtotalList == null || mtotalList.isEmpty()) {
            return TYPE_FAKE;
        }
        if (position == mtotalList.size()) {
            return TYPE_FAKE;
        } else {
            return TYPE_MUSIC;
        }
    }

    @Override
    public int getItemCount() {
        if (mtotalList == null || mtotalList.isEmpty())
            return 1;
        return mtotalList.size() + 1;
    }

    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txt;
        private TextView txt2;

        public MusicViewHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.txt);
            txt2 = (TextView) itemView.findViewById(R.id.txt2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, pos, mtotalList.get(pos).isActivating, mtotalList.get(pos).isPlaying, mtotalList.get(pos).isLoading, mtotalList.get(pos).isError);
            }
        }
    }

    class FakeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txt;

        public FakeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt = itemView.findViewById(R.id.txt);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                if(mErrorText != null){
                    mItemClickListener.onErrorClick();
                }
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int pos, boolean isActivating, boolean isPlaying, boolean isLoading, boolean isError);
        void onErrorClick();

    }

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public class TopSmoothScroller extends LinearSmoothScroller {

        TopSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            //return super.calculateSpeedPerPixel(displayMetrics);
            return 50.0f / (float) displayMetrics.densityDpi;
        }
    }

    public class TopSmoothScroller0 extends LinearSmoothScroller {

        TopSmoothScroller0(Context context) {
            super(context);
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            //return super.calculateSpeedPerPixel(displayMetrics);
            return 25.0f / (float) displayMetrics.densityDpi;
        }
    }

    public void smoothScrollToPostion(RecyclerView.LayoutManager layoutManager, int pos) {
        if (pos == 0) {
            topSmoothScroller0.setTargetPosition(pos);
            layoutManager.startSmoothScroll(topSmoothScroller0);
        } else {
            topSmoothScroller.setTargetPosition(pos);
            layoutManager.startSmoothScroll(topSmoothScroller);
        }
    }
}
