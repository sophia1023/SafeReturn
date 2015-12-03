package com.jh.safereturn;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    CharSequence title;
    FragmentManager frManager;
    FragmentTransaction frTrans;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ListView navList;
    String[] navItems ={"Police", "f1", "home"};
    ArrayAdapter<String> adapterDrawerList;

    FindPolice policeFr;
    FragmentHome homeFr;
    FragmentOne f1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer);
        //setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title=toolbar.getTitle();

        policeFr = new FindPolice();
        homeFr = new FragmentHome();
        f1 = new FragmentOne();

        navList = (ListView)findViewById(R.id.nav_list);
        adapterDrawerList=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,navItems);
        navList.setAdapter(adapterDrawerList);
        navList.setOnItemClickListener(new DrawerItemClickListener());
        toolbar.setOnClickListener(new ToolbarClickListener());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            switch (position){
                case 0:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, policeFr).commit();

                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, f1).commit();
                    break;
                case 2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();

            }
            drawerLayout.closeDrawer(navList);
        }
    }

    private class ToolbarClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            toolbar.setTitle(title);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
