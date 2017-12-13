package com.caesar.phonelogs.activitys;

import android.Manifest;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.fragments.BlankFragment;
import com.caesar.phonelogs.fragments.ContactsFragment;
import com.caesar.phonelogs.fragments.LogsFragment;
import com.caesar.phonelogs.utils.ContactUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Context context;

    private TabLayout mTabLayout;
    private List<Fragment> tabFragments = null;
    private List<String> tabIndicators = null;
    private LogsFragment logsFragment;
    private ContactsFragment contactsFragment;
    private BlankFragment blankFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tl_tab);
        mViewPager = (ViewPager) findViewById(R.id.container);
        initTab();
        initContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        initData();
    }

    private void initTab(){
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 1").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 2").setIcon(R.mipmap.ic_launcher));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 3").setIcon(R.mipmap.ic_launcher));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initContent(){
        tabIndicators = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tabIndicators.add("Tab " + i);
        }

        tabFragments = new ArrayList<>();
        logsFragment = LogsFragment.newInstance("","");
        tabFragments.add(logsFragment);

        contactsFragment = ContactsFragment.newInstance("","");
        tabFragments.add(contactsFragment);

        blankFragment = BlankFragment.newInstance("","");
        tabFragments.add(blankFragment);
        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout.getTabAt(0).select();
    }

    public void initData(){
        //获取通话记录
        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(
                Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(context, "权限允許", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(context, "权限拒绝", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }
    }
}
