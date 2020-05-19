import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;

public class Huffman
{
    public static HashMap<Character, Integer> countFrequencies(String message) // ENCODE - reads the message and counts every character in it
    {
        HashMap<Character, Integer> out = new HashMap<Character, Integer>();
        for (char x: message.toCharArray())
        {
            if (out.containsKey(x))
            {
                out.put(x, out.get(x)+1);
            } else {
                out.put(x, 1);
            }
        }
        return out;
    }

    private static void sortedAdd(ArrayList<HuffmanTree> al, HuffmanTree item) // useful function for building up the tree - largest frequency first
    {
        boolean notDone = true;
        int i = 0;
        while ((notDone) && (i < al.size()))
        {
            if (item.freq > al.get(i).freq)
            {
                al.add(i, item); // adds the item at position i
                notDone = false;
            } else {
                i++;
            }
        }
        if (notDone) // if it makes it to the end
        {
            al.add(item);
        }
    }

    public static ArrayList<HuffmanTree> huffmanNodeList(String message) // used for encoding 
    {
        ArrayList<HuffmanTree> nodes = new ArrayList<HuffmanTree>();
        for (Character x: countFrequencies(message).keySet())
        {
            sortedAdd(nodes, new HuffmanTree((countFrequencies(message).get(x)), x)); // largest nodes at the front
        }
        return nodes; 

    }
    public static HuffmanTree buildHuffmanTree(String message) // generates a huffman tree for a message
    {
       ArrayList<HuffmanTree> nodes = huffmanNodeList(message);

        while (nodes.size() > 1) // continually combines nodes based on size to make a complete tree
        {
            HuffmanTree t1 = nodes.get(nodes.size() - 2);
            HuffmanTree t2 = nodes.get(nodes.size() - 1);
            HuffmanTree newTree = new HuffmanTree(t1.freq + t2.freq, t1, t2);
            nodes.remove(t2);
            nodes.remove(t1);
            sortedAdd(nodes, newTree);
        }
        //System.out.println(nodes);
        return nodes.get(0);
    }

    public static HuffmanTree buildHuffmanTree(ArrayList<HuffmanTree> nodes) // decoding
    {
        while (nodes.size() > 1) // continually combines nodes based on size to make a complete tree
        {
            HuffmanTree t1 = nodes.get(nodes.size() - 2);
            HuffmanTree t2 = nodes.get(nodes.size() - 1);
            HuffmanTree newTree = new HuffmanTree(t1.freq + t2.freq, t1, t2);
            nodes.remove(t2);
            nodes.remove(t1);
            sortedAdd(nodes, newTree);
        }
        //System.out.println(nodes);
        return nodes.get(0);
    }


    public static String getBinaryRep(String message, HuffmanTree tree) // encoded message in binary form
    {
        String binaryRep = "";
        for (char x: message.toCharArray())
        {
            binaryRep += tree.encodingMap.get(x);
        }
        return binaryRep;
    }
    
    public static String encodedString(String message, HuffmanTree tree) // turns the binary representation into ASCII characters
    {
        String out = "";
        String binaryRep = getBinaryRep(message, tree);
        while (binaryRep.length() % 8 != 0)
        {
            binaryRep = "0" + binaryRep;
        }
        //System.out.println(binaryRep.length());
        for (int i = 0; i < binaryRep.length(); i += 8)
        {
            out += (char) Integer.parseInt(binaryRep.substring(i, i + 8), 2);
        }
        return out;
    }

    private static String decimalToASCII(int i, int len) // converts a number into len ascii characetrs
    {
        String bin = Integer.toString(i, 2);
        
        while (bin.length() < len*8) // adds zeroes on the front if needed
        {
            bin = "0" + bin;
        }
        if (bin.length() > len*8) // trims the front if needed - usually if len is faulty
        {
            bin = bin.substring(bin.length() - len*8, bin.length());
        }

        //System.out.println(bin);
        String out = "";
        for (int j = 0; j < bin.length(); j += 8)
        {
            out += (char) Integer.parseInt(bin.substring(j, j+8), 2);
        }
        return out;
    }

    private static String ASCIItoBinary(String message)
    {
        String out = "";
        for (char x: message.toCharArray())
        {
            String charRep = Integer.toString(((int) x), 2);
            while (charRep.length() < 8)
            {
                charRep = "0" + charRep;
            }
            out += charRep;
        }
        /*while (out.length() < 8*message.length())
        {
            out = "0" + out;
        }*/
        return out;
    }

    public static void encode(String inputFile, String outputFile)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String contents = "";
            String line = "";
            while ((line = br.readLine()) != null)
            {
                contents += (line + "\n");
            }
            contents = contents.substring(0, contents.length()-1); // removes the extra \n at the end
           

            String out = "";  /*
            
            First 2 characters - number of unique characters in the data  (16 bits stored in ASCII characters)
            3rd character - binary rep length mod 8 - related to the number of zeroes at the front of the binary data representation
            Next (based on the first 2 characters) characters - sorted huffmanNodeList of the contents
            For each character: One character as the character itself, 3 characters (24 ASCII bits) of its frequency
            Rest of characters: encodedString
            */

            out += decimalToASCII(huffmanNodeList(contents).size(), 2);
            out += Integer.toString((getBinaryRep(contents, buildHuffmanTree(contents)).length() % 8));
            for (HuffmanTree t: huffmanNodeList(contents))
            {
                out += t.getDatum();
                out += decimalToASCII(t.freq, 3);
            }
            out += encodedString(contents, buildHuffmanTree(contents));            

            System.out.println(huffmanNodeList(contents));
            System.out.println(countFrequencies(contents));
            String shortBinRep = getBinaryRep(contents, buildHuffmanTree(contents));
            System.out.println(shortBinRep.substring(4, shortBinRep.length()));

            FileWriter fr = new FileWriter(outputFile);
            fr.write(out);
            fr.flush();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private static String treeDecoding(HuffmanTree tree, String binaryDump) // a 0 goes down the left branch, a 1 down the right branch
    {
        String out = "";
        int i = 0;
        System.out.println(tree);
        System.out.println(tree.encodingMap);
        System.out.println(binaryDump.length());
        while (i < binaryDump.length() - 8)
        {
            HuffmanTree currentNode = tree;
            while (currentNode.hasChildren())
            {
                if (binaryDump.charAt(i) == '0')
                {
                    currentNode = currentNode.left;
                } else
                {
                    currentNode = currentNode.right;
                }
                i++;
            }
            //i++;
            out += currentNode.getDatum();
        }
        return out;

    }

    public static void decode(String inputFile, String outputFile)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String contents = "";
            String line = "";
            while ((line = br.readLine()) != null)
            {
                contents += (line + "\n");
            }
            contents = contents.substring(0, contents.length()-1); // removes the extra \n at the end

            int uniqueChars = Integer.parseInt(ASCIItoBinary(contents.substring(0, 2)), 2);
            int binRepMod8 = Integer.parseInt(contents.substring(2, 3));

            ArrayList<HuffmanTree> constructedNodes = new ArrayList<HuffmanTree>();
            for (int i = 0; i < uniqueChars; i++)
            {
                constructedNodes.add(new HuffmanTree(Integer.parseInt(ASCIItoBinary(contents.substring(4 + 4*i, 7 + 4*i)), 2), contents.charAt(3 + 4*i)));
            }
            System.out.println(constructedNodes);

            String encodedMessage = contents.substring(7 + 4*(uniqueChars - 1), contents.length());
            encodedMessage = ASCIItoBinary(encodedMessage);

            if (binRepMod8 > 0)
            {
                encodedMessage = encodedMessage.substring(binRepMod8, encodedMessage.length());
            }

            System.out.println(encodedMessage.substring(4, encodedMessage.length()));

            FileWriter fr = new FileWriter(outputFile);
            fr.write(treeDecoding(buildHuffmanTree(constructedNodes), encodedMessage));
            fr.flush();


        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) // "-e" for encode or "-d" for decode, first file, second file
    {
        if ((args.length != 3) || !(args[0].equals("-e") || args[0].equals("-d")) || !(args[1].contains(".")) || !(args[2].contains(".")))
        {
            System.err.println("First argument must be -e for encode or -d for decode\nSecond argument is the file you wish to read from\nThird argument is the file you wish to write to");
            
            System.exit(1);
        }
        if (args[0].equals("-e"))
        {
            encode(args[1], args[2]);
        }
        if (args[0].equals("-d"))
        {
            decode(args[1], args[2]);
        }

        //System.out.println(countFrequencies(msg));
        //System.out.println(buildHuffmanTree(msg));
        //System.out.println(buildHuffmanTree(msg).encodingMap);
    }
    
}