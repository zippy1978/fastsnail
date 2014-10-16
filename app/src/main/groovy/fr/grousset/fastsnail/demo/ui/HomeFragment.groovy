package fr.grousset.fastsnail.demo.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView;
import fr.grousset.fastsnail.demo.R
import fr.grousset.fastsnail.transform.InjectLayout
import fr.grousset.fastsnail.transform.InjectView
import fr.grousset.fastsnail.widget.FSEfficientAdapter
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


@InjectLayout(R.layout.fragment_home)
@CompileStatic
public class HomeFragment extends Fragment {


    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    }

}
