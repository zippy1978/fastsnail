package fr.grousset.fastsnail.demo.ui

import android.app.ActionBar
import android.app.Activity
import android.app.Fragment
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import fr.grousset.fastsnail.demo.R
import fr.grousset.fastsnail.transform.InjectLayout
import fr.grousset.fastsnail.transform.InjectView
import groovy.transform.CompileStatic

@CompileStatic
@InjectLayout(R.layout.activity_main)
public class MainActivity extends Activity implements MenuFragment.Listener {

    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout
    @InjectView(R.id.left_drawer_frame) FrameLayout leftDrawerFrame
    @InjectView(R.id.content_frame) FrameLayout contentFrame

    private ActionBarDrawerToggle drawerToggle
    private Fragment contentFragment

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState)

        // Action bar setup
        actionBar.displayHomeAsUpEnabled = true
        actionBar.homeButtonEnabled = true

        // Drawer setup
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_navigation_drawer, android.R.string.ok, android.R.string.cancel)
        drawerLayout.drawerListener = drawerToggle

        // Add menu fragment in left drawer
        MenuFragment menuFragment = new MenuFragment()
        menuFragment.listener = this
        fragmentManager.beginTransaction().replace(R.id.left_drawer_frame, menuFragment).commit()

        // Set initial content fragment
        this.setContentFragment(new HomeFragment())

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle home button click to open / close drawer
        if (item != null && item.getItemId() == android.R.id.home && drawerToggle.drawerIndicatorEnabled) {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return false
    }

    public void setContentFragment(Fragment fragment) {

        if (!contentFragment || (contentFragment && fragment.class.name != contentFragment.class.name)) {
            this.contentFragment = fragment
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit()

        }

        // Close drawer
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    @Override
    void onMenuItemSelected(fr.grousset.fastsnail.demo.ui.MenuFragment.MenuItem item) {

        switch (item.id) {
            case 'HOME':
                this.setContentFragment(new HomeFragment())
                break
            case 'REPO':
                this.setContentFragment(new RepositoryInfoFragment())
                break
        }
    }
}