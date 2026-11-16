package com.mycompany.melopart35121;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class MeloPART35121Test {

    static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";

    @BeforeEach
    public void setUp() {
        MeloPART35121.messages.clear();
        MeloPART35121.sentMessages.clear();
        MeloPART35121.disregardMessages.clear();
        MeloPART35121.storedMessages.clear();
        MeloPART35121.messageHashes.clear();
        MeloPART35121.messageIDs.clear();
        MeloPART35121.sentCount = 0;

        MeloPART35121.addMessageToArrays("0000000001", "+27834557896", "Did you get the cake?", "Sent");
        MeloPART35121.addMessageToArrays("0000000002", "+27838884567", MSG2, "Stored");
        MeloPART35121.addMessageToArrays("0000000003", "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        MeloPART35121.addMessageToArrays("0000000004", "0838884567", "It is dinner time!", "Sent");
        MeloPART35121.addMessageToArrays("0000000005", "+27838884567", "Ok, I am leaving without you.", "Stored");
    }

    // TEST 1: Sent messages array
    @Test
    @DisplayName("Sent array contains correct entries")
    public void testSentMessagesContent() {
        assertTrue(MeloPART35121.sentMessages.contains("Did you get the cake?"));
        assertTrue(MeloPART35121.sentMessages.contains("It is dinner time!"));
    }

    @Test
    @DisplayName("Sent array has exactly 2 entries")
    public void testSentMessagesSize() {
        assertEquals(2, MeloPART35121.sentMessages.size());
    }

    @Test
    @DisplayName("Stored and disregarded messages are not in sent array")
    public void testOthersNotInSent() {
        assertFalse(MeloPART35121.sentMessages.contains(MSG2));
        assertFalse(MeloPART35121.sentMessages.contains("Ok, I am leaving without you."));
        assertFalse(MeloPART35121.sentMessages.contains("Yohoooo, I am at your gate."));
    }

    @Test
    @DisplayName("sentCount matches sentMessages size")
    public void testSentCount() {
        assertEquals(MeloPART35121.sentMessages.size(), MeloPART35121.sentCount);
    }

    // TEST 2: Longest message
    @Test
    @DisplayName("Longest message is message 2")
    public void testLongestMessage() {
        assertEquals(MSG2, MeloPART35121.displayLongestMessage());
    }

    @Test
    @DisplayName("Longest message returns empty string when list is empty")
    public void testLongestMessageEmpty() {
        MeloPART35121.messages.clear();
        assertEquals("", MeloPART35121.displayLongestMessage());
    }

    // TEST 3: Search by message ID / developer number
    @Test
    @DisplayName("Search 0838884567 returns 'It is dinner time!'")
    public void testSearchByDeveloperNumber() {
        assertEquals("It is dinner time!", MeloPART35121.searchByMessageIDLogic("0838884567"));
    }

    @Test
    @DisplayName("Search by ID 0000000001 returns correct message")
    public void testSearchById() {
        assertEquals("Did you get the cake?", MeloPART35121.searchByMessageIDLogic("0000000001"));
    }

    @Test
    @DisplayName("Search for unknown ID returns empty string")
    public void testSearchByIdNotFound() {
        assertEquals("", MeloPART35121.searchByMessageIDLogic("NOTEXISTS"));
    }

    // TEST 4: Search by recipient
    @Test
    @DisplayName("Recipient +27838884567 has 2 messages")
    public void testRecipientCount() {
        assertEquals(2, MeloPART35121.searchByRecipientLogic("+27838884567").size());
    }

    @Test
    @DisplayName("Recipient search returns messages 2 and 5")
    public void testRecipientContent() {
        ArrayList<String> r = MeloPART35121.searchByRecipientLogic("+27838884567");
        assertTrue(r.contains(MSG2));
        assertTrue(r.contains("Ok, I am leaving without you."));
    }

    @Test
    @DisplayName("Unknown recipient returns empty list")
    public void testRecipientNotFound() {
        assertTrue(MeloPART35121.searchByRecipientLogic("+27000000000").isEmpty());
    }

    // TEST 5: Delete by hash
    @Test
    @DisplayName("Delete by hash returns true for valid hash")
    public void testDeleteTrue() {
        String hash = getHashForContent(MSG2);
        assertNotNull(hash);
        assertTrue(MeloPART35121.deleteByHashLogic(hash));
    }

    @Test
    @DisplayName("Delete by hash shrinks messages, stored array, ids and hashes")
    public void testDeleteShrinksArrays() {
        String hash = getHashForContent(MSG2);
        MeloPART35121.deleteByHashLogic(hash);
        assertEquals(4, MeloPART35121.messages.size());
        assertEquals(1, MeloPART35121.storedMessages.size());
        assertEquals(4, MeloPART35121.messageIDs.size());
        assertEquals(4, MeloPART35121.messageHashes.size());
    }

    @Test
    @DisplayName("Deleted message is gone")
    public void testDeletedGone() {
        MeloPART35121.deleteByHashLogic(getHashForContent(MSG2));
        for (Message m : MeloPART35121.messages) {
            assertNotEquals(MSG2, m.content);
        }
    }

    @Test
    @DisplayName("Unknown hash returns false and changes nothing")
    public void testDeleteUnknownHash() {
        int before = MeloPART35121.messages.size();
        assertFalse(MeloPART35121.deleteByHashLogic("BADHASH"));
        assertEquals(before, MeloPART35121.messages.size());
    }

    // TEST 6: Report / arrays populated
    @Test
    @DisplayName("Report: 5 messages, 5 hashes, 5 IDs")
    public void testReportCounts() {
        assertEquals(5, MeloPART35121.messages.size());
        assertEquals(5, MeloPART35121.messageHashes.size());
        assertEquals(5, MeloPART35121.messageIDs.size());
    }

    @Test
    @DisplayName("Report: 2 Sent, 2 Stored, 1 Disregarded")
    public void testReportDistribution() {
        assertEquals(2, MeloPART35121.sentMessages.size());
        assertEquals(2, MeloPART35121.storedMessages.size());
        assertEquals(1, MeloPART35121.disregardMessages.size());
    } 

    @Test
    @DisplayName("Report: no null or empty fields")
    public void testReportNoNulls() {
        for (Message m : MeloPART35121.messages) {
            assertNotNull(m.id);
            assertNotNull(m.hash);
            assertNotNull(m.recipient);
            assertNotNull(m.content);
            assertFalse(m.id.isEmpty());
            assertFalse(m.hash.isEmpty());
            assertFalse(m.recipient.isEmpty());
            assertFalse(m.content.isEmpty());
        }
    }

    // Validation tests
    @Test
    public void testValidateNumberValid() {
        assertEquals("Recipient number entered successfully",
                MeloPART35121.validateNumber("+27834557896"));
    }

    @Test
    public void testValidateNumberNoPrefix() {
        assertTrue(MeloPART35121.validateNumber("0834557896").contains("incorrect"));
    }

    @Test
    public void testValidateNumberTooShort() {
        assertTrue(MeloPART35121.validateNumber("+2783455").contains("incorrect"));
    }

    @Test
    public void testCheckUserName() {
        assertTrue(MeloPART35121.checkUserName("Us_er"));
        assertFalse(MeloPART35121.checkUserName("UserA"));
        assertFalse(MeloPART35121.checkUserName("User_Name"));
    }

    @Test
    public void testPasswordComplexity() {
        assertTrue(MeloPART35121.checkPasswordComplexity("Password1!"));
        assertFalse(MeloPART35121.checkPasswordComplexity("password1!"));
        assertFalse(MeloPART35121.checkPasswordComplexity("Password!!"));
        assertFalse(MeloPART35121.checkPasswordComplexity("Password1A"));
    }

    @Test
    @DisplayName("Hash format is 00:0:DIDCAKE?")
    public void testCreateMessageHash() {
        String hash = MeloPART35121.createMessageHash("0000000001", 0, "Did you get the cake?");
        assertEquals("00:0:DIDCAKE?", hash);
    }

    private String getHashForContent(String content) {
        for (Message m : MeloPART35121.messages) {
            if (m.content.equals(content)) return m.hash;
        }
        return null;
    }
}