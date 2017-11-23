package com.pugfish1992.sample.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.pugfish1992.sample.R;
import com.pugfish1992.sample.data.Dinosaur;
import com.pugfish1992.sample.data.Period;
import com.pugfish1992.sqliteflow.core.DinosaurBase;

public class SampleActivity extends AppCompatActivity implements DinosaurEditorDialog.OnSaveSuccessfulListener {

    private CardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mCardAdapter = new CardAdapter(Dinosaur.getAll(), new CardAdapter.CardHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DinosaurEditorDialog dialog = DinosaurEditorDialog.newInstance(mCardAdapter.getItemAt(position));
                dialog.show(SampleActivity.this.getSupportFragmentManager(), null);
            }
        });
        recyclerView.setAdapter(mCardAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Dinosaur dinosaur = mCardAdapter.getItemAt(position);
                mCardAdapter.removeItemAt(position);
                dinosaur.delete();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DinosaurEditorDialog dialog = DinosaurEditorDialog.newInstance(null);
                dialog.show(SampleActivity.this.getSupportFragmentManager(), null);
            }
        });
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

    /**
     * DinosaurEditorDialog.OnSaveSuccessfulListener interface implementation
     * ----------------------------------------------------------------------- */

    @Override
    public void onSaveSuccessful(@NonNull Dinosaur dinosaur, boolean isNew) {
        mCardAdapter.addOrUpdateItem(dinosaur);
    }
}
