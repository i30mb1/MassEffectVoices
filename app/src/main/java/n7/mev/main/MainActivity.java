package n7.mev.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import n7.mev.R;

import static n7.mev.modules.ModulesFragment.MODULE_NAME;

public class MainActivity extends AppCompatActivity {

    public static final int WRITE_STORAGE_CODE = 12;
    private MainViewModel mViewModel;
    private byte[] file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
//        hideSystemUI();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow();
        }

        mViewModel = ViewModelProviders.of(this, new MainViewModelFactory(getApplication(), getIntent().getStringExtra(MODULE_NAME))).get(MainViewModel.class);
        mViewModel.getStartActivityForResultSaveFile().observe(this, new Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                if (intent != null) {
                    startActivityForResult(intent, WRITE_STORAGE_CODE);
                }
            }
        });
        mViewModel.getFileSaveFile().observe(this, new Observer<byte[]>() {
            @Override
            public void onChanged(byte[] bytes) {
                if (bytes != null) {
                    file = bytes;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case WRITE_STORAGE_CODE:
                if (data != null) {
                    if (data.getData() != null) {
                        writeInCreatedFile(data.getData());
                    }
                }
            default:

        }
    }

    private void writeInCreatedFile(final Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "w");
            if (parcelFileDescriptor != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                fileOutputStream.write(file);
            }
            final Snackbar snack = Snackbar.make(findViewById(R.id.container), "SAVED", Snackbar.LENGTH_LONG);
            snack.setAction("OPEN", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("*/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                        snack.dismiss();
                    }
                }
            });
            snack.show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Snackbar snack = Snackbar.make(findViewById(R.id.container), e.getMessage(), Snackbar.LENGTH_LONG);
            snack.show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar snack = Snackbar.make(findViewById(R.id.container), e.getMessage(), Snackbar.LENGTH_LONG);
            snack.show();}
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
