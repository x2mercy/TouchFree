package com.example.chenrui.easycook;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class NavigateActivity extends AppCompatActivity {

    //TODO fragments initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        // bottom navigation initialization
        // -search -shopping list -my recipe
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

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
}
