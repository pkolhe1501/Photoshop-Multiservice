
package com.example.photoshopmultiservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout root, content;
    double salesTotal = 0, expenseTotal = 0;

    final ArrayList<Service> services = new ArrayList<>();
    final ArrayList<String> transactions = new ArrayList<>();

    final int navy = Color.rgb(8, 63, 120);
    final int green = Color.rgb(16, 145, 76);

    static class Service {
        String name;
        double rate;

        Service(String name, double rate) {
            this.name = name;
            this.rate = rate;
        }
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        services.add(new Service("A4 Xerox", 3));
        services.add(new Service("Color Print", 10));
        services.add(new Service("Photo Print", 15));
        services.add(new Service("Lamination", 20));
        services.add(new Service("Passport Photo", 40));

        showHome();
    }

    String money(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    String now() {
        return new SimpleDateFormat(
            "dd-MM-yyyy HH:mm", Locale.getDefault()
        ).format(new Date());
    }

    String today() {
        return new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).format(new Date());
    }

    String monthNow() {
        return new SimpleDateFormat(
            "yyyy-MM", Locale.getDefault()
        ).format(new Date());
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    Button button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    EditText edit(String hint, int type) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        e.setInputType(type);
        return e;
    }

    void begin(String title) {
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
        root.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        root.addView(button("← मुख्य पान", this::showHome));
        setContentView(root);
    }

    void addSection(String title) {
        content.addView(text(title, 18, navy));
    }

    void showHome() {
        begin("Photoshop & Multiservice");

        content.addView(text(now() + " • Dashboard", 14,
            Color.DKGRAY));
        content.addView(text(
            "आजची नोंदवलेली एकूण विक्री: ₹" + money(salesTotal),
            20, green));
        content.addView(text(
            "नोंदवलेला एकूण खर्च: ₹" + money(expenseTotal),
            18, Color.RED));
        content.addView(text(
            "अंदाजे शिल्लक: ₹" + money(salesTotal - expenseTotal),
            18, navy));

        content.addView(button(
            "नवीन बिल / सेवा बिलिंग", this::showServiceBilling));
        content.addView(button(
            "जलद बिल — फक्त रक्कम", this::showQuickBill));
        content.addView(button(
            "नवीन सेवा व दर जोडा", this::showAddService));
        content.addView(button(
            "दुकानाचा खर्च नोंदवा", this::showExpense));
        content.addView(button(
            "दैनिक / मासिक रिपोर्ट", this::showReport));

        content.addView(text(
            "टीप: ही स्थानिक डेमो आवृत्ती आहे. " +
            "अॅप बंद केल्यावर डेटा टिकवणे आणि Cloud Backup " +
            "या आवृत्तीत जोडलेले नाही.",
            13, Color.DKGRAY));
    }

    void showServiceBilling() {
        begin("सेवा बिलिंग");

        EditText customer = edit(
            "ग्राहकाचे नाव (ऐच्छिक)",
            InputType.TYPE_CLASS_TEXT);
        content.addView(customer);

        addSection("सेवेवर टॅप करा — बिलात जोडा");

        LinearLayout serviceList = new LinearLayout(this);
        serviceList.setOrientation(LinearLayout.VERTICAL);
        content.addView(serviceList);

        LinearLayout cart = new LinearLayout(this);
        cart.setOrientation(LinearLayout.VERTICAL);
        content.addView(cart);

        ArrayList<Service> cartItems = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();

        TextView total = text("एकूण: ₹0.00", 22, green);
        content.addView(total);

        Runnable[] refresh = new Runnable[1];

        refresh[0] = () -> {
            cart.removeAllViews();
            double sum = 0;

            for (int i = 0; i < cartItems.size(); i++) {
                final int idx = i;
                Service s = cartItems.get(i);
                int q = quantities.get(i);
                sum += s.rate * q;

                LinearLayout row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER_VERTICAL);

                TextView label = text(
                    s.name + "\n₹" + money(s.rate) +
                    " × " + q + " = ₹" + money(s.rate * q),
                    14, Color.BLACK);

                row.addView(label,
                    new LinearLayout.LayoutParams(0, -2, 1));

                row.addView(button("−", () -> {
                    int next = quantities.get(idx) - 1;
                    if (next <= 0) {
                        cartItems.remove(idx);
                        quantities.remove(idx);
                    } else {
                        quantities.set(idx, next);
                    }
                    refresh[0].run();
                }));

                row.addView(button("+", () -> {
                    quantities.set(idx, quantities.get(idx) + 1);
                    refresh[0].run();
                }));

                row.addView(button("×", () -> {
                    cartItems.remove(idx);
                    quantities.remove(idx);
                    refresh[0].run();
                }));

                cart.addView(row);
            }

            total.setText("एकूण: ₹" + money(sum));
        };

        Runnable drawServices = () -> {
            serviceList.removeAllViews();

            for (Service s : services) {
                serviceList.addView(button(
                    s.name + " — ₹" + money(s.rate),
                    () -> {
                        int found = -1;

                        for (int i = 0; i < cartItems.size(); i++) {
                            if (cartItems.get(i) == s) {
                                found = i;
                                break;
                            }
                        }

                        if (found >= 0) {
                            quantities.set(found,
                                quantities.get(found) + 1);
                        } else {
                            cartItems.add(s);
                            quantities.add(1);
                        }

                        refresh[0].run();
                    }));
            }
        };

        drawServices.run();

        content.addView(button("बिल सेव्ह करा", () -> {
            double sum = 0;

            for (int i = 0; i < cartItems.size(); i++) {
                sum += cartItems.get(i).rate * quantities.get(i);
            }

            if (sum <= 0) {
                Toast.makeText(this, "आधी सेवा निवडा",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            String name = customer.getText().toString().trim();
            salesTotal += sum;

            transactions.add(
                "SALE|" + now() + "|" + today() + "|" +
                monthNow() + "|" + name + "|" + money(sum));

            Toast.makeText(this,
                "बिल सेव्ह झाले: ₹" + money(sum),
                Toast.LENGTH_LONG).show();

            showHome();
        }));
    }

    void showQuickBill() {
        begin("जलद बिल");

        EditText customer = edit(
            "ग्राहकाचे नाव (ऐच्छिक)",
            InputType.TYPE_CLASS_TEXT);
        content.addView(customer);

        EditText amount = edit(
            "एकूण रक्कम ₹",
            InputType.TYPE_CLASS_NUMBER |
            InputType.TYPE_NUMBER_FLAG_DECIMAL);
        content.addView(amount);

        content.addView(button("बिल सेव्ह करा", () -> {
            String value = amount.getText().toString().trim();

            if (value.isEmpty()) {
                amount.setError("रक्कम टाका");
                return;
            }

            double sum;
            try {
                sum = Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                amount.setError("योग्य रक्कम टाका");
                return;
            }

            if (sum <= 0) {
                amount.setError("रक्कम शून्यापेक्षा जास्त असावी");
                return;
            }

            salesTotal += sum;

            transactions.add(
                "QUICK|" + now() + "|" + today() + "|" +
                monthNow() + "|" +
                customer.getText().toString().trim() +
                "|" + money(sum));

            Toast.makeText(this,
                "बिल सेव्ह झाले: ₹" + money(sum),
                Toast.LENGTH_LONG).show();

            showHome();
        }));
    }

    void showAddService() {
        begin("नवीन सेवा जोडा");

        EditText name = edit(
            "सेवेचे नाव",
            InputType.TYPE_CLASS_TEXT);
        content.addView(name);

        EditText rate = edit(
            "सेवेचा दर ₹",
            InputType.TYPE_CLASS_NUMBER |
            InputType.TYPE_NUMBER_FLAG_DECIMAL);
        content.addView(rate);

        content.addView(button("सेवा सेव्ह करा", () -> {
            String n = name.getText().toString().trim();
            String r = rate.getText().toString().trim();

            if (n.isEmpty()) {
                name.setError("सेवेचे नाव टाका");
                return;
            }

            if (r.isEmpty()) {
                rate.setError("दर टाका");
                return;
            }

            try {
                double value = Double.parseDouble(r);

                if (value < 0) {
                    rate.setError("दर ऋण असू शकत नाही");
                    return;
                }

                services.add(new Service(n, value));

                Toast.makeText(this, "नवीन सेवा जोडली",
                    Toast.LENGTH_SHORT).show();

                showHome();
            } catch (NumberFormatException ex) {
                rate.setError("योग्य दर टाका");
            }
        }));
    }

    void showExpense() {
        begin("दुकानाचा खर्च");

        EditText name = edit(
            "खर्चाचे कारण",
            InputType.TYPE_CLASS_TEXT);
        content.addView(name);

        EditText amount = edit(
            "खर्चाची रक्कम ₹",
            InputType.TYPE_CLASS_NUMBER |
            InputType.TYPE_NUMBER_FLAG_DECIMAL);
        content.addView(amount);

        content.addView(button("खर्च सेव्ह करा", () -> {
            String n = name.getText().toString().trim();
            String a = amount.getText().toString().trim();

            if (n.isEmpty()) {
                name.setError("खर्चाचे कारण टाका");
                return;
            }

            if (a.isEmpty()) {
                amount.setError("रक्कम टाका");
                return;
            }

            try {
                double value = Double.parseDouble(a);

                if (value <= 0) {
                    amount.setError("रक्कम शून्यापेक्षा जास्त असावी");
                    return;
                }

                expenseTotal += value;

                transactions.add(
                    "EXPENSE|" + now() + "|" + today() + "|" +
                    monthNow() + "|" + n + "|" + money(value));

                Toast.makeText(this, "खर्च सेव्ह झाला",
                    Toast.LENGTH_SHORT).show();

                showHome();
            } catch (NumberFormatException ex) {
                amount.setError("योग्य रक्कम टाका");
            }
        }));
    }

    void showReport() {
        begin("दैनिक / मासिक रिपोर्ट");

        double todaySales = 0;
        double todayExpenses = 0;
        double monthSales = 0;
        double monthExpenses = 0;

        for (String record : transactions) {
            String[] parts = record.split("\\|", -1);

            if (parts.length < 6) continue;

            double amount;

            try {
                amount = Double.parseDouble(parts[5]);
            } catch (NumberFormatException ex) {
                continue;
            }

            boolean sale = parts[0].equals("SALE") ||
                           parts[0].equals("QUICK");
            boolean expense = parts[0].equals("EXPENSE");

            if (parts[2].equals(today())) {
                if (sale) todaySales += amount;
                if (expense) todayExpenses += amount;
            }

            if (parts[3].equals(monthNow())) {
                if (sale) monthSales += amount;
                if (expense) monthExpenses += amount;
            }
        }

        addSection("आजचा रिपोर्ट");
        content.addView(text(
            "विक्री: ₹" + money(todaySales), 18, green));
        content.addView(text(
            "खर्च: ₹" + money(todayExpenses), 18, Color.RED));
        content.addView(text(
            "शिल्लक: ₹" + money(todaySales - todayExpenses),
            18, navy));

        addSection("चालू महिन्याचा रिपोर्ट");
        content.addView(text(
            "विक्री: ₹" + money(monthSales), 18, green));
        content.addView(text(
            "खर्च: ₹" + money(monthExpenses), 18, Color.RED));
        content.addView(text(
            "शिल्लक: ₹" + money(monthSales - monthExpenses),
            18, navy));

        addSection("अलीकडील नोंदी");

        if (transactions.isEmpty()) {
            content.addView(text(
                "अजून कोणतीही नोंद नाही.", 15, Color.DKGRAY));
        } else {
            for (int i = transactions.size() - 1;
                 i >= 0; i--) {
                content.addView(text(
                    transactions.get(i).replace("|", "  •  "),
                    13, Color.DKGRAY));
            }
        }
    }
}
