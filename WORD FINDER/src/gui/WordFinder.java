package gui;

import mics.Constants;
import mics.CsvJsonFajl;
import mics.Utilities;
import model.Letter;
import model.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Pattern;
//import java.util.regex.Matcher;

public class WordFinder extends JFrame {
    private static final String PREFIX = "Word length:";

    private Checkbox excludeRep;
    private Checkbox addTextbox;
    private Checkbox displayRank;

    private Label labelForWordLength;
    private JSpinner wordLengthUpDpwn;
    private JComboBox<String> ch;

    private Panel lettersBoxPanel;
    private ArrayList<JTextField> textBoxesInPlace;
    private JTextField containsField;

    private Button nextButt;
    private Button restartButt;
    private Button searchButt;
    private Button resetButt;

    private JTextField bannedTextbox;
    private Label solutionLabel;
    private Label tempLabel;

    public WordFinder() {
        super("Word finder");

        setLayout(new BorderLayout());
        Panel mainPanel = new Panel(new GridLayout(6, 1, 0, 10));

        // Row 1: Settings label + first checkbox
        Panel settings1 = new Panel(new FlowLayout(FlowLayout.LEFT));
        settings1.add(new Label("Settings:"));
        Panel checkboxes = new Panel(new GridLayout(3, 1));
        this.excludeRep = new Checkbox("Exclude repetition of the letters (unless necesary)");
        this.addTextbox = new Checkbox("Add a texbox for not desireable letters");
        this.displayRank = new Checkbox("Display rank");
        addTextbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkboxMiniReset();
                Checkbox T = (Checkbox) e.getSource();
                bannedTextbox.setVisible(T.getState());
                bannedTextbox.setText("");
                revalidate();
            }
        });
        excludeRep.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkboxMiniReset();
            }
        });
        displayRank.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                checkboxMiniReset();
            }
        });
        checkboxes.add(excludeRep);
        checkboxes.add(addTextbox);
        checkboxes.add(displayRank);
        settings1.add(checkboxes);
        mainPanel.add(settings1);

        // Row 2: Word length
        Panel wordLengthPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        this.labelForWordLength = new Label(PREFIX, Label.RIGHT);

        wordLengthPanel.add(new Label("Algorithm:", Label.RIGHT));
        String[] algorithms = {"Normal", "Perfect", "Binary"};
        this.ch = new JComboBox<>(algorithms);
        ch.setSelectedIndex(1);
        ch.addActionListener(e -> textChangedGUI());
        wordLengthPanel.add(ch);

        wordLengthPanel.add(labelForWordLength);
        this.wordLengthUpDpwn = new JSpinner(new SpinnerNumberModel(5,1,11,1));
        wordLengthUpDpwn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                makeTextFields();
                textChangedGUI();
            }
        });
        wordLengthPanel.add(wordLengthUpDpwn);
        mainPanel.add(wordLengthPanel);

        // Row 3: label i BACK (ne) dugme + textboxes
        Panel lettersHeader = new Panel(new BorderLayout());

        Panel labelAndRightTextboxes = new Panel(new GridLayout(2, 0));
        Label lettersLabel = new Label("Letters in the right place:", Label.CENTER);
        labelAndRightTextboxes.add(lettersLabel);

        createFields();
        makeTextFields();

        labelAndRightTextboxes.add(lettersBoxPanel);
        lettersHeader.add(labelAndRightTextboxes, BorderLayout.CENTER);

        mainPanel.add(lettersHeader);


        // Row 4: "Contains those letters:" label
        Panel labelAndContainsTextbox = new Panel(new GridLayout(2,1));
        Panel flowForContainsTextbox = new Panel(new FlowLayout());
        labelAndContainsTextbox.add(new Label("Contains those letters:", Label.CENTER));
        this.containsField = new JTextField(26);
        ((AbstractDocument) containsField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[A-Za-z]+")){
                    textChangedGUI();
                    fb.insertString(offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() == length && text.isEmpty()) {fb.replace(offset, length, text, attrs);}
                else if (text.matches("[A-Za-z]+")) {
                    textChangedGUI();
                    fb.replace(offset, length, text, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                textChangedGUI(); //Remove if nessecary
                fb.remove(offset, length);
            }
        });
        containsField.setHorizontalAlignment(JTextField.CENTER);
        flowForContainsTextbox.add(containsField);
        labelAndContainsTextbox.add(flowForContainsTextbox);
        mainPanel.add(labelAndContainsTextbox);


        // Row 5: NEXT + Restart + Search
        Panel buttonGridThing = new Panel(new GridLayout(2, 1));

        Panel navButtons = new Panel(new FlowLayout(FlowLayout.CENTER));
        this.nextButt = new Button("NEXT");
        this.restartButt = new Button("Restart");
        this.searchButt = new Button("Search");
        nextButt.addActionListener(e -> nextButt());
        restartButt.addActionListener(e -> restartButt());
        searchButt.addActionListener(e -> searchButt());
        nextButt.setVisible(false);
        restartButt.setVisible(false);
        navButtons.add(nextButt);
        navButtons.add(restartButt);
        navButtons.add(searchButt);
        buttonGridThing.add(navButtons);

        Panel resetButtonPart = new Panel(new FlowLayout(FlowLayout.CENTER));
        this.resetButt = new Button("RESET");
        resetButt.addActionListener(e -> resetButt());
        resetButtonPart.add(resetButt);
        buttonGridThing.add(resetButtonPart);

        mainPanel.add(buttonGridThing);

        // Row 6: wide textbox
        Panel bannedLetters = new Panel(new FlowLayout(FlowLayout.CENTER));
        this.bannedTextbox = new JTextField(26);
        ((AbstractDocument) bannedTextbox.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[A-Za-z]+")){
                    textChangedGUI();
                    fb.insertString(offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() == length && text.isEmpty()) {fb.replace(offset, length, text, attrs);}
                else if (text.matches("[A-Za-z]+")) {
                    textChangedGUI();
                    fb.replace(offset, length, text, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                textChangedGUI(); //Remove if nessecary
                fb.remove(offset, length);
            }
        });
        bannedTextbox.setHorizontalAlignment(JTextField.CENTER);
        bannedTextbox.setVisible(false);
        bannedLetters.add(bannedTextbox, BorderLayout.NORTH);
        mainPanel.add(bannedLetters);

        add(mainPanel, BorderLayout.CENTER);   // <-- KEY FIX: was CENTER

// ---------- SOUTH: WORD FOUND (left) + Process (right) ----------
        Panel bottomPanel = new Panel(new BorderLayout());

        Panel wordFoundPanel = new Panel(new GridLayout(2, 1));
        this.solutionLabel = new Label("WORD FOUND:");
        this.tempLabel = new Label("0");
        tempLabel.setVisible(false);
        solutionLabel.setVisible(false);
        solutionLabel.setPreferredSize(new Dimension(250, solutionLabel.getPreferredSize().height));
        wordFoundPanel.add(tempLabel);
        wordFoundPanel.add(solutionLabel);
        bottomPanel.add(wordFoundPanel, BorderLayout.WEST);

        //--------------------------------MENUBAR--------------------------------
        MenuBar processPanel = new MenuBar();
        Menu opcije = new Menu("Options");
        MenuItem importDict = new MenuItem("Change dictionary");
        MenuItem importFreq = new MenuItem("Custom frequency");
        MenuItem resetFreq = new MenuItem("Reset frequency");
        importDict.addActionListener(e -> importDIC());
        importFreq.addActionListener(e -> importFreq());
        resetFreq.addActionListener(e -> resetFreq());
        opcije.add(importDict);
        opcije.add(importFreq);
        opcije.addSeparator();
        opcije.add(resetFreq);
        processPanel.add(opcije);
        setMenuBar(processPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { setVisible(false); dispose(); System.exit(0);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                try {VerifyApplicationDataFiles();} catch (Exception exc) {dispose();}
            }
        });

        setSize(650, 675);
        setLocation(50, 80);
        setVisible(true);
    }

    private void VerifyApplicationDataFiles() throws IOException {
        String applicationDataFolder = Utilities.GetApplicationDataFolder();
        Files.createDirectories(Paths.get(applicationDataFolder));
        String[] files = new String[] { Constants.PTempFileName, /*Constants.TempFileName,*/ Constants.LetterRankFileName, /*Constants.WordsRankFileName,*/ Constants.WordsAlphaFileName };
        for (String file : files)
        {
            String fullTargetFilePath = Utilities.GetApplicationDataFile(file);
            if (Files.exists(Paths.get(fullTargetFilePath))) { continue; }

            try (BufferedReader stream = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/Files/" + file)))) {

                String fileContent = stream.lines().collect(Collectors.joining("\n"));
                Files.writeString(Paths.get(fullTargetFilePath), fileContent);
            }
        }
        //System.out.println("PASS"); //DEBUG
    }

    //CREATE JTEXTFIELDS (samo se jednom koristi, on start up)
    private void createFields(){
        this.lettersBoxPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        this.textBoxesInPlace = new ArrayList<>();

        JTextField before = null;
        for (int i = 0; i < 10; i++) {
            JTextField tempText = new JTextField();
            tempText.setPreferredSize(new Dimension(22, 25));
            if (before != null) {
                int finalI = i;
                before.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    SwingUtilities.invokeLater(() -> textBoxesInPlace.get(finalI).requestFocusInWindow());
                }
                @Override
                public void removeUpdate(DocumentEvent e) {textChangedGUI();} //Remove if nessecary
                @Override
                public void changedUpdate(DocumentEvent e) {textChangedGUI();} //Remove if nessecary
            });}
            ((AbstractDocument) tempText.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (fb.getDocument().getLength() + string.length() <= 1 && string.matches("[A-Za-z]")){
                        textChangedGUI();
                        fb.insertString(offset, string, attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (fb.getDocument().getLength() == length && text.isEmpty()) {fb.replace(offset, length, text, attrs);}
                    else if (text.matches("[A-Za-z]") && fb.getDocument().getLength() - length + text.length() <= 1) {
                        textChangedGUI();
                        fb.replace(offset, length, text, attrs);
                    }
                }
            });
            tempText.setHorizontalAlignment(JTextField.CENTER);
            textBoxesInPlace.add(tempText);
            lettersBoxPanel.add(tempText);
            before = tempText;
        }
        JTextField tempText2 = new JTextField();
        tempText2.setPreferredSize(new Dimension(200, 25));
        tempText2.setHorizontalAlignment(JTextField.CENTER);
        ((AbstractDocument) tempText2.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[A-Za-z_]+")){
                    textChangedGUI();
                    fb.insertString(offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() == length && text.isEmpty()) {fb.replace(offset, length, text, attrs);}
                else if (text.matches("[A-Za-z_]+")) {
                    textChangedGUI();
                    fb.replace(offset, length, text, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                textChangedGUI(); //Remove if nessecary
                fb.remove(offset, length);
            }
        });
        textBoxesInPlace.add(tempText2);
        lettersBoxPanel.add(tempText2);

        lettersBoxPanel.revalidate();
    }

    //MAKE JTEXTFIELDS
    private void makeTextFields(){ //(FINAL)
        clearTextBoxes();
        int wordLen = (Integer) wordLengthUpDpwn.getValue();

        if (wordLen != 11) {textBoxesInPlace.get(10).setVisible(false); labelForWordLength.setText(PREFIX);}
        for (int i = 0; i < 10; i++) {
            if (wordLen == 11) {textBoxesInPlace.get(i).setVisible(false);}
            else if (i < wordLen) {textBoxesInPlace.get(i).setVisible(true);}
            else {textBoxesInPlace.get(i).setVisible(false);}
        }
        if (wordLen == 11) { textBoxesInPlace.get(10).setVisible(true); labelForWordLength.setText(PREFIX + " 10+"); }

        lettersBoxPanel.revalidate();
    }

    private void clearTextBoxes(){ //(FINAL)
        for (JTextField txt : textBoxesInPlace){
            txt.setText("");
        }
    }

    //RESET BUTTON
    private void resetButt(){ //(FINAL)
        clearTextBoxes();
        bannedTextbox.setText("");
        containsField.setText("");
        solutionLabel.setText(PREFIX);
        solutionLabel.setVisible(false);
        nextButt.setVisible(false);
        restartButt.setVisible(false);
        resetButt.setVisible(true);
        reset();
    }
    private void reset(){ //Soft reset (temp files)
        try (BufferedWriter _ = new BufferedWriter(new FileWriter(Utilities.GetApplicationDataFile(Constants.PTempFileName), StandardCharsets.UTF_8))) {} //Rewrites everything
        catch (IOException e) { new GreskaProzor(this, e.toString(), true); return; }
        searchButt.setVisible(true);
        tempLabel.setText("0");
    }

    //SEARCH
    private void searchButt(){
        toUpperFields(); //Style change
        Popunjavanje();
        revalidate();
    }
    private void Popunjavanje() { //fill-up er
        int wrdL = WordLength();
        String U = "";
        String charsR;
        int chRLen = 0;
        if (wrdL > 10)
        {
            U = textBoxesInPlace.get(10).getText().toLowerCase().replace("_", "\\w");
            chRLen = textBoxesInPlace.get(10).getText().replace("_", "").length();
            charsR = "\\b" + U + "\\b";
        }
        else
        {
            if (textBoxesInPlace.get(10).isVisible()) { solutionLabel.setVisible(true); solutionLabel.setText("INPUT ERROR"); return; }
            for (int o = 0; o < wrdL; o++)
            {
                String tempStr = textBoxesInPlace.get(o).getText();
                U += tempStr.trim().isEmpty() ? "\\w" : tempStr.toLowerCase();
                if (!tempStr.trim().isEmpty()) {chRLen++;}
            }
            charsR = "\\b" + U + "\\b";
        }
        char[] charsY = containsField.getText().toLowerCase().toCharArray();
        Checker(charsY, charsR, wrdL, chRLen);
    }

    private void Checker(char[] charsY, String charsR, int wordLength, int chRLen)//before output, important
    { //checks if code can safely proceed :-)
        int len = containsField.getText().length() + chRLen;
        if (len > wordLength) { solutionLabel.setText("INVALID INPUT"); solutionLabel.setVisible(true); return; }
        try { Perfect_search(charsR, charsY, wordLength); } catch (Exception e) {new GreskaProzor(this, e.toString(), true); return; }
        if (!solutionLabel.isVisible()) { solutionLabel.setText("WORD NOT FOUND"); solutionLabel.setVisible(true); }
    }

    private void Perfect_search(String charsR, char[] charsY, int wordLength) throws IOException//Searches the words based on the rank of the letters that are in canditate words (calculates most common letters in those words) (PERFECT ALGORITHM, I think???)
    {
        char[] charsYprobna = charsY;   // reference reassignment — safe, see note below
        ArrayList<Word> Canditates = new ArrayList<>();

        String WordsTEXT = Files.readString(Paths.get(Utilities.GetApplicationDataFile(Constants.WordsAlphaFileName)));

        Pattern charsRPattern = Pattern.compile(charsR);   // compile once

        Matcher KOMBS = charsRPattern.matcher(WordsTEXT);   // charsR must be a Pattern, not a String

        //Combing trough words
        while (KOMBS.find()) {
            String value = KOMBS.group();
            charsY = charsYprobna;

            if (value.length() != wordLength) {
                continue;
            }

            for (int i = 0; i < wordLength; i++) {
                char lower = Character.toLowerCase(value.charAt(i));
                if (containsChar(charsY, lower)) {
                    int numIndex = indexOfChar(charsY, lower);
                    char[] probna = new char[charsY.length - 1];
                    System.arraycopy(charsY, 0, probna, 0, numIndex);
                    System.arraycopy(charsY, numIndex + 1, probna, numIndex, charsY.length - 1 - numIndex);
                    charsY = probna;
                }
            }

            if (charsY.length != 0) {
                continue;
            }

            if (excludeRep.getState() && !duplicate(value.toLowerCase(), charsYprobna, charsR, wordLength)) {
                continue;
            }
            if (addTextbox.getState() && !Checkerv2(value.toLowerCase(), wordLength)) {
                continue;
            }

            Canditates.add(new Word(value));
        }

        if (!Canditates.isEmpty()) { //DOSTA KOSTA
            try (BufferedWriter A = Files.newBufferedWriter(Paths.get(Utilities.GetApplicationDataFile(Constants.PTempFileName)))) {
                for (Word P : Canditates) {
                    A.write(P.getName());
                    A.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Letter[] Letters = null;
            try {Letters = rankedLetterArray(); }  // SPECIAL RANKED LETTERS
            catch (Exception e) {new GreskaProzor(this, "Please import dictionary/alphabet .txt file!", true); }
            for (Word UU : Canditates) {
                UU.rank(Letters);
            }     // NOW IS RANKING

            ((List<Word>) Canditates).sort(Word::wordComparator);

            try (BufferedWriter A = Files.newBufferedWriter(Paths.get(Utilities.GetApplicationDataFile(Constants.PTempFileName)))) {
                for (Word P : Canditates) {
                    A.write(P.getName() + " (" + P.getPop() + ")");
                    A.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ArrayList<String> I;
            try {
                I = (ArrayList<String>) Files.readAllLines(Paths.get(Utilities.GetApplicationDataFile(Constants.PTempFileName)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                if (displayRank.getState()) {
                    solutionLabel.setText("WORD FOUND: " + I.getFirst());
                } else {
                    solutionLabel.setText("WORD FOUND: " + I.getFirst().split(" \\(")[0]);
                }
                solutionLabel.setVisible(true);
                nextButt.setVisible(true);
                restartButt.setVisible(true);
                searchButt.setVisible(false);
            } catch (Exception e) {
                solutionLabel.setText("WORD NOT FOUND");
            }
        }
    }

    private Letter[] rankedLetterArray() throws Exception //returns most common letters sorted in an array
    {
        String[] Words = Files.readAllLines(Paths.get(Utilities.GetApplicationDataFile(ch.getSelectedIndex() == 0 ? Constants.WordsAlphaFileName : Constants.PTempFileName))).toArray(new String[0]);
        String customL = Files.readString(Paths.get(Utilities.GetApplicationDataFile(Constants.LetterRankFileName)));
        if (!customL.trim().isEmpty()) {
            List<String> customLLines = Files.readAllLines(Paths.get(Utilities.GetApplicationDataFile(Constants.LetterRankFileName)));
            Letter[] customRanked = new Letter[customLLines.size()];
            for (int e = 0; e < customLLines.size(); e++) {
                customRanked[e] = new Letter(customLLines.get(e).toLowerCase().charAt(0), e + 1);
            }
            return customRanked;
        }

        ArrayList<Letter> Lettersv2 = new ArrayList<>();

        for (String Word : Words) {
            char[] WWW = Word.toCharArray();
            for (char letter : WWW) {
                if (!Character.isLetter(letter)) { continue; }
                int ind = indexOfChar(Lettersv2, letter);
                if (ind == -1) {Lettersv2.add(new Letter(Character.toLowerCase(letter), 1));}
                else {Lettersv2.get(ind).incPop();}
            }
        }

        //FOR BINARY PART
        if (ch.getSelectedIndex() == 2) { //BINARY MODE
            long median = Math.round(medianOf(Lettersv2));
            for (Letter el : Lettersv2){
                el.setPop((long) Math.pow(el.getPop() - median, 2));
            }
        }

        Letter[] freq = Lettersv2.toArray(new Letter[0]);
        Arrays.sort(freq, Letter::letterComparator);
        return freq;
    }

    private boolean duplicate(String Word, char[] charsY, String charsR, int wordLength) //Better algoritm (opcional, advanced)
    {
        int[] probna = new int[wordLength];
        Arrays.fill(probna, 1); //makes an array of 1's
        char[] charsYkopija = charsY;
        for (int l = 0; l < wordLength && charsYkopija.length != 0; l++)
        {
            if (!textBoxesInPlace.get(l).getText().trim().isEmpty()) { probna[l] = 0; } //Koja mesta za slovo su puna
            if (probna[l] == 1 && containsChar(charsYkopija, Character.toLowerCase(Word.charAt(l))))
            {
                int numIndex = indexOfChar(charsYkopija, Character.toLowerCase(Word.charAt(l)));
                char[] pr = new char[charsYkopija.length - 1];
                System.arraycopy(charsYkopija, 0, pr, 0, numIndex);
                System.arraycopy(charsYkopija, numIndex + 1, pr, numIndex, charsYkopija.length - 1 - numIndex); //Fenci nacin da izbacim element
                charsYkopija = pr;
                probna[l] = 0;
            }
        }
        for (int A = 0; A < wordLength; A++)
        {
            for (int B = A + 1; B < wordLength; B++)
            {
                if ((probna[A] != probna[B] || probna[A] == 1 && probna[B] == 1) && Character.toLowerCase(Word.charAt(A)) == Character.toLowerCase(Word.charAt(B)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean Checkerv2(String Word, int wordLength) // second checkbox activates this checker :) (FINAL)
    {
        char[] nonochars = bannedTextbox.getText().toLowerCase().toCharArray();
        for (int i = 0; i < wordLength; i++)
        {
            if (containsChar(nonochars, Character.toLowerCase(Word.charAt(i)))) { return false; }
        }
        return true;
    }

    private int WordLength()//LENGTH OF THE WORD (IMPORTANT) (FINAL)
    {
        if ((Integer) wordLengthUpDpwn.getValue() == 11) { return textBoxesInPlace.get(10).getText().length(); }
        else { return (Integer) wordLengthUpDpwn.getValue(); }
    }

    private void checkboxMiniReset() { //TextBox soft reset (FINAL)
        reset();
        solutionLabel.setVisible(false);
        restartButt.setVisible(false);
        nextButt.setVisible(false);
        revalidate();
    }

    public static boolean containsChar(char[] arr, char c) { // (FINAL)
        for (char x : arr) { if (x == c) return true; }
        return false;
    }

    public static int indexOfChar(char[] arr, char c) { // (FINAL)
        for (int i = 0; i < arr.length; i++) { if (arr[i] == c) return i; }
        return -1;
    }

    public static long indexOfNull(Object[] arr) { // (FINAL)
        for (int i = 0; i < arr.length; i++) { if (arr[i] == null) return i; }
        return -1;   // matches Array.IndexOf's real contract — see warning below
    }

    public static int indexOfChar(ArrayList<Letter> arr, char c){
        for (int i = 0; i < arr.size(); i++){
            if (arr.get(i).getName() == c){ return i; }
        }
        return -1;
    }

    public static double medianOf(ArrayList<Letter> arr){
        if (arr.size() == 1) {return arr.getFirst().getPop(); }
        int middleIndex = arr.size() / 2;
        if (arr.size() % 2 == 0) {return (double) (arr.get(middleIndex).getPop() + arr.get(middleIndex - 1).getPop()) / 2;}
        else {return arr.get(middleIndex).getPop(); }
    }

    //NEXT DUGME
    private void nextButt() // (FINAL)
    {
        Add();
        try
        {
            String[] U = Files.readAllLines(Path.of(Utilities.GetApplicationDataFile(Constants.PTempFileName))).toArray(new String[0]);
            if (displayRank.getState()) { solutionLabel.setText("WORD FOUND: " + U[Integer.parseInt(tempLabel.getText())]); }
            else { solutionLabel.setText("WORD FOUND: " + (U[Integer.parseInt(tempLabel.getText())].split(" \\(")[0])); }
        }
        catch (Exception e) { return; }
        solutionLabel.setVisible(true);
        nextButt.setVisible(true);
        restartButt.setVisible(true);
        revalidate();
        repaint();
    }

    private void restartButt() //restart (FINAL)
    {
        reset();
        solutionLabel.setVisible(false);
        nextButt.setVisible(false);
        searchButt();
        revalidate();
        repaint();
    }

    private void Add()//Counter add (FINAL)
    {
        tempLabel.setText(Integer.toString(Integer.parseInt(tempLabel.getText()) + 1));
    }

    private void textChangedGUI() //softReset after the letter input (FINAL)
    {
        reset();
        solutionLabel.setVisible(false);
        nextButt.setVisible(false);
        restartButt.setVisible(false);
    }

    private void toUpperFields(){ //Style change (Applies only after search) (FINAL)
        for (JTextField txt : textBoxesInPlace){
            if (txt.isVisible()) {txt.setText(txt.getText().toUpperCase());}
            else { break; }
        }
    }

    //Import dictionary
    private void importDIC() {
        try {
            String pathStr = FajlProzor.nadjiPath(this, "Import dictionary", "C:\\Users\\User\\Desktop\\data\\test.json");
            if (pathStr != null) {
                CsvJsonFajl.citajPisiCSVJSON(Paths.get(pathStr), true);
            }
        } catch (NoSuchFileException e) {new GreskaProzor(this, "Said file doesn't exist!", true); }
        catch (Exception e) {new GreskaProzor(this, "Format incorrect! Required format: <word><linebreak><word><linebreak>..." , true); }
        resetButt();
    }

    //Import custom frequency
    private void importFreq() {
        try {
            String pathStr = FajlProzor.nadjiPath(this, "Import alphabet", "C:\\Users\\Korisnik\\User\\data\\test.json");
            if (pathStr != null) {CsvJsonFajl.citajPisiCSVJSON(Paths.get(pathStr), false);}
        } catch (NoSuchFileException e) {new GreskaProzor(this, "Said file doesn't exist!", true); }
        catch (Exception e) {new GreskaProzor(this, "Format incorrect! Required format: <letter><linebreak><letter><linebreak>...", true); }
        resetButt();
    }
    //Reset custom frequency
    private void resetFreq() {
        YesNoDialog izbor = new YesNoDialog(this, "Are you sure?");
        if (izbor.dane == YesNoDialog.IZBOR.YES) {
            try (BufferedWriter _ = new BufferedWriter(new FileWriter(Utilities.GetApplicationDataFile(Constants.LetterRankFileName), StandardCharsets.UTF_8))) {} //Rewrites everything
            catch (IOException e) {new GreskaProzor(this, e.toString(), true); return;}
        }
        resetButt();
    }
}
