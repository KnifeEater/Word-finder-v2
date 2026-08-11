package model;

public class Word {
    private String name;
    private long popularity;
    public Word()
    {
        name = "";
        popularity = 0;
    }
    public Word(String NAME)
    {
        this.name = NAME;
        popularity = 0;
    }
    public String getName() { return name; }
    public void setName(String NAME) {this.name = NAME;}
    public long getPop() { return popularity; }
    public void setPop(long POP) {this.popularity = POP;}
    @Override
    public String toString() { return name.toString().toUpperCase() + ": " + popularity; }
    //public long addPopularity(Word right) { return this.popularity + right.popularity; }
    //public long addPopularity(long right) { return this.popularity + right; }
    public static int wordComparator(Word A, Word B) { // manji broj = bolji, isto kao u C#
        return Long.compare(A.popularity, B.popularity);
    }
    public long rank(Letter[] Letters)
    {
        if (popularity != 0) { return popularity; }
        int counter = 0;
        for (int A = 0; A < name.length(); A++)
        {
            for (int L = 0; L < Letters.length; L++)
            {
                if (Letters[L].getName() == name.charAt(A)) { counter += (L + 1); break; }
            }
        }
        popularity = counter;
        return counter;
    }
}
