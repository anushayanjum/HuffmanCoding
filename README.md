# Assignment 4b: Huffman Coding

**Course**: Data Structures (CSCI-UA 102, Section 7)
//
**Instructor**: Max Sklar
//
**Author**: Anushay Anjum

---

This project implements Huffman coding in Java for compressing and decompressing text files with an explicit End-Of-Message (EOM) symbol. It includes:

* **FrequencyCounter.java**: scans stdin, counts character frequencies, outputs a frequency list.
* **HuffmanTree.java**: builds a Huffman tree from frequencies, prints a tree-spec or legend, and supports encoding/decoding bit-by-bit.
* **HuffmanConverter.java**: uses a saved tree-spec to encode/decode/analyze text via Huffman coding.
* **BitConverter.java**: converts between bit-strings and binary/hex/base64 formats.

---

## Table of Contents

1. [Runbook](#runbook)
2. [Time Spent](#time-spent)
3. [Notes](#notes)
4. [Resources & Acknowledgements](#resources--acknowledgements)

---

## Runbook

1. **Clean** previous compilations (optional):

   ```bash
   rm -f *.class
   ```
2. **Compile** all Java classes:

   ```bash
   javac FrequencyCounter.java HuffmanTree.java HuffmanConverter.java BitConverter.java
   ```
3. **Generate tree-spec** from your text ("just\_to\_say.txt"):

   ```bash
   cat just_to_say.txt | java FrequencyCounter > freq.txt
   cat freq.txt         | java HuffmanTree     > spec.txt
   ```
4. **Encode** your text → bit-string:

   ```bash
   cat just_to_say.txt | java HuffmanConverter encode spec.txt > bits.txt
   ```
5. **Decode** back to text:

   ```bash
   cat bits.txt | java HuffmanConverter decode spec.txt > recovered.txt
   ```
6. **Verify** lossless round-trip:

   ```bash
   diff recovered.txt just_to_say.txt
   ```
7. **Analyze** compression statistics:

   ```bash
   cat just_to_say.txt | java HuffmanConverter analyze spec.txt
   ```
8. **Full pipeline** with BitConverter (bonus):

   ```bash
   cat just_to_say.txt \
    | java HuffmanConverter encode spec.txt \
    | java BitConverter encode binary \
    | java BitConverter decode binary \
    | java HuffmanConverter decode spec.txt \
    > roundtrip.txt

   diff roundtrip.txt just_to_say.txt
   ```

---

## Time Spent

Approximately **6 hours**:

* **2 hours** setting up classes and basic functionality
* **2 hours** debugging encoding/decoding edge cases (newline handling, encoding issues)
* **1 hour** writing and testing the runbook and bonus pipeline
* **1 hour** documenting and polishing this README

---

## Notes 

* **Encoding mismatches** on Windows: needed `javac -encoding UTF-8` or removal of BOM/fancy hyphens.
* **Newline handling**: originally lost line breaks—fixed by mapping `\n` to a `newline` symbol and treating newlines as spaces on encode/decode.
* **ArrayIndexOutOfBounds**: found stray symbols (e.g. BOM or control chars) mapping to invalid indices; added debug guard and symbol filtering.
* **Spec formatting**: tree-spec uses explicit `|`, `\\`, `\\e`, and now supports `\\n` for newlines.

---

## Resources & Acknowledgements

* **Instructor*: Max Sklar for project specs and guidance.
* **Java SE Documentation**: for `java.io` and collection APIs.
* **StackOverflow**: for Java encoding tips and diff tools.
* **ChatGPT**: iterative debugging help (comment replacement, newline mapping), refine run sequence commands and debug code; all suggestions vetted and hand-tuned.
