package model;



public class Letter {
    private char name;
    private long popularity;
    public Letter()
    {
        name = '\0';
        popularity = 0;
    }
    public Letter(char NAME,long POPULARITY)
    {
        this.name = NAME;
        this.popularity = POPULARITY;
    }
    public char getName() { return name; }
    public void setName(char NAME) {this.name = NAME;}
    public long getPop() { return popularity; }
    public void setPop(long POP) {this.popularity = POP;}
    public void incPop() { this.popularity += 1; }
    @Override
    public String toString() { return Character.toString(name).toUpperCase() + ": " + popularity; }
    //public int addName (Letter left, Letter right) { return (int)left.name + (int)right.name; }
    //public int addName (Letter left, int right) { return (int)left.name + right; }
    public static int letterComparator(Letter A, Letter B)
    { //AKO je A > B -> -1, A < B -> 1
        return Long.compare(B.popularity, A.popularity);
    }
}
