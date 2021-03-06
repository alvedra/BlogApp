package com.educate.blogapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.educate.blogapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Dialog popAddPostDialog;
    ImageView popupUserPhoto, popupPostImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPostDialog.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Get Firebase User
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Set logout action
        MenuItem logoutMenu = navigationView.getMenu().findItem(R.id.nav_logout);
        logoutMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                firebaseAuth.signOut();
                Intent LoginActivity = new Intent(getApplicationContext(), com.educate.blogapp.Activities.LoginActivity.class);
                startActivity(LoginActivity);
                finish();
                return true;
            }
        });

        // Update Header
        updateNavHeader();


        // Set popup
        initPopup();

    }

    private void initPopup() {
        popAddPostDialog = new Dialog(this);
        popAddPostDialog.setContentView(R.layout.popup_add_post);
        popAddPostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPostDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPostDialog.getWindow().getAttributes().gravity = Gravity.TOP;

        // popup widgets
        popupUserPhoto = popAddPostDialog.findViewById(R.id.popup_user_photo);
        popupPostImage = popAddPostDialog.findViewById(R.id.popup_img);
        popupTitle = popAddPostDialog.findViewById(R.id.popup_title);
        popupDescription = popAddPostDialog.findViewById(R.id.popup_description);
        popupAddBtn = popAddPostDialog.findViewById(R.id.popup_add);
        popupClickProgress = popAddPostDialog.findViewById(R.id.popup_progressBar);

        // load user photo
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(popupUserPhoto);

        // add post click listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupClickProgress.setVisibility(View.VISIBLE);
                popupAddBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            Intent LoginActivity = new Intent(getApplicationContext(), com.educate.blogapp.Activities.LoginActivity.class);
            startActivity(LoginActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        ImageView navImgPhoto = headerView.findViewById(R.id.nav_img_photo);

        if (firebaseUser != null) {
            navUsername.setText(firebaseUser.getDisplayName());
            navEmail.setText(firebaseUser.getEmail());

            if (firebaseUser.getPhotoUrl() != null)
                Glide.with(this).load(firebaseUser.getPhotoUrl()).into(navImgPhoto);
        }
    }
}
