package fr.grousset.fastsnail.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import groovy.transform.CompileStatic

@CompileStatic
public class FSEfficientAdapter extends BaseAdapter {

    @Delegate List items = []

    Closure onCreateViewHolder
    Closure onBindViewHolder

    private int itemResId
    private final LayoutInflater inflater

    public FSEfficientAdapter(Context context, int itemResId) {
        inflater = LayoutInflater.from(context)
        this.itemResId = itemResId
    }

    public int getCount() {
        return items.size()
    }

    public Object getItem(int position) {
        return position
    }

    public long getItemId(int position) {
        return position
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder

        if (convertView == null) {

            convertView = inflater.inflate(itemResId, null)
            if (this.onCreateViewHolder) {
                holder = this.onCreateViewHolder.call(convertView, position) as ViewHolder
            } else {
                holder = new ViewHolder()
                holder.itemView = convertView
            }

            convertView.tag = holder

        } else {
            holder = (ViewHolder) convertView.tag
        }

        if (this.onBindViewHolder) {
            this.onBindViewHolder.call(holder, items.get(position), position)
        }

        return convertView
    }

    class ViewHolder {
        View itemView
    }
}
