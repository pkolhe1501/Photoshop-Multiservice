package com.example.photoshopmultiservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root, content;
    private EditText customerInput;
    private Spinner paymentSpinner;
    private double salesTotal = 0.0;
    private double expenseTotal = 0.0;
    private final ArrayList<Service> services = new ArrayList<>();
    private final ArrayList<String> transactions = new ArrayList<>();
    private final ArrayList<String> expenses = new ArrayList<>();

    private final int navy = Color.rgb(8, 63, 120);
    private final int blue = Color.rgb(0, 102, 220);
    private final int green = Color.rgb(16, 145, 76);

    private static class Service {
        String name;
        double rate;
        Service(String name, double rate) {
            this.name = name;
            this.rate = rate;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        services.add(new Service("A4 Xerox", 3));
        services.add(new Service("Color Print", 10));
        services.add(new Service("Photo Print", 15));
        services.add(new Service("Lamination", 20));
        services.add(new Service("Passport Photo", 40));
        showHome();
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    private Button button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private EditText edit(String hint, int inputType) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        e.setInputType(inputType);
        return e;
    }

    private void begin(String title) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(246, 249, 253));

        TextView header = text(title, 20, Color.WHITE);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(navy);
        header.setPadding(18, 18, 18, 18);
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(14, 12, 14, 18);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        root.addView(button("⌂ मुख्य पान", this::showHome));
        setContentView(root);
    }
