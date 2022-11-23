package com.mejidprojects.instabruteforce;

import java.util.Scanner;

public class GetStarted {

    public static void main(String[] args) {
	    // write your code here
        Scanner input = new Scanner(System.in);
        String username, wordlistPath;
        final String chromeDriver = "C:\\webdrivers\\chromedriver.exe";

        do {
            // input username & password
            System.out.print("Enter username > ");
            username = input.nextLine();
            System.out.print("Enter path of wordlist > ");
            wordlistPath = input.nextLine();
            // check if it is not blank
        }
        while (username.isEmpty() || wordlistPath.isEmpty());

        InstaBrute brute = new InstaBrute(username, wordlistPath, chromeDriver);
        brute.startBruteForce();
    }
}
