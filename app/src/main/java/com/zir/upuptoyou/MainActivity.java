package com.zir.upuptoyou;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected DrawView drawView;
    protected EditText editText;
    protected Button reload_btn;
    protected Button clear_btn;
    protected Button color1_btn;
    protected Button color2_btn;
    protected Button color3_btn;
    protected Button color4_btn;
    protected Button color5_btn;
    protected Button color6_btn;
    protected Button color7_btn;
    protected Button color8_btn;
    protected Button save_btn;

    final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawView = (DrawView) findViewById(R.id.drawView);
        editText = (EditText) findViewById(R.id.editText);
        reload_btn = (Button) findViewById(R.id.reload_btn);
        clear_btn = (Button) findViewById(R.id.clear_btn);
        color1_btn = (Button) findViewById(R.id.color1_btn);
        color2_btn = (Button) findViewById(R.id.color2_btn);
        color3_btn = (Button) findViewById(R.id.color3_btn);
        color4_btn = (Button) findViewById(R.id.color4_btn);
        color5_btn = (Button) findViewById(R.id.color5_btn);
        color6_btn = (Button) findViewById(R.id.color6_btn);
        color7_btn = (Button) findViewById(R.id.color7_btn);
        color8_btn = (Button) findViewById(R.id.color8_btn);
        save_btn = (Button) findViewById(R.id.save_btn);

        setSupportActionBar(toolbar);

        //Todo: dynamically change button size


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Uri uriToImage = drawView.share();
                if(uriToImage != null)
                    share(uriToImage);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                drawView.drawPeople(editText.getText().toString());
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        reload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.refresh();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        color1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color1);
            }
        });
        color2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color2);
            }
        });
        color3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color3);
            }
        });
        color4_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color4);
            }
        });
        color5_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color5);
            }
        });
        color6_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color6);
            }
        });
        color7_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color7);
            }
        });
        color8_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBackgroundResource(R.color.color8);
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

    public void save() {
        //Permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
        } else
            drawView.save();
    }

    public  void share(Uri fileUri){
        String title = "share to";
        ShareCompat.IntentBuilder.from(this).setChooserTitle(title).setType("image/jpeg").setStream(fileUri).startChooser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    drawView.save();
                } else {
                    Toast toast = Toast.makeText(this, R.string.AskPermission_Storage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                return;
            }
        }
    }
}
