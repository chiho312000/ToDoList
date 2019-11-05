package hk.polyu.eie.eie3109.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class PhoneActivity extends AppCompatActivity
{
    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACT_NAME_INDEX = 1;
    private static final int CONTACT_HAS_PHONE_INDEX = 1;
    private static final int CONTACT_KEY_INDEX = 2;

    private final String[] PROJECTION =
    {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
    };
    private ContentResolver cr;
    private ListView myPhoneList;
    private SimpleCursorAdapter myCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        cr = getContentResolver();
        Button btnBack = findViewById(R.id.btnBack);
        myPhoneList = findViewById(R.id.LVPhoneList);
        showContacts();
        if (btnBack != null)
        {
            btnBack.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    finish();
                }
            });
        }

        myPhoneList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                boolean hasPhoneNumber = !((Cursor) myCursorAdaptor.getItem(i)).getString(CONTACT_HAS_PHONE_INDEX).equals("0");
                String message = "";
                if (hasPhoneNumber)
                {
                    String id = ((Cursor) myCursorAdaptor.getItem(i)).getString(CONTACT_ID_INDEX);
                    Log.v("Phone Activity: ", "id selected :" + id);
                    Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    while (phoneCursor.moveToNext())
                    {
                        if (phoneCursor.getString(1) != null) message += phoneCursor.getString(1) + '\n';
                    }
                }
                else
                {
                    message = "No Phone Number Available";
                }

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showContacts()
    {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[])overriden method
        }
        else
        {
            Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, null);
            myCursorAdaptor = new SimpleCursorAdapter(this, R.layout.list_item, c, new String[] {ContactsContract.Contacts.DISPLAY_NAME }, new int[] {R.id.TVRow}, 0);
            myPhoneList.setAdapter(myCursorAdaptor);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 100)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Permission is granted
                showContacts();
            }
            else
            {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
