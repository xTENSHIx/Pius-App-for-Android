package com.rmkrings.main.pius_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.adapter.NewsListAdapter;
import com.rmkrings.data.news.NewsItem;
import com.rmkrings.data.news.NewsItems;
import com.rmkrings.data.news.NewsListItem;
import com.rmkrings.helper.Cache;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.ParentFragment;
import com.rmkrings.interfaces.ViewSelectedCallback;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.NewsLoader;
import com.rmkrings.pius_app_for_android.R;
import com.rmkrings.pius_app_for_android.WebViewActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;

public class TodayNewsFragment extends Fragment implements HttpResponseCallback, ViewSelectedCallback {

    // Outlets
    private NewsListAdapter mNewsListAdapter;

    // Local State
    private final String digestFileName = "news.md5";
    private final String cacheFileName = "news.json";

    private final Cache cache = new Cache();
    private NewsItems newsItems;
    private final ArrayList<BaseListItem> newsItemList = new ArrayList<>();
    private ParentFragment parentFragment;

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    public TodayNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mNewsList = view.findViewById(R.id.newslist);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);

        mNewsList.setLayoutManager(mVerticalLayoutManager);
        mNewsList.setClickable(false);
        mNewsList.addItemDecoration(new DividerItemDecoration(mNewsList.getContext(), DividerItemDecoration.VERTICAL));
        mNewsListAdapter = new NewsListAdapter(newsItemList, this);
        mNewsList.setAdapter(mNewsListAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_news, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void show(ParentFragment parentFragment) {
        this.parentFragment = parentFragment;
        reload();
    }

    private void setNewsList() {
        newsItemList.clear();
        for (NewsItem newsItem: newsItems.getNewsItems()) {
            newsItemList.add(new NewsListItem(newsItem));
        }

        mNewsListAdapter.notifyDataSetChanged();
    }

    private void setMessage(String message) {
        newsItemList.clear();
        newsItemList.add(new MessageItem(message, Gravity.CENTER));
        mNewsListAdapter.notifyDataSetChanged();
    }

    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        NewsLoader newsLoader = new NewsLoader();
        newsLoader.load(this, digest);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for news. HTTP Status code %d.", responseData.getHttpStatusCode()));
            setMessage(getResources().getString(R.string.error_failed_to_load_data));
            return;
        }

        if (responseData.getData() != null) {
            data = responseData.getData();
            cache.store(cacheFileName, data);
        } else {
            data = cache.read(cacheFileName);
        }

        try {
            jsonData = new JSONObject(data);
            newsItems = new NewsItems(jsonData);

            if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 304 && newsItems.getDigest() != null) {
                cache.store(digestFileName, newsItems.getDigest());
            }

            setNewsList();
        }
        catch (Exception e) {
            setMessage(getResources().getString(R.string.error_failed_to_load_data));
            e.printStackTrace();
        }

        parentFragment.notifyDoneRefreshing();
    }

    @Override
    public void notifySelectionChanged(View b, String href) {
        Intent a = new Intent(this.getActivity(), WebViewActivity.class);
        a.putExtra("URL", href);
        startActivity(a);
    }
}