import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class FrequencyCounter {
    private int[] frequencies;

    public FrequencyCounter() {
        frequencies = new int[128];  
    }

    public void fromStdin() throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      int c;
      while ((c = reader.read()) != -1) {
          if (c == '\r') continue;       
          if (c >= 0 && c < frequencies.length) {
              frequencies[c]++;
          }
      }
      reader.close();
  }

  public void printFrequencies() {
    System.out.print("eom 1");
    for (int i = 0; i < frequencies.length; i++) {
        if (frequencies[i] == 0) continue;
        if (i == '\r') continue;      
        
        System.out.print(" ");
        String symbol;
        if (i == '\n')       symbol = "newline";
        else if (i == 32)    symbol = "space";
        else                 symbol = Character.toString((char)i);
        System.out.print(symbol);
        System.out.print(" ");
        System.out.print(frequencies[i]);
    }
    System.out.println();
}

    // Utility methods
    public int getCount(char c) {
        return frequencies[c];
    }

    public int getSpaceCount() {
        return frequencies[32];
    }

    public int[] getAllFrequencies() {
        return frequencies.clone();
    }

    public static void main(String[] args) throws IOException {
        FrequencyCounter parser = new FrequencyCounter();
        parser.fromStdin();
        parser.printFrequencies();
    }
}
