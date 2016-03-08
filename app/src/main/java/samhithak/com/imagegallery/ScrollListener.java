package samhithak.com.imagegallery;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * TODO: Write Javadoc for OnScrollListener.
 *
 * @author skamaraju
 */
public class ScrollListener extends RecyclerView.OnScrollListener {
    private boolean mLoading;
    public static final int DEFAULT_PAGE_SIZE = 30;

    private LoadMoreListener mLoadMoreListener;

    public ScrollListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int mLastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
        // when the scroll is performed, make service call after a page in
        // the current retrieved items set
        if (totalItemCount < mLastVisibleItem + (DEFAULT_PAGE_SIZE)) {
            if (!mLoading || totalItemCount == mLastVisibleItem + 1) {
                enableLoading();
            } else {
                disableLoading();
            }
        } else {
            disableLoading();
        }
    }

    private void disableLoading() {
        mLoading = false;
    }

    private void enableLoading() {
        mLoading = true;
        mLoadMoreListener.getNextPageOnScrolled();
    }

    public interface LoadMoreListener {
        void getNextPageOnScrolled();
    }
}
