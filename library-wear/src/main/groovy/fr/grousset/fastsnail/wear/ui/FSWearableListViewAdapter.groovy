package fr.grousset.fastsnail.wear.ui

import android.content.Context
import android.support.wearable.view.WearableListView
import android.view.LayoutInflater
import android.view.ViewGroup
import groovy.transform.CompileStatic

@CompileStatic
/**
 * WearableListView adapter with simple setup.
 * Uses the onBindViewHolder(viewHolder, item) closure to bind views.
 * @author gi.grousset@gmail.com
 */
public class FSWearableListViewAdapter extends WearableListView.Adapter {

    @Delegate List mItems = []

    Closure onBindViewHolder

    private int mItemResId
    private final LayoutInflater mInflater

    public FSWearableListViewAdapter(Context context, int itemResId) {
        mInflater = LayoutInflater.from(context)
        mItemResId = itemResId
    }

    @Override
    WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        return new WearableListView.ViewHolder(mInflater.inflate(mItemResId, null))
    }

    @Override
    void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {

        Object item = mItems.get(i)

        if (this.onBindViewHolder) {
            this.onBindViewHolder.call(viewHolder, item)
        }
    }

    @Override
    int getItemCount() {
        return mItems.size()
    }
}