public class LinkStrand implements IDnaStrand {

    private Node myFirst, myLast;
    private long mySize;
    private int myAppends;
    private int myIndex;
    private Node myCurrent;
    private int myLocalIndex;

    public LinkStrand() {
        this("");
    }

    public LinkStrand(String s) {
        initialize(s);
    }

    public void initialize(String source) {
        Node n = new Node(source, null);
        myFirst = n;
        myLast = n;
        myAppends = 0;
        myIndex = 0;
        myCurrent = myFirst;
        myLocalIndex = 0;
        mySize = source.length();
    }

    public IDnaStrand getInstance(String source) {
        return new LinkStrand(source);
    }

    public long size() {
        return mySize;
    }

    public IDnaStrand append(String dna) {
        Node newDNA = new Node(dna, null);
        myLast.next = newDNA;
        myLast = myLast.next;
        mySize += dna.length();
        myAppends++;
        return this;
    }

    public int getAppendCount() {
        return myAppends;
    }

    public String toString() {
        StringBuilder s = new StringBuilder("");
        Node myCurrent = myFirst;
        while (myCurrent != null) {
            s.append(myCurrent.info);
            myCurrent = myCurrent.next;
        }
        return s.toString();
    }

    @Override
    public IDnaStrand reverse() {
        LinkStrand reversedStrand = new LinkStrand();
        Node current = myFirst;

        while (current != null) {
            StringBuilder s = new StringBuilder(current.info);
            String reversedString = s.reverse().toString();
            Node rev = new Node(reversedString); // new node of reversed string
            if (current == myFirst) {
                reversedStrand.myLast = rev;
            }
            reversedStrand.mySize += rev.info.length(); // update size
            rev.next = reversedStrand.myFirst;
            reversedStrand.myFirst = rev;
            current = current.next;

        }
        return reversedStrand;
    }

    public char charAt(int index) {
        if (index >= this.mySize || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        // if character is smaller than current index, reset the variables
        if (myIndex > index) {
            myIndex = 0;
            myLocalIndex = 0;
            myCurrent = myFirst;
        }
        int characterCount = myIndex;
        while (myCurrent != null) {
            if (characterCount + myCurrent.info.length()> index) {
                myLocalIndex = index - characterCount;
                myIndex = index;
                return myCurrent.info.charAt(myLocalIndex);
            }
            characterCount += myCurrent.info.length();
            myCurrent = myCurrent.next;
        }
        return 0;
    }

    private class Node {
        private String info;
        private Node next;

        private Node(String i) {
            info = i;
        }

        private Node(String i, Node n) {
            info = i;
            next = n;
        }
    }
}
