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
        Runnable refresh = () -> {
            cart.removeAllViews(); double sum=0;
            for (int i=0;i<cartItems.size();i++) {
                final int idx=i; Service s=cartItems.get(i); int q=quantities.get(i); sum+=s.rate*q;
                LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
                TextView label=text(s.name+"  ₹"+money(s.rate)+" × "+q+" = ₹"+money(s.rate*q),15,Color.BLACK);
                row.addView(label,new LinearLayout.LayoutParams(0,-2,1));
                row.addView(button("−",()->{ quantities.set(idx,Math.max(1,quantities.get(idx)-1)); refresh.run(); }));
                row.addView(button("+",()->{ quantities.set(idx,quantities.get(idx)+1); refresh.run(); }));
                row.addView(button("×",()->{cartItems.remove(idx);quantities.remove(idx);refresh.run();}));
                cart.addView(row);
            }
            total.setText("एकूण: ₹"+money(sum));
        };
        for (Service s : services) {
            content.addView(button(s.name+" — ₹"+money(s.rate), () -> {
                int found=-1; for(int i=0;i<cartItems.size();i++) if(cartItems.get(i).name.equals(s.name)) found=i;
                if(found>=0) quantities.set(found,quantities.get(found)+1); else {cartItems.add(s);quantities.add(1);}
                refresh.run();
            }));
        }
        content.addView(button("बिल सेव्ह करा (डेमो)", () -> {
            double sum=0; for(int i=0;i<cartItems.size();i++) sum+=cartItems.get(i).rate*quantities.get(i);
            if(sum<=0){toast("कृपया सेवा निवडा");return;}
            saveSale(customerInput.getText().toString(),sum,"सेवा बिल");
            new AlertDialog.Builder(this).setTitle("डेमो बिल").setMessage("एकूण रक्कम: ₹"+money(sum)+"\nपेमेंट निवडण्यासाठी पुढील स्क्रीन वापरा.").setPositiveButton("ठीक", (d,w)->showHome()).show();
        }));
        content.addView(button("मागे",this::showHome));
    }
    void showQuickBill() {
        begin("जलद बिल — फक्त एकूण रक्कम");
        customerInput=edit("ग्राहकाचे नाव (ऐच्छिक)",1); content.addView(customerInput);
        amountInput=edit("एकूण रक्कम ₹", android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); content.addView(amountInput);
        Spinner spinner=new Spinner(this);
        String[] pay={"Cash","UPI","Bank","उधारी"};
        spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,pay)); content.addView(text("पेमेंट प्रकार",15,navy)); content.addView(spinner);
        content.addView(button("बिल सेव्ह करा (डेमो)",()->{
            try {double a=Double.parseDouble(amountInput.getText().toString()); if(a<=0)throw new Exception();
                saveSale(customerInput.getText().toString(),a,"जलद बिल / "+spinner.getSelectedItem());
                toast("डेमो बिल नोंदवले"); showHome();
            } catch(Exception ex){toast("योग्य रक्कम टाका");}
        }));
        content.addView(button("मागे",this::showHome));
    }
    void showAddService() {
        begin("नवीन सेवा जोडा");
        serviceNameInput=edit("सेवेचे नाव",1); content.addView(serviceNameInput);
        serviceRateInput=edit("दर ₹",android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); content.addView(serviceRateInput);
        content.addView(button("सेवा जोडा (डेमो)",()->{
            String n=serviceNameInput.getText().toString().trim();
            try {double r=Double.parseDouble(serviceRateInput.getText().toString()); if(n.isEmpty()||r<0)throw new Exception();
                services.add(new Service(n,r)); toast("सेवा या डेमोमध्ये जोडली"); showHome();
            }catch(Exception ex){toast("सेवेचे नाव आणि योग्य दर टाका");}
        }));
        content.addView(button("मागे",this::showHome));
    }
    void showExpense() {
        begin("खर्च नोंदवा");
        expenseNameInput=edit("खर्चाचे नाव — उदा. इंटरनेट, वीज",1); content.addView(expenseNameInput);
        expenseAmountInput=edit("रक्कम ₹",android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); content.addView(expenseAmountInput);
        Spinner p=new Spinner(this); p.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Cash","UPI","Bank"}));
        content.addView(text("पेमेंट प्रकार",15,navy)); content.addView(p);
        content.addView(button("खर्च सेव्ह करा (डेमो)",()->{
            try {double a=Double.parseDouble(expenseAmountInput.getText().toString()); if(a<=0)throw new Exception();
                expenseTotal+=a; transactions.add("खर्च: "+expenseNameInput.getText()+" ₹"+money(a)+" / "+p.getSelectedItem());
                toast("खर्च या डेमोमध्ये नोंदवला"); showHome();
            }catch(Exception ex){toast("खर्चाचे नाव आणि योग्य रक्कम टाका");}
        }));
        content.addView(button("मागे",this::showHome));
    }
    void showReport() {
        begin("दैनिक / मासिक रिपोर्ट");
        content.addView(text("या डेमोमधील विक्री: ₹"+money(salesTotal),20,green));
        content.addView(text("या डेमोमधील खर्च: ₹"+money(expenseTotal),20,Color.RED));
        content.addView(text("तात्पुरता फरक: ₹"+money(salesTotal-expenseTotal),20,navy));
        addSection("अलीकडील नोंदी");
        if(transactions.isEmpty()) content.addView(text("अजून नोंदी नाहीत.",15,Color.DKGRAY));
        else for(int i=transactions.size()-1;i>=0;i--) content.addView(text("• "+transactions.get(i),14,Color.DKGRAY));
        content.addView(button("मागे",this::showHome));
    }
    void showSyncInfo() {
        begin("Cloud Sync / Google Sheets");
        content.addView(text("ही प्रारंभिक डेमो आवृत्ती आहे. दोन मोबाइल सिंक आणि Google Sheets बॅकअप अद्याप सक्रिय नाही.",17,Color.DKGRAY));
        content.addView(text("पूर्ण करण्यासाठी आवश्यक:",16,navy));
        content.addView(text("1. Firebase प्रोजेक्ट व Android app नोंदणी\n2. google-services.json\n3. दोन्ही पार्टनरसाठी लॉगिन/परवानग्या\n4. Firestore database rules\n5. Google Sheets API साठी सुरक्षित server-side integration\n6. बॅकअप pending/synced/failed स्थिती व retry",15,Color.DKGRAY));
        content.addView(button("मागे",this::showHome));
    }
    void saveSale(String customer,double amount,String type) {
        salesTotal += amount;
        transactions.add(type+": "+(customer==null||customer.trim().isEmpty()?"Walk-in":customer.trim())+" ₹"+money(amount));
    }
    String money(double n) { return String.format(Locale.US,"%.2f",n).replaceAll("\\.00$",""); }
    void toast(String s) { Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
