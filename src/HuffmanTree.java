import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HuffmanTree supports:
 *  - Part A: building from frequencies, printing legend or tree-spec
 *  - Part B: loading from a spec string, exporting a code array, and advancing bit by bit
 */
public class HuffmanTree {
    private HuffmanNode root;
    private HuffmanNode current;  // for decoding

    // --- Node definition ---
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        final public String symbols;
        final public Double frequency;
        final public HuffmanNode left, right;

        // Leaf-node constructor
        public HuffmanNode(String symbol, Double frequency) {
            this.symbols = symbol;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        // Internal-node constructor
        public HuffmanNode(HuffmanNode left, HuffmanNode right) {
            this.symbols = left.symbols + right.symbols;
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(HuffmanNode o) {
            int cmp = this.frequency.compareTo(o.frequency);
            if (cmp != 0) return cmp;
            return this.symbols.compareTo(o.symbols);
        }
    }

    // --- Part A: Build from frequency string ---
    public static BinaryHeap<HuffmanNode> freqToHeap(String frequencyStr) {
        String[] tokens = frequencyStr.trim().split("\\s+");
        HuffmanNode[] nodes = new HuffmanNode[tokens.length / 2];
        int idx = 0;
        for (int i = 0; i < tokens.length; i += 2) {
            String sym = tokens[i];
            Double freq = Double.valueOf(tokens[i + 1]);
            nodes[idx++] = new HuffmanNode(sym, freq);
        }
        return new BinaryHeap<>(nodes);
    }

    public static HuffmanTree createFromHeap(BinaryHeap<HuffmanNode> heap) {
        while (heap.getSize() > 1) {
            HuffmanNode a = heap.extractMin();
            HuffmanNode b = heap.extractMin();
            heap.insert(new HuffmanNode(a, b));
        }
        return new HuffmanTree(heap.extractMin());
    }

    // --- Part B: Load from a tree-spec string ---
    public static HuffmanTree loadTree(String spec) {
        // Remove BOM if present
        if (spec.startsWith("\uFEFF")) {
            spec = spec.substring(1);
        }
        ArrayStack<HuffmanNode> stack = new ArrayStack<>();
        for (int i = 0; i < spec.length(); i++) {
            char c = spec.charAt(i);
            if (c == '\\') {
                // escape sequence: \e, \|, \\, or \n
                if (i + 1 >= spec.length()) break;
                char nxt = spec.charAt(i + 1);
                String sym;
                if (nxt == 'e')        sym = "eom";
                else if (nxt == '|')   sym = "|";
                else if (nxt == '\\') sym = "\\";
                else if (nxt == 'n')   sym = "newline";
                else                   sym = Character.toString(nxt);
                stack.push(new HuffmanNode(sym, 0.0));
                i++;  // skip the escaped char
            } else if (c == '|') {
                // combine top two nodes
                HuffmanNode right = stack.pop();
                HuffmanNode left  = stack.pop();
                stack.push(new HuffmanNode(left, right));
            } else if (c == '\n' || c == '\r' || c == '\t') {
                // ignore whitespace
            } else {
                // literal leaf (including space)
                String sym = (c == ' ') ? "space" : Character.toString(c);
                stack.push(new HuffmanNode(sym, 0.0));
            }
        }
        // collapse any remaining nodes
        while (stack.size() > 1) {
            HuffmanNode right = stack.pop();
            HuffmanNode left  = stack.pop();
            stack.push(new HuffmanNode(left, right));
        }
        return new HuffmanTree(stack.pop());
    }

    // --- Constructor ---
    public HuffmanTree(HuffmanNode root) {
        this.root = root;
        this.current = root;
    }

    // --- Part B: Export legend as a 129-entry array ---
    //   indices 0–127 → ASCII, index 128 → EOM
    public String[] getCodes() {
        Map<String, String> map = new HashMap<>();
        buildLegend(root, "", map);
        String[] codes = new String[129];
        for (Map.Entry<String, String> e : map.entrySet()) {
            String sym  = e.getKey();
            String code = e.getValue();
            int idx;
            if      (sym.equals("eom"))      idx = 128;
            else if (sym.equals("newline"))  idx = (int)'\n';
            else if (sym.equals("space"))    idx = 32;
            else if (sym.equals("|"))        idx = (int)'|';
            else if (sym.equals("\\"))     idx = (int)'\\';
            else                                idx = sym.charAt(0);
            if (idx < 0 || idx >= codes.length) continue;  // skip invalid
            codes[idx] = code;
        }
        return codes;
    }

    // --- Helper to build legend map (used by both printLegend & getCodes) ---
    private void buildLegend(HuffmanNode node, String bits, Map<String, String> map) {
        if (node.left == null && node.right == null) {
            map.put(node.symbols, bits);
        } else {
            buildLegend(node.left,  bits + "1", map);
            buildLegend(node.right, bits + "0", map);
        }
    }

    // --- Part B: Advance one bit for decoding; returns a symbol when a leaf is hit ---
    public String advanceCurrent(char bit) {
        current = (bit == '1') ? current.left : current.right;
        if (current.left == null && current.right == null) {
            String out = convertSymbolToChar(current.symbols);
            current = root;
            return out;
        }
        return null;
    }

    // --- Part A: Print a tab-separated legend (symbol → bit-string) ---
    public void printLegend() {
        Map<String, String> map = new HashMap<>();
        buildLegend(root, "", map);
        List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getValue));
        for (Map.Entry<String, String> e : entries) {
            System.out.println(convertSymbolToChar(e.getKey()) + "\t" + e.getValue());
        }
    }

    // --- Part A: Print the tree-spec (so you can round-trip with loadTree) ---
    public void printTreeSpec() {
        traverseSpec(root, true);
        System.out.println();
    }
    private void traverseSpec(HuffmanNode node, boolean isLeftmost) {
        if (node.left == null && node.right == null) {
            System.out.print(convertSymbolToChar(node.symbols));
        } else {
            traverseSpec(node.right, false);
            traverseSpec(node.left,  isLeftmost);
            if (!isLeftmost) System.out.print("|");
        }
    }

    // --- Convert internal symbol name to the literal output used in specs/legends ---
    public static String convertSymbolToChar(String symbol) {
        switch (symbol) {
            case "newline": return "\\n";
            case "space":   return " ";
            case "eom":     return "\\e";
            case "|":       return "\\|";
            case "\\":     return "\\\\";
            default:         return symbol;
        }
    }

    // --- Part A main: either "legend" or "spec" from stdin frequencies ---
    public static void main(String[] args) throws IOException {
        String mode = (args.length == 0) ? "spec" : args[0].toLowerCase();
        String freq = StdinToString.read();
        BinaryHeap<HuffmanNode> heap = freqToHeap(freq);
        HuffmanTree tree = createFromHeap(heap);
        if (mode.equals("legend")) tree.printLegend();
        else                       tree.printTreeSpec();
    }
}
