package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GreskaProzor extends Dialog {

    public GreskaProzor(Frame owner, String greska, boolean modul) {
        super(owner, "GRESKA", modul);

        setLayout(new BorderLayout());
        Panel textboxP = new Panel(new FlowLayout());
        TextField T = new TextField(greska);
        T.setEditable(false);
        textboxP.add(T);
        add(textboxP, BorderLayout.CENTER);

        FlowLayout A = new FlowLayout(); A.setAlignment(FlowLayout.CENTER);
        Panel buttonP = new Panel(A);
        Button b = new Button("U redu");
        b.addActionListener(e -> dispose());
        buttonP.add(b);
        add(buttonP, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false); dispose();
            }
        });
        setSize(500, 125);
        setLocation(50, 80);
        setVisible(true);
    }
}
