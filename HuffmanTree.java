import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;

public class HuffmanTree
{
    public int freq;
    private Optional<Character> datum;
    public HuffmanTree left;
    public HuffmanTree right;
    public HashMap<Character, String> encodingMap; // each character and the binary number (as a string) that represents it

    
    public HuffmanTree(int freq, char datum) // leaf node of Huffman tree
    {
        this.freq = freq;
        this.datum = Optional.of(datum);
        left = null;
        right = null;
        encodingMap = null;
    }
    public HuffmanTree(int freq, HuffmanTree left, HuffmanTree right) // has at least 2 children
    {
        this.freq = freq;
        datum = null;
        this.left = left;
        this.right = right;
        
        this.encodingMap = new HashMap<Character, String>();
        
        if (left.encodingMap != null)
        {
            for (Character x: left.encodingMap.keySet())
            {
                this.encodingMap.put(x, "0" + left.encodingMap.get(x)); // adds a 0 on the front of every representation of a key from the left branch
            }
        }
        if (right.encodingMap != null)
        {
            for (Character x: right.encodingMap.keySet())
            {
                this.encodingMap.put(x, "1" + right.encodingMap.get(x)); // adds a 1 on the front of every representation of a key from the right branch
            }
        }

        if (left.datum != null)
        {
            this.encodingMap.put(left.getDatum(), "0");
        }
         if (right.datum != null)
        {
            this.encodingMap.put(right.getDatum(), "1");
        }



    }
    public void setDatum(char newDatum)
    {
        this.datum = Optional.of(newDatum);
    }  
    public char getDatum()
    {
        return datum.get();
    }
    public boolean hasChildren()
    {
        return (this.right != null) || (this.left != null);
    }
    public boolean isLeaf() 
    {
        return !(hasChildren());
    }

    public int size() //recursive function that calculates the amount of attached nodes
    {
        int out = 1;
        if (right != null)
        {
            out += right.size();
        }
        if (left != null)
        {
            out += left.size();
        }
        return out;
    }
    public int depth() // recursive function that finds the length of the longest branch
    {
        int out = 1;
        int leftD = 0;
        int rightD = 0;
        if (left != null)
        {
            leftD = left.depth();
        }
        if (right != null)
        {  
            rightD = right.depth();
        }
        return out + Math.max(leftD, rightD);
    }
  
   
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (datum != null)
        {
            sb.append(Character.toString(getDatum()));
        }
        else {
            sb.append("multiple");
        }
        sb.append(" freq " + freq);
        
        if (left != null)
        {
            sb.append("[" + left.toString() + "]");
        }

        if (right != null)
        {
            sb.append(" [" + right.toString() + "]");
        }

        return sb.toString();
    }
   
}