package it.liqid.instamaterial;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by niccolo on 27/01/15.
 */
public class BaseActivity extends ActionBarActivity implements GlobalMenuView.OnHeaderClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.ivLogo)
    ImageView ivLogo;

    private MenuItem inboxMenuItem;
    private DrawerLayout drawerLayout;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
        setupToolbar();
        setupDrawer();
    }

    protected void setupToolbar() {

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MenuItem getInboxMenuItem() {
        return inboxMenuItem;
    }

    public ImageView getIvLogo() {
        return ivLogo;
    }

    private void setupDrawer() {
        GlobalMenuView menuView = new GlobalMenuView(this);
        menuView.setOnHeaderClickListener(this);

        drawerLayout = DrawerLayoutInstaller.from(this)
                .drawerRoot(R.layout.drawer_root)
                .drawerLeftView(menuView)
                .drawerLeftWidth(Utils.dpToPx(300))
                .withNavigationIconToggler(getToolbar())
                .build();
    }

    @Override
    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }
}
