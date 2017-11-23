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
import com.pugfish1992.sqliteflow.utils.ValidationErrorListener;

import java.util.List;
import java.util.Set;

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
        user.age = 3;
        user.hasBrothers = false;
        user.save(new ValidationErrorListener() {
            @Override
            public void onValidationError(int validatorTag, Set<Integer> errors) {
                Log.d("mylog", "errors from -> " + String.valueOf(validatorTag));
                for (int error : errors) {
                    if (error == UserTableContract.UserValidator.NAME_IS_EMPTY) {
                        Log.d("mylog", "error:name_is_empty");
                    } else if (error == UserTableContract.UserValidator.AGE_IS_LESS_THAN_10) {
                        Log.d("mylog", "error:age_is_less_than_10");
                    }
                }
            }
        });

        user = new User();
        user.name = null;
        user.age = 18;
        user.hasBrothers = false;
        user.save(new ValidationErrorListener() {
            @Override
            public void onValidationError(int validatorTag, Set<Integer> errors) {
                Log.d("mylog", "errors from -> " + String.valueOf(validatorTag));
                for (int error : errors) {
                    if (error == UserTableContract.UserValidator.NAME_IS_EMPTY) {
                        Log.d("mylog", "error:name_is_empty");
                    } else if (error == UserTableContract.UserValidator.AGE_IS_LESS_THAN_10) {
                        Log.d("mylog", "error:age_is_less_than_10");
                    }
                }
            }
        });

        user = new User();
        user.name = "wryyyyyyyyyyyy";
        user.age = 120;
        user.hasBrothers = true;
        user.save(new ValidationErrorListener() {
            @Override
            public void onValidationError(int validatorTag, Set<Integer> errors) {
                Log.d("mylog", "errors from -> " + String.valueOf(validatorTag));
                for (int error : errors) {
                    if (error == UserTableContract.UserValidator.NAME_IS_EMPTY) {
                        Log.d("mylog", "error:name_is_empty");
                    } else if (error == UserTableContract.UserValidator.AGE_IS_LESS_THAN_10) {
                        Log.d("mylog", "error:age_is_less_than_10");
                    }
                }
            }
        });

        List<User> users = Select
                .target(User.class)
                .from(UserTable.class)
                .where(UserTable.hasBrothers.equalsTo(true))
                .start();

        for (User u : users) {
            Log.d("mylog", "selected -> " + u.name + " hasBrothers=" + String.valueOf(u.hasBrothers));
        }

        Join join = new UserTable().innerJoin(FamilyTable.class).on(UserTable.id.equalsTo(FamilyTable.id));
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
