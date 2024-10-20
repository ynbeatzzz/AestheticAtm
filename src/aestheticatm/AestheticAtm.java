package aestheticatm;

import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AestheticAtm {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner kb = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("0.00");

        //Constants
        final String INPUT_FILE_PATH = "ATM-Database.txt";
        final String TEMP_FILE_PATH = "temp.txt";

        //Variable declaration
        String line, AccountHolder = "";
        int AccountNumber = 0, userAccountNumber, cardPin = 0, userPin, option, newPin;
        double balance = 0, withdrawalAmount, depositAmount;
        boolean accountFound = false;
        boolean accAccess = false;
        boolean authorizedChange = false;

        // File initialization
        File inputFile = new File(INPUT_FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);

        do {
            // BufferedReader and BufferedWriter initialization
            BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
            BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH));

            // Welcome message and input to start transaction
            System.out.println("\n\tWelcome to Aesthetic ATM\n");
            System.out.print("Enter your Account number to start transaction: ");
            userAccountNumber = kb.nextInt();

            // Reading and grouping data
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("AccountNumber=" + userAccountNumber)) {
                    AccountNumber = Integer.parseInt(line.substring(14)); // Extraction of Account number
                    AccountHolder = reader.readLine().substring(14); // Extraction of Account user name 
                    cardPin = Integer.parseInt(reader.readLine().substring(4)); // Extraction of PIN (Personal Identification Number)
                    balance = Double.parseDouble(reader.readLine().substring(8)); // Extraction of Account Balance.
                    accountFound = true;

                    // Closing Buffered reader and Buffered writer to release underlying information
                    reader.close();
                    writer.close();
                    break;
                }
            }

            if (accountFound) {
                //Authentication
                System.out.print("Enter your PIN: ");
                userPin = kb.nextInt();
                for (int pinTrial = 1; pinTrial < 3; pinTrial++) {
                    if (userPin == cardPin) {
                        System.out.println("\nAccess Granted!" + "\nAccout owner: " + AccountHolder + "\n");
                        accAccess = true;
                        break;
                    } else {
                        System.out.println("Wrong PIN try again!");
                        System.out.print("Enter your PIN: ");
                        userPin = kb.nextInt();
                    }
                }

                if (accAccess) {
                    do {
                        // Main menu and user input
                        System.out.println("1. Withdraw\n2. Deposit\n3. Check Balance\n4. Change Pin\n5. Exit");
                        System.out.print("Choose your option: ");
                        option = kb.nextInt();

                        switch (option) {
                            case 1:
                                System.out.print("Enter amount to withdraw: R ");
                                withdrawalAmount = kb.nextDouble();

                                if (withdrawalAmount > 0 && withdrawalAmount <= balance) {
                                    balance = balance - withdrawalAmount;

                                    //Re-Initialize Buffered reader and Buffered writer for reuse and start on a clean state
                                    reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
                                    writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH));

                                    // Iterate through lines in the database to update balance amount after withdrawal.
                                    while ((line = reader.readLine()) != null) {
                                        if (line.equals("AccountNumber=" + AccountNumber)) {
                                            writer.write(line);
                                            authorizedChange = true;
                                        } else if (authorizedChange && line.startsWith("Balance=")) {
                                            writer.write("Balance=" + balance);
                                            authorizedChange = false;
                                        } else {
                                            writer.write(line);
                                        }
                                        writer.newLine();
                                    }

                                    // Closing BufferedReader and BufferedWriter to release underlying information
                                    reader.close();
                                    writer.close();

                                    // Replace the old mock-database with the new temporary file
                                    Files.delete(inputFile.toPath());
                                    Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    System.out.println("\tTransaction summary:");
                                    System.out.println("\tYou withdrew: R " + df.format(withdrawalAmount));
                                    System.out.println("\tNew balance is: R " + df.format(balance) + "\n");
                                    break;
                                } else if (withdrawalAmount > balance) {
                                    System.out.println("Insufficient balance!\n");
                                } else {
                                    System.out.println("Invalid withdrawal amount!");
                                }
                                break;
                            case 2:
                                System.out.print("Enter amount to deposit: R ");
                                depositAmount = kb.nextDouble();

                                if (depositAmount > 0) {
                                    balance = balance + depositAmount;

                                    //Re-Initialize BufferedReader and BufferedWriter for reuse and start on a clean state
                                    reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
                                    writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH));

                                    // Iterate through lines in the database to update balance amount after deposit.
                                    while ((line = reader.readLine()) != null) {
                                        if (line.equals("AccountNumber=" + AccountNumber)) {
                                            writer.write(line);
                                            authorizedChange = true;
                                        } else if (authorizedChange && line.startsWith("Balance=")) {
                                            writer.write("Balance=" + balance);
                                            authorizedChange = false;
                                        } else {
                                            writer.write(line);
                                        }
                                        writer.newLine();
                                    }

                                    reader.close();
                                    writer.close();

                                    // Replace the old mock-database with the new temporary file
                                    Files.delete(inputFile.toPath());
                                    Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    System.out.println("\tTransaction summary:");
                                    System.out.println("\tYou deposited: R " + df.format(depositAmount));
                                    System.out.println("\tNew balance is: R " + df.format(balance) + "\n");
                                } else {
                                    System.out.println("Invalid deposit amount!\n");
                                }
                                break;
                            case 3:
                                System.out.println("Your balance is: R " + df.format(balance) + "\n");
                                break;
                            case 4:
                                System.out.print("Enter your new PIN: ");
                                newPin = kb.nextInt();

                                //Re-Initialize BufferedReader and BufferedWriter for reuse and start on a clean state
                                reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
                                writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH));

                                // Iterate through lines in the database to update balance amount after deposit.
                                while ((line = reader.readLine()) != null) {
                                    if (line.equals("Pin=" + cardPin)) {
                                        writer.write("Pin=" + newPin);
                                    } else {
                                        writer.write(line);
                                    }
                                    writer.newLine();
                                }

                                // Closing Buffered reader and Buffered writer to release underlying information
                                reader.close();
                                writer.close();

                                // Replace the old mock-database with the new temporary file
                                Files.delete(inputFile.toPath());
                                Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("PIN Changed successfully\n");
                                break;
                            case 5:
                                System.out.println("Transaction ended!\nThank you for using the ATM. Goodbye!");
                                System.out.println("\n=============================================================");
                                break;
                            default:
                                System.out.println("Invalid option! Please choose a valid option from the menu.\n");
                                break;
                        }
                    } while (option != 5);
                } else {
                    System.out.println("You failed to enter your PIN Multiple times, you cannot proceed."
                            + "\nThank you for using the ATM. Goodbye!\n");
                }
            } else {
                System.out.println("Your account number is not recognised, you cannot proceed with transactions."
                        + "\nThank you for using the ATM. Goodbye!\n");
                System.out.println("\n=============================================================");
            }
        } while (true); // Sentinel is not in place because we need the ATM to be functional forever
    }
}