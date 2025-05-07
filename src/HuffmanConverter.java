import java.io.IOException;

public class HuffmanConverter {
  // Usage from the command line:
  //   cat just_to_say.txt | java HuffmanConverter encode spec.txt
  //   cat bits.txt      | java HuffmanConverter decode spec.txt
  //   cat file.txt      | java HuffmanConverter analyze spec.txt
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.err.println("Usage: java HuffmanConverter <encode|decode|analyze> <specFile>");
      System.exit(1);
    }
    String mode = args[0].toLowerCase();
    String treeFile = args[1];

    // 1) Read the saved tree-spec string, rebuild the HuffmanTree
    String treeStr = StdinToString.readfile(treeFile);
    HuffmanTree tree = HuffmanTree.loadTree(treeStr);

    // 2) Read the data to be processed (raw text for encode/analyze, bit-string for decode)
    String input = StdinToString.read();

    // 3) Dispatch modes
    switch (mode) {
      case "encode":
        doEncode(tree, input);
        break;

      case "decode":
        doDecode(tree, input);
        break;

      case "analyze":
        doAnalyze(tree, input);
        break;

      default:
        System.err.println("Unknown mode: " + mode);
        System.exit(2);
    }
  }

// --- ENCODE --------------------------------------------------------
private static void doEncode(HuffmanTree tree, String text) {
    String[] codes = tree.getCodes();
    StringBuilder out = new StringBuilder();
    for (char c : text.toCharArray()) {
        if (c == '\n' || c == '\r') {
            out.append(codes[32]);   
        } else {
            int idx = (int) c;
            String code = codes[idx];
            if (code == null) {
                System.err.println("No code for character: [" + c + "]");
                System.exit(3);
            }
            out.append(code);
        }
    }  
    
    out.append(codes[128]);
    System.out.print(out.toString());
}

  // --- DECODE --------------------------------------------------------
  private static void doDecode(HuffmanTree tree, String bits) {
    StringBuilder out = new StringBuilder();

    for (char b : bits.toCharArray()) {
      if (b != '0' && b != '1') continue;  // skip whitespace/newlines
      String sym = tree.advanceCurrent(b);
      if (sym != null) {
        // stop at EOM
        if (sym.equals("\\e")) break;
        out.append(sym);
      }
    }

    System.out.print(out.toString());
  }

  // --- ANALYZE -------------------------------------------------------
  private static void doAnalyze(HuffmanTree tree, String text) {
    String[] codes = tree.getCodes();
    int charCount = 0;
    long bitCount  = 0L;

    // count bits for each character
    for (char c : text.toCharArray()) {
      int idx = (int)c;
      if (codes[idx] == null) {
        System.err.println("No code for character: [" + c + "]");
        System.exit(4);
      }
      charCount++;
      bitCount += codes[idx].length();
    }
    // plus EOM
    bitCount += codes[128].length();

    double avg = (charCount > 0) ? ((double)bitCount / charCount) : 0.0;

    System.out.println("Encoded Bits: " + bitCount);
    System.out.println("Original Character Count: " + charCount);
    System.out.printf("Average Bits Per Character: %.2f\n", avg);
  }
}
