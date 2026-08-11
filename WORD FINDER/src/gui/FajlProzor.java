package gui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FajlProzor extends Dialog {
    String path = null;
    TextField textbox;

    public FajlProzor(Frame vlasnik, String Title, String Standard){
        super(vlasnik, Title, true);

        setLayout(new BorderLayout());
        Label L = new Label("Unesi putanju:");
        L.setAlignment(Label.CENTER);
        add(L, BorderLayout.NORTH);

        Panel textboxDeo = new Panel(new FlowLayout());
        textbox = new TextField(Standard, 45);
        textbox.setForeground(Color.GRAY);
        textbox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textbox.getText().equals(Standard)) {
                    textbox.setText("");
                    textbox.setForeground(Color.BLACK);   // real text color
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textbox.getText().isEmpty()) {
                    textbox.setText(Standard);
                    textbox.setForeground(Color.GRAY);
                }
            }
        });
        textboxDeo.add(textbox);
        add(textboxDeo, BorderLayout.CENTER);

        Panel dugmiciDeo = new Panel(new FlowLayout());
        Button uredu = new Button("U redu");
        Button otkazi = new Button("Otkazi");
        otkazi.addActionListener(e -> dispose());
        uredu.addActionListener(e -> potvrdi());
        dugmiciDeo.add(otkazi);
        dugmiciDeo.add(uredu);
        add(dugmiciDeo, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false); dispose();
            }
        });

        setSize(500, 125);
        setLocation(50, 80);
    }

    private void potvrdi(){
        String T = textbox.getText().trim();
        if (!T.isEmpty()){
            path = T;
        }
        dispose();
    }

    public static String nadjiPath(Frame vlasnik, String naslov, String podrazumevano){
        FajlProzor prozor = new FajlProzor(vlasnik, naslov, podrazumevano);
        prozor.setVisible(true);
        return prozor.path;
    }
}
