package prestozen.presto.watch.prestozen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_phone);

        Button goButton = (Button)findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText endAddTxt = (EditText)findViewById(R.id.end_address_text);
                String endAddress = endAddTxt.getText().toString();
                if(endAddress == null || endAddress.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainPhoneActivity.this).create();
                    alertDialog.setTitle("Missing End Address");
                    alertDialog.setMessage("An End address is needed to begin routing");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }

                //////OK, we have a valid address, let's do some routing....

            }
        });
    }
}
