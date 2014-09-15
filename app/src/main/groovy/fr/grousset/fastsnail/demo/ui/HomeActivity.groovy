package fr.grousset.fastsnail.demo.ui;

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem
import android.widget.TextView
import fr.grousset.fastsnail.demo.R
import groovy.transform.CompileStatic
import fr.grousset.fastsnail.async.AsyncManager
import fr.grousset.fastsnail.transform.InjectComponent
import fr.grousset.fastsnail.transform.InjectView
import fr.grousset.fastsnail.transform.InjectLayout


@CompileStatic
@InjectLayout(R.layout.activity_home)
class HomeActivity extends AbstractActivity implements HomeFragment.OnFragmentInteractionListener {

    @InjectComponent AsyncManager mAsyncManager
    @InjectView(R.id.helloTextView) TextView mHelloTextView

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState)

        mHelloTextView.setText('Hello from Groovy !!!')

        mAsyncManager.runAsync({
            sleep(4000)
            return 'Gilles'

        }).subscribe({String result ->
            mHelloTextView.setText('My name is Gilles :)')
            println('AAAAAA' + result)
        }, {Throwable e ->
            println 'OOOOPs ' + e
        }, {
            println 'completed ' + Thread.currentThread()
        })

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new HomeFragment())
        fragmentTransaction.commit()

    }

    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
