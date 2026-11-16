package com.mycompany.melopart35121;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

/** Stores the details of one message. */
class Message {
    String id;         // 10-digit unique identifier
    String hash;       // generated hash code
    String recipient;  // recipient phone number
    String content;    // message body (max 250 chars)
    String timestamp;  // date and time created
    String status;     // "Sent", "Stored" or "Disregard"

    public Message(String id, String hash, String recipient,
                   String content, String timestamp, String status) {
        this.id = id;
        this.hash = hash;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.status = status;
    }
}

public class MeloPART35121 {

    // PART 3: five parallel arrays (filled by methods, never hard-coded)
    static ArrayList<String> sentMessages      = new ArrayList<>();
    static ArrayList<String> disregardMessages = new ArrayList<>();
    static ArrayList<String> storedMessages    = new ArrayList<>();
    static ArrayList<String> messageHashes     = new ArrayList<>();
    static ArrayList<String> messageIDs        = new ArrayList<>();

    // Master list of full Message objects
    static ArrayList<Message> messages = new ArrayList<>();

    static Scanner input      = new Scanner(System.in);
    static int sentCount      = 0;
    static int messageLimit   = 0;
    static int messageCounter = 0;

    static final String JSON_FILE = "messages.json";

    // ===================== VALIDATION =====================

    public static boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public static boolean checkPasswordComplexity(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(regex, password);
    }

    public static boolean checkCellPhoneNumber(String number) {
        return Pattern.matches("^\\+27\\d{9}$", number);
    }

    public static String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return "Username is not correct. Must have _ and max 5 chars.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correct. Must have 8+ chars, capital letter, number, and special char.";
        }
        return "Username and password successfully captured. User registered!";
    }

    public static boolean loginUser(String username, String password,
                                    String storedUsername, String storedPassword) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    public static String returnLoginStatus(boolean status) {
        if (status) {
            return "Login successful! Welcome back!";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public static String validateNumber(String num) {
        if (num == null) return "incorrect";
        if (!num.startsWith("+27"))
            return "incorrect: Recipient number must start with +27";
        if (num.length() != 12)
            return "incorrect: Recipient number must be 12 characters (+27XXXXXXXXX)";
        if (!num.substring(3).matches("\\d{9}"))
            return "incorrect: only digits allowed after +27";
        return "Recipient number entered successfully";
    }

    // First 2 digits of ID : sequence number : FIRSTLAST word (upper case)
    public static String createMessageHash(String id, int num, String msg) {
        String[] words = msg.trim().split("\\s+");
        String first = words.length > 0 ? words[0] : "MSG";
        String last  = words.length > 1 ? words[words.length - 1] : first;
        return (id.substring(0, 2) + ":" + num + ":" + first + last).toUpperCase();
    }

    // ===================== PARALLEL ARRAY HELPERS =====================

    /** Adds one message to the master list AND every relevant parallel array. */
    public static void addMessageToArrays(String id, String recipient,
                                          String content, String status) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String hash = createMessageHash(id, messages.size(), content);

        messages.add(new Message(id, hash, recipient, content, timestamp, status));
        messageIDs.add(id);
        messageHashes.add(hash);

        if (status.equals("Sent")) {
            sentMessages.add(content);
            sentCount++;
        } else if (status.equals("Stored")) {
            storedMessages.add(content);
        } else if (status.equals("Disregard")) {
            disregardMessages.add(content);
        } else {
            System.out.println("Unknown status '" + status + "' for message: " + id);
        }
    }

    /** Removes a message from the master list AND every relevant parallel array. */
    public static void removeMessageFromArrays(Message m) {
        messages.remove(m);
        messageIDs.remove(m.id);
        messageHashes.remove(m.hash);

        if (m.status.equals("Sent")) {
            sentMessages.remove(m.content);
            sentCount--;
        } else if (m.status.equals("Stored")) {
            storedMessages.remove(m.content);
        } else if (m.status.equals("Disregard")) {
            disregardMessages.remove(m.content);
        }
    }

    // ===================== REGISTRATION AND LOGIN =====================

    public static String[] registerUser() {
        System.out.println("===== REGISTRATION =====");

        String username;
        while (true) {
            System.out.print("Enter Username (must contain _ and max 5 characters): ");
            username = input.nextLine();
            if (checkUserName(username)) break;
            System.out.println("Invalid username. Example: user_");
        }

        String password;
        while (true) {
            System.out.print("Enter Password (8+ characters, capital, number, special character): ");
            password = input.nextLine();
            if (checkPasswordComplexity(password)) break;
            System.out.println("Invalid password. Example: Password5!");
        }

        while (true) {
            System.out.print("Enter Cell Phone (+27 followed by 9 digits): ");
            String cellPhone = input.nextLine();
            if (checkCellPhoneNumber(cellPhone)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }
            System.out.println("Invalid number. Example: +27123456789");
        }

        System.out.println(registerUser(username, password));
        return new String[]{username, password};
    }

    public static void login(String[] credentials) {
        System.out.println("\n===== LOGIN =====");
        while (true) {
            System.out.print("Enter Username: ");
            String enteredUser = input.nextLine();
            System.out.print("Enter Password: ");
            String enteredPass = input.nextLine();

            boolean status = loginUser(enteredUser, enteredPass, credentials[0], credentials[1]);
            System.out.println(returnLoginStatus(status));

            if (status) {
                System.out.println("WELCOME TO QUICKCHAT!");
                break;
            }
        }
    }

    public static int getMessageLimit() {
        while (true) {
            try {
                System.out.print("How many messages do you want to send? ");
                int limit = Integer.parseInt(input.nextLine().trim());
                if (limit > 0) return limit;
                System.out.println("Please enter a number greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Enter a valid whole number.");
            }
        }
    }

    // ===================== MAIN MENU =====================

    public static void displayMenu() {
        System.out.println("\n========== QUICKCHAT MENU ==========");
        System.out.println("1. Send / Store / Disregard a Message");
        System.out.println("2. Show All Messages");
        System.out.println("3. Discard Last Message");
        System.out.println("4. Stored Messages Menu (Part 3)");
        System.out.println("5. Save Messages to JSON");
        System.out.println("6. Read JSON File into Array");
        System.out.println("7. Quit");
        System.out.println("====================================");
        System.out.print("Choose an option (1-7): ");
    }

    public static void main(String[] args) {
        String[] credentials = registerUser();
        login(credentials);
        messageLimit = getMessageLimit();

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1": sendMessage();            break;
                case "2": showMessages();           break;
                case "3": discardLastMessage();     break;
                case "4": storedMessagesMenu();     break;
                case "5": storeMessage();           break;
                case "6": readJsonIntoArray();      break;
                case "7": saveAndExit(); running = false; break;
                default:  System.out.println("[ERROR] Invalid option. Choose 1-7.");
            }
        }
    }

    // ===================== SEND / SHOW / DISCARD =====================

    public static void sendMessage() {
        if (sentCount >= messageLimit) {
            System.out.println("[WARNING] Message limit reached!");
            return;
        }

        messageCounter++;
        System.out.println("\n---- Message No. " + messageCounter + " ----");

        System.out.print("Enter Recipient Number (+27XXXXXXXXX): ");
        String recipient = input.nextLine().trim();
        String validation = validateNumber(recipient);
        if (!validation.equals("Recipient number entered successfully")) {
            System.out.println("[ERROR] " + validation);
            return;
        }

        String recipientUsername;
        while (true) {
            System.out.print("Enter Recipient Username (max 5 characters and contains _): ");
            recipientUsername = input.nextLine();
            if (checkUserName(recipientUsername)) break;
            System.out.println("Invalid recipient username. Example: user_");
        }

        System.out.print("Enter Message (maximum 250 characters): ");
        String content = input.nextLine();
        if (content.trim().isEmpty()) {
            System.out.println("[ERROR] Message cannot be empty.");
            return;
        }
        if (content.length() > 250) {
            System.out.println("[ERROR] Message exceeds 250 characters by "
                    + (content.length() - 250) + " characters. Please reduce the size.");
            return;
        }

        System.out.println("\nWhat would you like to do with this message?");
        System.out.println(" 1. Send Message");
        System.out.println(" 2. Store Message");
        System.out.println(" 3. Disregard Message");
        System.out.print("Choose (1-3): ");
        String choice = input.nextLine().trim();

        String status;
        if (choice.equals("1")) {
            status = "Sent";
        } else if (choice.equals("2")) {
            status = "Stored";
        } else if (choice.equals("3")) {
            status = "Disregard";
        } else {
            System.out.println("Invalid choice. Defaulting to Sent.");
            status = "Sent";
        }

        String id = String.format("%010d", (long) (Math.random() * 10000000000L));
        addMessageToArrays(id, recipient, content, status);
        Message added = messages.get(messages.size() - 1);

        System.out.println("\n===== MESSAGE DETAILS =====");
        System.out.println("Message ID         : " + added.id);
        System.out.println("Hash               : " + added.hash);
        System.out.println("Recipient Number   : " + recipient);
        System.out.println("Recipient Username : " + recipientUsername);
        System.out.println("Message            : " + content);
        System.out.println("Status             : " + status);
        System.out.println("Time               : " + added.timestamp);
        System.out.println("Message successfully processed as: " + status);
    }

    public static void showMessages() {
        if (messages.isEmpty()) {
            System.out.println("No messages available.");
            return;
        }
        System.out.println("\n===== ALL MESSAGES =====");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            System.out.println("\nMessage " + (i + 1));
            System.out.println("ID       : " + m.id);
            System.out.println("Hash     : " + m.hash);
            System.out.println("Recipient: " + m.recipient);
            System.out.println("Message  : " + m.content);
            System.out.println("Status   : " + m.status);
            System.out.println("Time     : " + m.timestamp);
        }
    }

    public static void discardLastMessage() {
        if (messages.isEmpty()) {
            System.out.println("No messages to discard.");
            return;
        }
        Message last = messages.get(messages.size() - 1);
        removeMessageFromArrays(last);
        System.out.println("Message discarded: " + last.content);
    }

    // ===================== PART 3: STORED MESSAGES MENU =====================

    public static void storedMessagesMenu() {
        boolean inSubMenu = true;
        while (inSubMenu) {
            System.out.println("\n.......... STORED MESSAGES MENU ..........");
            System.out.println("a. Display sender and recipient of all sent messages");
            System.out.println("b. Display the longest message");
            System.out.println("c. Search for a message by message ID");
            System.out.println("d. Search all messages for a recipient");
            System.out.println("e. Delete a message using its hash");
            System.out.println("f. Display full message report");
            System.out.println("o. Back to main menu");
            System.out.println("............................................");
            System.out.print("Choose (a-f or o): ");
            String ch = input.nextLine().trim().toLowerCase();

            switch (ch) {
                case "a": displaySenderRecipient(); break;
                case "b": displayLongestMessage();  break;
                case "c": searchByMessageID();      break;
                case "d": searchByRecipient();      break;
                case "e": deleteByHash();           break;
                case "f": displayFullReport();      break;
                case "o": inSubMenu = false;        break;
                default:  System.out.println("Invalid option.");
            }
        }
    }

    // a. Sender (this user) and recipient of all Sent messages
    public static void displaySenderRecipient() {
        System.out.println("\n---- Sent Messages: Sender and Recipient ----");
        boolean found = false;
        for (Message m : messages) {
            if (m.status.equals("Sent")) {
                System.out.println("Sender: You  |  Recipient: " + m.recipient
                        + "  |  Message: " + m.content);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No sent messages found.");
        }
    }

    // b. Longest message across all messages; returns "" if none
    public static String displayLongestMessage() {
        String longest = "";
        for (Message m : messages) {
            if (m.content.length() > longest.length()) {
                longest = m.content;
            }
        }
        if (longest.isEmpty()) {
            System.out.println("No messages available.");
        } else {
            System.out.println("\n---- Longest Message ----");
            System.out.println(longest);
        }
        return longest;
    }

    // c. Search by message ID (also matches recipient/developer number)
    public static String searchByMessageID() {
        System.out.print("Enter Message ID or Developer Number: ");
        return searchByMessageIDLogic(input.nextLine().trim());
    }

    public static String searchByMessageIDLogic(String query) {
        for (Message m : messages) {
            if (m.id.equalsIgnoreCase(query) || m.recipient.equalsIgnoreCase(query)) {
                System.out.println("Recipient : " + m.recipient);
                System.out.println("Message   : " + m.content);
                return m.content;
            }
        }
        System.out.println("No message found for: " + query);
        return "";
    }

    // d. Search all messages for a recipient
    public static ArrayList<String> searchByRecipient() {
        System.out.print("Enter Recipient Number: ");
        return searchByRecipientLogic(input.nextLine().trim());
    }

    public static ArrayList<String> searchByRecipientLogic(String recipient) {
        ArrayList<String> results = new ArrayList<>();
        System.out.println("\n---- Messages for recipient: " + recipient + " ----");
        for (Message m : messages) {
            if (m.recipient.equalsIgnoreCase(recipient)) {
                System.out.println("[" + m.status + "] " + m.content);
                results.add(m.content);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No messages found for this recipient.");
        }
        return results;
    }

    // e. Delete by hash
    public static boolean deleteByHash() {
        System.out.print("Enter Message Hash to delete: ");
        return deleteByHashLogic(input.nextLine().trim());
    }

    public static boolean deleteByHashLogic(String hash) {
        Message target = findByHash(hash);
        if (target == null) {
            System.out.println("No message found with hash: " + hash);
            return false;
        }
        removeMessageFromArrays(target);
        System.out.println("Message \"" + target.content + "\" successfully deleted.");
        return true;
    }

    public static Message findByHash(String hash) {
        for (Message m : messages) {
            if (m.hash.equalsIgnoreCase(hash)) return m;
        }
        return null;
    }

    public static Message findById(String id) {
        for (Message m : messages) {
            if (m.id.equalsIgnoreCase(id)) return m;
        }
        return null;
    }

    // f. Full report
    public static void displayFullReport() {
        System.out.println("\n.......... FULL MESSAGE REPORT ..........");
        if (messages.isEmpty()) {
            System.out.println("No messages available.");
            return;
        }
        System.out.printf("%-20s %-15s %-10s %s%n", "Message Hash", "Recipient", "Status", "Message");
        System.out.println("-------------------------------------------------------------------");
        for (Message m : messages) {
            System.out.printf("%-20s %-15s %-10s %s%n", m.hash, m.recipient, m.status, m.content);
        }
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Sent: " + sentMessages.size()
                + " | Stored: " + storedMessages.size()
                + " | Disregarded: " + disregardMessages.size());
    }

    // ===================== PART 3: JSON FILE =====================

    public static void storeMessage() {
        if (messages.isEmpty()) {
            System.out.println("[INFO] No messages to save.");
            return;
        }
        try (FileWriter fw = new FileWriter(JSON_FILE)) {
            fw.write("[\n");
            for (int i = 0; i < messages.size(); i++) {
                Message m = messages.get(i);
                fw.write("  {\n");
                fw.write("    \"id\": \""        + escapeJson(m.id)        + "\",\n");
                fw.write("    \"hash\": \""      + escapeJson(m.hash)      + "\",\n");
                fw.write("    \"recipient\": \"" + escapeJson(m.recipient) + "\",\n");
                fw.write("    \"message\": \""   + escapeJson(m.content)   + "\",\n");
                fw.write("    \"status\": \""    + escapeJson(m.status)    + "\",\n");
                fw.write("    \"time\": \""      + escapeJson(m.timestamp) + "\"\n");
                fw.write("  }" + (i < messages.size() - 1 ? "," : "") + "\n");
            }
            fw.write("]");
            System.out.println(messages.size() + " message(s) saved to " + JSON_FILE + ".");
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to save file: " + e.getMessage());
        }
    }

    public static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String unescapeJson(String value) {
        return value.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    /** Reads messages.json line by line and loads each "message" value into storedMessages. */
    public static void readJsonIntoArray() {
        System.out.println("\n---- Reading JSON file into array ----");
        ArrayList<String> loaded = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(JSON_FILE))) {
            String line;
            String currentMessage = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"message\"")) {
                    String value = line.substring(line.indexOf(":") + 1).trim();
                    if (value.endsWith(",")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    currentMessage = unescapeJson(value);
                }

                if (line.startsWith("}") && currentMessage != null) {
                    loaded.add(currentMessage);
                    if (!storedMessages.contains(currentMessage)) {
                        storedMessages.add(currentMessage);
                    }
                    currentMessage = null;
                }
            }

            if (loaded.isEmpty()) {
                System.out.println("JSON file is empty or contains no messages.");
            } else {
                System.out.println(loaded.size() + " message(s) loaded from " + JSON_FILE + ":");
                for (int i = 0; i < loaded.size(); i++) {
                    System.out.println("  [" + (i + 1) + "] " + loaded.get(i));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read '" + JSON_FILE + "'. Save messages first (option 5).");
        }
    }

    // ===================== SAVE AND EXIT =====================

    public static void saveAndExit() {
        while (true) {
            System.out.print("Save messages before exiting? (yes/no): ");
            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("yes")) {
                storeMessage();
                break;
            } else if (answer.equalsIgnoreCase("no")) {
                break;
            }
            System.out.println("Invalid input. Please type 'yes' or 'no'.");
        }
        System.out.println("Total messages sent: " + sentCount);
        System.out.println("Thank you for using QuickChat!");
    }
}