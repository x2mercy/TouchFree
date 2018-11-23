package com.example.chenrui.easycook;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class NavigateActivity extends AppCompatActivity implements UserProfile.UserProfileListener,Tab2Recipes.OnFragmentInteractionListener{

    //TODO fragments initialization
    public Recipes recipes;

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        // bottom navigation initialization
        // -search -shopping list -my recipe
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        // Added by Justin for My Recipes tab
        recipes = new Recipes();
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frag_navigation,recipes);

        // Set Item click listener to the menu items
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override

                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        switch (item.getItemId()) {
                            case R.id.search:
                                //TODO


                                Toast.makeText(NavigateActivity.this,"Search Fragment",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.shoppingList:
                                //TODO
                                Toast.makeText(NavigateActivity.this,"shoppinglist Fragment",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.favorite:
                                //TODO
                                Toast.makeText(NavigateActivity.this,"favorite Fragment",Toast.LENGTH_SHORT).show();
                                break;

                        }
                        return false;
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void pickRecipeList(int i) {
        recipes.pickRecipeList(i);
    }
}
