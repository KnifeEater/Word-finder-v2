package mics;


import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvJsonFajl {

    private static final short TOLERANCE = 50;
    public static void citajPisiCSVJSON(Path putanja, boolean dict) throws Exception {
        uvoz(putanja, dict);
    }

    private static void uvoz(Path putanja, boolean dict) throws Exception {
        List<String> linije = Files.readAllLines(putanja, StandardCharsets.UTF_8);
        String ceoText = Files.readString(putanja, StandardCharsets.UTF_8);

        if (linije.isEmpty()) { throw new Exception(); }
        if (dict && !checkerDict(linije)) {throw new Exception(); }
        if (!dict && !checkerAlphabet(ceoText)) {throw new Exception();}

        try (BufferedWriter izlaz = new BufferedWriter(new FileWriter(Utilities.GetApplicationDataFile(dict ? Constants.WordsAlphaFileName : Constants.LetterRankFileName), StandardCharsets.UTF_8))) {
            //Rewrites dict/alphabet
            for (String line : linije) {
                izlaz.write(line);
                izlaz.newLine();
            }
        }
    }

    private static boolean checkerDict(List<String> lines) { //True = passed the check
        short c = 0;
        for (String line : lines) {
            if (!line.matches("\\p{L}+")) {
                //System.out.println(line);
                c++;
                if (c > TOLERANCE) {return false;}
            }
        }
        return true;
        //return text.matches("(\\p{L}+\\R)*\\p{L}+\\R?"); //DEBUG
    }
    private static boolean checkerAlphabet(String text) { //True = passed the check
        return text.matches("([A-Za-z](: \\d+ \\(\\d+\\))?\\R)*[A-Za-z](: \\d+ \\(\\d+\\))?\\R?");
    }
}
