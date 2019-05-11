package com.rmkrings.main.pius_app;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.rmkrings.data.adapter.PostingsAdapter;
import com.rmkrings.data.postings.Postings;
import com.rmkrings.helper.Cache;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.CalendarLoader;
import com.rmkrings.loader.PostingsLoader;
import com.rmkrings.pius_app_for_android.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;


public class TodayPostingsFragment extends Fragment implements HttpResponseCallback {

    // Outlets
    private PostingsAdapter mPostingsAdapter;

    // Local State
    private final String digestFileName = "postings.md5";
    private final String cacheFileName = "postings.json";
    private final Cache cache = new Cache();
    private Postings postings;
    private final ArrayList<BaseListItem> itemlist = new ArrayList<>();

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());


    public TodayPostingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mPostings = view.findViewById(R.id.postinglist);
        RecyclerView.LayoutManager mVerticalLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);

        mPostings.setLayoutManager(mVerticalLayoutManager);
        mPostings.addItemDecoration(new DividerItemDecoration(mPostings.getContext(), DividerItemDecoration.VERTICAL));
        mPostingsAdapter = new PostingsAdapter(itemlist);
        mPostings.setAdapter(mPostingsAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_postings, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void reload() {
        String digest;

        if (cache.fileExists(cacheFileName) && cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        } else {
            logger.info(String.format("Cache and/or digest file %s does not exist. Not sending digest.", cacheFileName));
            digest = null;
        }

        final PostingsLoader postingsLoader = new PostingsLoader();
        postingsLoader.load(this, digest);
    }

    public void show(String message) {
        itemlist.clear();
        itemlist.add(new MessageItem(message, Gravity.CENTER));
        mPostingsAdapter.notifyDataSetChanged();
    }

    public void show() {
        reload();
    }

    private void setPostings() {
        itemlist.clear();
        itemlist.addAll(Objects.requireNonNull(postings.getPostings()));
        mPostingsAdapter.notifyDataSetChanged();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        String data;
        JSONObject jsonData;

        if (responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Calendar. HTTP Status code %d.", responseData.getHttpStatusCode()));
            show(getResources().getString(R.string.error_failed_to_load_data));
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
            postings = new Postings(jsonData);

            if (responseData.getHttpStatusCode() != 304 && postings.getDigest() != null) {
                cache.store(digestFileName, postings.getDigest());
            }

            setPostings();
        }
        catch (Exception e) {
            e.printStackTrace();
            show(getResources().getString(R.string.error_failed_to_load_data));
        }
    }
}