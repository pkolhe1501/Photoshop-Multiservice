package com.example.photoshopmultiservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, content;
    TextView totalView, messageView;
    EditText customerInput, amountInput, serviceNameInput, serviceRateInput, expenseNameInput, expenseAmountInput;
    Spinner paymentSpinner;
    double salesTotal = 0, expenseTotal = 0;
    final ArrayList<Service> services = new ArrayList<>();
    final ArrayList<String> transactions = new ArrayList<>();
    final int navy = Color.rgb(8, 63, 120);
    final int blue = Color.rgb(0, 102, 220);
    final int green = Color.rgb(16, 145, 76);

    static class Service {
        String name; double rate;
        Service(String n, double r) { name=n; rate=r; }
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        services.add(new Service("A4 Xerox", 3));
        services.add(new Service("Color Print", 10));
        services.add(new Service("Photo Print", 15));
        services.add(new Service("Lamination", 20));
        services.add(new Service("Passport Photo", 40));
        showHome();
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setPadding(8,8,8,8); return t;
    }
    Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false);
        b.setOnClickListener(v -> action.run()); return b;
    }
    EditText edit(String hint, int inputType) {
        EditText e = new EditText(this); e.setHint(hint); e.setSingleLine(true); e.setInputType(inputType);
        return e;
    }
    void begin(String title) {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(246,249,253));
        TextView header = text(title, 20, Color.WHITE); header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(navy); header.setPadding(18,18,18,18); root.addView(header);
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(14,12,14,18);
        scroll.addView(content); root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
    void addSection(String title) {
        TextView t = text(title,18,navy); t.setPadding(4,14,4,6); content.addView(t);
    }
    void showHome() {
        begin("Photoshop & Multiservice");
        content.addView(text(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()) + "  •  Dashboard", 14, Color.DKGRAY));
        content.addView(text("आजची विक्री: ₹" + money(salesTotal), 22, green));
        content.addView(text("एकूण खर्च: ₹" + money(expenseTotal), 18, Color.RED));
        content.addView(text("शिल्लक (या डेमोतील): ₹" + money(salesTotal-expenseTotal), 18, navy));
        content.addView(button("नवीन बिल / सेवा बिलिंग", this::showServiceBilling));
        content.addView(button("जलद बिल — फक्त एकूण रक्कम", this::showQuickBill));
        content.addView(button("नवीन सेवा जोडा", this::showAddService));
        content.addView(button("दुकानाचा खर्च नोंदवा", this::showExpense));
        content.addView(button("दैनिक / मासिक रिपोर्ट", this::showReport));
        content.addView(button("Google Sheets / Cloud Sync सेटअप माहिती", this::showSyncInfo));
        content.addView(text("टीप: ही प्रारंभिक स्थानिक डेमो आवृत्ती आहे. Cloud sync आणि Sheets backup जोडलेले नाहीत.", 13, Color.DKGRAY));
    }
    void showServiceBilling() {
        begin("सेवा बिलिंग");
        customerInput = edit("ग्राहकाचे नाव (ऐच्छिक)", 1); content.addView(customerInput);
        addSection("सेवा निवडा — टॅप केल्यावर बिलात जोडा");
        LinearLayout cart = new LinearLayout(this); cart.setOrientation(LinearLayout.VERTICAL); content.addView(cart);
        ArrayList<Service> cartItems = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        TextView total = text("एकूण: ₹0", 22, green); content.addView(total);
        final Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            cart.removeAllViews(); double sum=0;
            for (int i=0;i<cartItems.size();i++) {
                final int idx=i; Service s=cartItems.get(i); int q=quantities.get(i); sum+=s.rate*q;
                LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
                TextView label=text(s.name+"  ₹"+money(s.rate)+" × "+q+" = ₹"+money(s.rate*q),15,Color.BLACK);
                row.addView(label,new LinearLayout.LayoutParams(0,-2,1));
                row.addView(button("−",()->{ quantities.set(idx,Math.max(1,quantities.get(idx)-1)); refresh[0].run(); }));
                row.addView(button("+",()->{ quantities.set(idx,quantities.get(idx)+1); refresh[0].run(); }));
                row.addView(button("×",()->{cartItems.remove(idx);quantities.remove(idx);refresh[0].run();}));
