package samhithak.com.imagegallery;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements ScrollListener.LoadMoreListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private String mQuery = "";
    private int mPageNumber = 0;
    private List<Image> mImageList;
    private GalleryFragmentListener mFragmentListener;


    public interface RequestListener {
        void onSuccess(List<Image> images, boolean isRefresh);

        void onFailure();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View top = inflater.inflate(R.layout.fragment_gallery, container, false);
        mImageList = new ArrayList<>();

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) top.findViewById(R.id.swipe_refresh);
        swipeContainer.setColorSchemeResources(R.color.orange, R.color.yellow, R.color.purple);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onListRefreshed(true);
            }
        });

        final FloatingActionButton options = (FloatingActionButton) top.findViewById(R.id.btn_options);
        final FloatingActionButton refresh = (FloatingActionButton) top.findViewById(R.id.btn_refresh);
        final FloatingActionButton clear = (FloatingActionButton) top.findViewById(R.id.btn_clear);
        refresh.setOnClickListener(this);
        clear.setOnClickListener(this);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = View.GONE;
                int optionsResource = R.drawable.icon_options;

                if(refresh.getVisibility() == View.GONE) {
                    visibility = View.VISIBLE;
                    optionsResource = R.drawable.icon_share_close;
                }

                refresh.setVisibility(visibility);
                clear.setVisibility(visibility);
                options.setImageDrawable(getResources().getDrawable(optionsResource));
            }
        });

        //Set options menu
        setHasOptionsMenu(true);
        return top;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        if (view != null) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.addOnScrollListener(new ScrollListener(this));
            GalleryAdapter adapter = new GalleryAdapter(getContext());
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            mQuery = savedInstanceState.getString("Query");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mFragmentListener = (GalleryFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!menu.hasVisibleItems()) {
            inflater.inflate(R.menu.menu_gallery, menu);
        }

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    onListCleared();
                    searchView.clearFocus();
                    return true;
                }

                onSearchPerformed(newText, 1, false);
                return true;
            }
        };
        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideProgressBar();
                searchView.clearFocus();
                return false;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setOnCloseListener(closeListener);
    }

    @Override
    public void onStart(){
        super.onStart();

        if(mQuery != null && !TextUtils.isEmpty(mQuery)) {
            onSearchPerformed(mQuery, 1, false);
        }
    }

    @Override
    public void getNextPageOnScrolled() {
        View view = getView();

        if (view != null) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
            GalleryAdapter adapter = (GalleryAdapter) recyclerView.getAdapter();
            int size = adapter.getItemCount();
            if (mPageNumber == size / ScrollListener.DEFAULT_PAGE_SIZE) {
                mPageNumber++;
                onSearchPerformed(mQuery, mPageNumber, false);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View topView = getView();
        if (topView != null) {
            RecyclerView recyclerView = (RecyclerView) topView.findViewById(R.id.gallery_recycler_view);
            GalleryAdapter adapter = (GalleryAdapter) recyclerView.getAdapter();
            Image image = adapter.getImage(position);
            mFragmentListener.onImageClicked(image);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:
                onListRefreshed(false);
                break;
            case R.id.btn_clear:
                onListCleared();
                break;
        }
    }

    private void onSearchPerformed(String query, int pageNumber, boolean isPullToRefresh) {
        RequestListener listener = new RequestListener() {
            @Override
            public void onSuccess(List<Image> images, boolean isRefresh) {
                onDataReceived(images, isRefresh);
            }

            @Override
            public void onFailure() {

            }
        };

        if(pageNumber == 1 && !isPullToRefresh) {
            showProgressBar();
        }
        ImageQuery imageQuery = new ImageQuery();
        try {
            mQuery = query;
            imageQuery.computeUrl(query, pageNumber);
            imageQuery.run(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDataReceived(final List<Image> imageList, boolean isRefresh) {
        View view = getView();
        if (view != null) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
            final GalleryAdapter adapter = (GalleryAdapter) recyclerView.getAdapter();
            if (isRefresh) {
                mImageList = imageList;
            } else {
                mImageList.addAll(imageList);
            }

            mPageNumber = mImageList.size() / ScrollListener.DEFAULT_PAGE_SIZE;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideRefreshBar();
                    hideProgressBar();
                    adapter.swapData(mImageList);
                }
            });
        }
    }

    private void onListCleared(){
        mImageList = new ArrayList<>();
        View view = getView();
        if(view != null) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
            GalleryAdapter adapter = (GalleryAdapter) recyclerView.getAdapter();
            adapter.swapData(mImageList);
        }
    }

    private void onListRefreshed(boolean isPullToRefresh){
        onSearchPerformed(mQuery, 1, isPullToRefresh);
    }

    private void showProgressBar(){
        View view = getView();
        if(view != null) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_indicator);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(){
        View view = getView();
        if(view != null) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_indicator);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideRefreshBar(){
        View view = getView();
        if(view != null) {
            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public interface GalleryFragmentListener {
        void onImageClicked(Image image);
    }
}
