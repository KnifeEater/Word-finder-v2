package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class YesNoDialog extends Dialog {
    enum IZBOR {NO, YES};
    public IZBOR dane;

    public YesNoDialog(Frame owner, String text) {
        super(owner, "INFO", true);

        setLayout(new BorderLayout());

        Label center = new Label(text, Label.CENTER);
        Panel centerLabel = new Panel(new FlowLayout());
        centerLabel.add(center);
        add(centerLabel, BorderLayout.CENTER);

        Button noButt = new Button("NO");
        Button yesButt = new Button("YES");
        noButt.addActionListener(e -> {dane = IZBOR.NO; dispose();});
        yesButt.addActionListener(e -> {dane = IZBOR.YES; dispose();});
        Panel buttonRibbon = new Panel(new FlowLayout());
        buttonRibbon.add(noButt);
        buttonRibbon.add(yesButt);
        add(buttonRibbon, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dane = IZBOR.NO;
                dispose();
            }
        });

        setSize(400, 150);
        setLocation(50, 80);
        setVisible(true);
    }
}
