package com.bramgussekloo.projectb.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bramgussekloo.projectb.R;
import com.google.firebase.auth.FirebaseAuth;

public class Admin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FloatingActionButton addProductBttn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Admin");

        addProductBttn = findViewById(R.id.addProductBttn);
        addProductBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newProductIntent = new Intent(getApplicationContext(), NewProductActivity.class);
                startActivity(newProductIntent);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_bttn:

                logOut();

                return true;

            default:

                return false;


        }

    }

    private void logOut() {
        mAuth.signOut();
        sendTologin();
    }

    private void sendTologin() {

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}