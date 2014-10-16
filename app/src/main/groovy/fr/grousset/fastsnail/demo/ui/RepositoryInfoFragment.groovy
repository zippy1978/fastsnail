package fr.grousset.fastsnail.demo.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup
import android.widget.TextView;
import fr.grousset.fastsnail.demo.R
import fr.grousset.fastsnail.demo.manager.RequestManager
import fr.grousset.fastsnail.demo.model.github.Repo
import fr.grousset.fastsnail.transform.InjectComponent;
import fr.grousset.fastsnail.transform.InjectLayout
import fr.grousset.fastsnail.transform.InjectView;
import groovy.transform.CompileStatic;

/**
 * Created by gillesgrousset on 16/10/2014.
 */
@InjectLayout(R.layout.fragment_repository_info)
@CompileStatic
public class RepositoryInfoFragment extends Fragment {

    @InjectView(R.id.title_text_view) TextView titleTextView

    @InjectComponent RequestManager requestManager

    public RepositoryInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume()

        requestManager.getGitHubRepo('zippy1978', 'fastsnail').subscribe({Repo repo ->
            println "Repo = $repo.name"

            titleTextView.setText(repo.name)
        }, {Throwable e ->
            println ">>>>> $e.message"
        }, {
            println "done"
        })
    }

}
