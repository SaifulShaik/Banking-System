






//**************************//
//							//
//		Banking System		//
//	 Made by: Saiful Shaik	//
//							//
//**************************//











package bankingSystem;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String owner;
    private String password;
    private double balance;

    public Account(String accountNumber, String owner, String password, double balance) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.password = password;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwner() {
        return owner;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposit of $" + amount + " successful. New balance: $" + balance);
    }

    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawal of $" + amount + " successful. New balance: $" + balance);
        } else {
            System.out.println("Insufficient funds. Withdrawal failed.");
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                '}';
    }
}

class BankingSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Account> accounts;

    public BankingSystem() {
        this.accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getOwner(), account);
    }

    public Account getAccount(String owner) {
        return accounts.get(owner);
    }

    public boolean usernameExists(String username) {
        return accounts.containsKey(username);
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }
}

public class SimpleBankingSystem {

    private static final String DATA_FILE = "banking_data.ser";

    public static void main(String[] args) {
        BankingSystem bankingSystem = loadBankingSystem();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Advanced Banking System");
        System.out.println("=====================================");

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        if (bankingSystem.usernameExists(username)) {
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            if (authenticate(bankingSystem, username, password)) {
                System.out.println("Login successful. Welcome, " + username + "!");
                showMainMenu(bankingSystem, scanner, username);
            } else {
                System.out.println("Login failed. Quitting Application...");
            }
        } else {
            System.out.println("\nUser not found. Do you want to sign up? (yes/no)");
            String signUpOption = scanner.nextLine();
            if (signUpOption.equalsIgnoreCase("yes")) {
                signUp(bankingSystem, scanner, username);
            } else {
                System.out.println("Quiting Application...");
            }
        }

        saveBankingSystem(bankingSystem);
    }

    private static boolean authenticate(BankingSystem bankingSystem, String username, String password) {
        Account account = bankingSystem.getAccounts().values().stream()
                .filter(acc -> acc.getOwner().equalsIgnoreCase(username) && acc.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        return account != null;
    }

    private static void signUp(BankingSystem bankingSystem, Scanner scanner, String username) {
        System.out.print("Create a password: ");
        String password = scanner.nextLine();

        Account newAccount = new Account(
                "A" + (bankingSystem.getAccounts().size() + 1),
                username,
                password,
                0.0
        );
        bankingSystem.addAccount(newAccount);

        System.out.println("Sign up successful. Welcome, " + username + "!");
        showMainMenu(bankingSystem, scanner, username);
    }

    private static void showMainMenu(BankingSystem bankingSystem, Scanner scanner, String username) {
        while (true) {
            System.out.println("\nMain Menu");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. View Balance");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            Account userAccount = bankingSystem.getAccount(username);

            if (userAccount == null) {
                System.out.println("Account not found. Exiting...");
                return;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter deposit amount: $");
                    double depositAmount = scanner.nextDouble();
                    userAccount.deposit(depositAmount);
                    break;
                case 2:
                    System.out.print("Enter withdrawal amount: $");
                    double withdrawalAmount = scanner.nextDouble();
                    userAccount.withdraw(withdrawalAmount);
                    break;
                case 3:
                    System.out.println("Current Balance: $" + userAccount.getBalance());
                    break;
                case 4:
                    System.out.println("Quitting Application...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

            saveBankingSystem(bankingSystem);
        }
    }

    private static BankingSystem loadBankingSystem() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (BankingSystem) ois.readObject();
        } catch (FileNotFoundException e) {
            return new BankingSystem();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveBankingSystem(BankingSystem bankingSystem) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(bankingSystem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
