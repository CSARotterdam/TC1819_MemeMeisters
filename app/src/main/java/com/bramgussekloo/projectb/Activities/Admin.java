package com.bramgussekloo.projectb.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bramgussekloo.projectb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Admin extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FloatingActionButton addProductBttn;
    private FirebaseUser user = mAuth.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setTitle("Admin"); // sets title for toolbar

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

        getMenuInflater().inflate(R.menu.main_menu, menu); // inflates menu from XML to objects

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) { // switch case for detecting which menu item is clicked

            case R.id.action_logout_bttn:  // triggers when logout button is clicked

                logOut();  // logs out user

                return true;
            case R.id.action_resetPassword_buttn:

                resetpassword();
                return true;
            case R.id.action_ChangeEmail_bttn:
                Intent changeEmail = new Intent(getBaseContext(), ResetEmail.class);
                startActivity(changeEmail);
                return true;
            default:

                return false;


        }

    }

    private void logOut() {
        mAuth.signOut();
        sendTologin();
    }
    private void resetpassword(){
        if (user.getEmail() != null)
        mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Admin.this, "Mail was sent succesful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Admin.this, "Mail not sent succesful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendTologin() {

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish(); // ensures user can't go back
    }
}
