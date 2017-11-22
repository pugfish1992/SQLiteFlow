package com.pugfish1992.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.pugfish1992.sqliteflow.component.Expressions;
import com.pugfish1992.sqliteflow.component.Join;
import com.pugfish1992.sqliteflow.core.FamilyTable;
import com.pugfish1992.sqliteflow.core.Select;
import com.pugfish1992.sqliteflow.core.User;
import com.pugfish1992.sqliteflow.core.UserTable;

import java.util.List;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        User user = new User();
        user.name = "oraoraora";
        user.age = 32;
        user.hasBrothers = false;
        if (!user.save()) {
            Log.d("mylog", "failed to save...");
        }

        user = new User();
        user.name = "dorararara";
        user.age = 18;
        user.hasBrothers = false;
        if (!user.save()) {
            Log.d("mylog", "failed to save...");
        }

        user = new User();
        user.name = "wryyyyyyyyyyyy";
        user.age = 120;
        user.hasBrothers = true;
        if (!user.save()) {
            Log.d("mylog", "failed to save...");
        }

        List<User> users = Select
                .target(User.class)
                .from(UserTable.class)
                .where(UserTable.hasBrothers.equalsTo(true))
                .start();

        for (User u : users) {
            Log.d("mylog", "selected -> " + u.name + " hasBrothers=" + String.valueOf(u.hasBrothers));
        }

        Join join = new Join("left", Join.Type.INNER_JOIN, "right").on(UserTable.id.equalsTo(FamilyTable.id));
        Log.d("mylog", "join -> " + join.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
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
}
