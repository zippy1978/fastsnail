package fr.grousset.fastsnail.demo.ui

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import fr.grousset.fastsnail.demo.R
import fr.grousset.fastsnail.transform.InjectLayout
import fr.grousset.fastsnail.transform.InjectView
import fr.grousset.fastsnail.widget.FSEfficientAdapter
import groovy.transform.CompileStatic

import java.lang.ref.WeakReference


@InjectLayout(R.layout.fragment_menu)
@CompileStatic
public class MenuFragment extends Fragment {

    private WeakReference<Listener> listener = new WeakReference<>(null)

    public void setListener(Listener listener) {
        this.listener = new WeakReference(listener)
    }

    public interface Listener {
        public void onMenuItemSelected(MenuItem menuItem)
    }

    public class MenuItem {
        String id
        String label

        public MenuItem(String id, String label) {
            this.id = id
            this.label = label
        }
    }

    @InjectView(R.id.list_view) ListView listView

    public MenuFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // List setup
        FSEfficientAdapter adapter = new FSEfficientAdapter(activity, R.layout.item_menu)
        adapter.addAll([new MenuItem('HOME', 'Home'), new MenuItem('REPO', 'Repository info')])
        adapter.onBindViewHolder = {FSEfficientAdapter.ViewHolder holder, MenuItem item, position ->
            TextView textView = holder.itemView as TextView
            textView.setText(item.label)
        }
        listView.adapter = adapter

        // Click listener
        listView.onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener.get()) {
                    listener.get().onMenuItemSelected(adapter.get(position) as MenuItem)
                }
            }
        }

    }
}