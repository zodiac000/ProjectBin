package com.example.kevin.realtimedatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button mSubmit;

    int counter = 0;

    double inertia_x = 10.0;
    double inertia_y = 20.0;
    double inertia_z = 30.0;

    double angle_x = 11.0;
    double angle_y = 22.0;
    double angle_z = 33.0;

    double light = 25.0;


    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usersRef = mDatabase.child("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get UI elements
        mSubmit = (Button)findViewById(R.id.btnSubmit);

    }

    @Override
    protected void onStart(){
        super.onStart();

        mSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference IDRef = usersRef.child(Integer.toString(counter)).push();

                DatabaseReference Inertia = IDRef.child("Inertia");
                DatabaseReference I_x = Inertia.child(("x"));
                DatabaseReference I_y = Inertia.child(("y"));
                DatabaseReference I_z = Inertia.child(("z"));

                DatabaseReference Angle = IDRef.child("Angle");
                DatabaseReference A_x = Angle.child(("x"));
                DatabaseReference A_y = Angle.child(("y"));
                DatabaseReference A_z = Angle.child(("z"));

                DatabaseReference Light = IDRef.child("Light");

                I_x.setValue(inertia_x);
                I_y.setValue(inertia_y);
                I_z.setValue(inertia_z);

                A_x.setValue(angle_x);
                A_y.setValue(angle_y);
                A_z.setValue(angle_z);

                Light.setValue(light);
                counter++;
            }

        });
    }
}
