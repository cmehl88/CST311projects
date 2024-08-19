/**
 *  Carson Mehl
 *  3/11/2024
 *  CST311
 *
 *  Code Info: This code project simulates how a simple DNS lookup process happens
 *  by using txt files that the code traverses iteratively through the text
 *  files starting with 1-0-0-0.txt which acts as the "Root Domain" then
 *  depending what the extension is on the domain name travels the path
 *  until reaching the end and outputs the path it took through the
 *  files along with the IP address of the domain that was searched.
 *  The "input-queries.txt" file is where the inputted domain names
 *  are acquired from to search through the files to find their respected
 *  ID address along with the cache entries file is where the
 *  recent domain names are stored and the dns-queries file is all
 *  the possible domain names available.
 *
 *  How to Run: To run the code input as many domain names in the
 *  "input-queries.txt" file as you want each on their own line. Once you press
 *  run for the program, it will ask the user to input the file name
 *  that it wants to get the domain names from which the default one
 *  is "input-queries.txt" though to use your own txt file right click
 *  in the project Root Directory using intellij IDEA, this one is called
 *  "DNS311" then click "new" and then click "file" and name your custom
 *  input file and then now you can input your domain names in that file
 *  and when the program prompts for a txt file name input your new text
 *  file. Otherwise, feel free to change what's in input-queries.txt as
 *  it's the default and no creating new text files is needed.
 *
 * Sources: GeeksforGeeks.org, docs.oracle.com, GitHub's copilot, Simplilearn.com
 * Tutorialspoint.com
 */

import java.io.*;
import java.util.*;

public class DNS {

    // Project variables
    private static final String CACHE_FILE = "cache-entries.txt";
    private static final int CACHE_SIZE = 3;
    private static LinkedList<String> cache = new LinkedList<>();

    public static void main(String[] args) {
        // First load the cache before getting domain names
        loadCache();

        // Get user input for the input file name
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the input file: ");
        String inputFile = scanner.nextLine();
        System.out.print("\n");

        // Using a scanner to check each domain name and seeing if it's in the cache
        try {
            File file = new File(inputFile);
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                String domainName = fileScanner.nextLine();
                String ip = checkCache(domainName);
                // Print the cache of the domain name if it's in there
                if (ip != null) {
                    System.out.println("Resolving query: " + domainName);
                    System.out.println("cache");
                    System.out.println(domainName + ";" + ip);
                    System.out.print("\n");
                    continue;
                }
                // Using Oracle as a source helped explain stringBuilders
                // Starting at the Root Domain File building the path that the search took and remove the .txt for all the IP's
                // Resolved is so unresolved domain names aren't printed out twice
                String[] parts = domainName.split("\\.");
                String currentFile = "1-0-0-0.txt"; // starting file
                StringBuilder path = new StringBuilder(currentFile.replace(".txt", ""));
                boolean resolved = true;

                // Using both Tutorialspoint.com and GitHub's copilot explained how to use and implement .join, lookup, and .copyOfRange
                for (int i = parts.length - 1; i >= 0; i--) {
                    String subDomain = String.join(".", Arrays.copyOfRange(parts, i, parts.length));
                    String nextFile = lookup(subDomain, currentFile);
                    // Prints if the domain name doesn't match any of the ones from the text files
                    if (nextFile == null) {
                        System.out.println("Resolving query: " + domainName);
                        System.out.println("Unresolved");
                        System.out.print("\n");
                        resolved = false;
                        break;
                    }
                    // Remove the .txt from the IP addresses
                    path.append(";").append(nextFile.replace(".txt", ""));
                    currentFile = nextFile;
                }
                // Prints each domain name with the concatenated toString of the path then the IP of the domain
                if (resolved) {
                    System.out.println("Resolving query: " + domainName);
                    System.out.println(path.toString());
                    System.out.println(domainName + ";" + currentFile.replace(".txt", ""));
                    System.out.print("\n");
                }
                // Update the cache at the end to add the new domain name
                updateCache(domainName, currentFile.replace(".txt", ""));
            }
            fileScanner.close();
            // Will output if there is an error with the file
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        scanner.close();
        // Lastly save and print cache
        saveCache();
        System.out.println("Current cache:");
        for (String entry : cache) {
            System.out.println(entry);
        }
        System.out.print("=====================================================");
    }

    // Loads cache at the start of the program to grab domain names from
    private static void loadCache() {
        try {
            File file = new File(CACHE_FILE);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                cache.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading cache file " + CACHE_FILE);
            e.printStackTrace();
        }
    }

    // Saves the cache to the file and prints at the end of the program the cache
    private static void saveCache() {
        try {
            PrintWriter writer = new PrintWriter(CACHE_FILE);
            for (String entry : cache) {
                writer.println(entry);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while writing to cache file " + CACHE_FILE);
            e.printStackTrace();
        }
    }

    // Checks each domain name to see if it's in the cache, Tutorialspoint.com helped with .split
    private static String checkCache(String domainName) {
        for (String entry : cache) {
            String[] parts = entry.split(";");
            if (parts[0].equals(domainName)) {
                return parts[1];
            }
        }
        return null;
    }

    // Adds and removes what's in the cache
    private static void updateCache(String domainName, String ip) {
        String entry = domainName + ";" + ip;
        // Remove the oldest entry if cache has three already in it
        if (cache.size() == CACHE_SIZE) {
            cache.removeFirst();
        }
        // Then add the new entry to the cache
        cache.addLast(entry);
    }

    // Returns the domain name and associated IP address
    private static String lookup(String subDomain, String filename) {
        try {
            // Add the .txt extension only if it's not already there
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] entry = scanner.nextLine().split(";");
                if (entry[0].equals(subDomain)) {
                    scanner.close();
                    // Add the .txt extension back to the returned filename
                    return entry[1] + ".txt";
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading file " + filename);
            e.printStackTrace();
        }
        return null;
    }
}
