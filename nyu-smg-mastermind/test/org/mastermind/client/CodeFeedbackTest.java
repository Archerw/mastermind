package org.mastermind.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class CodeFeedbackTest {

  @Test
  public void testGetValidFeedback() {
    assertEquals(CodeFeedback.getValidFeedback("1234", "1234"),"4b0w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1235"),"3b0w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1674"),"2b0w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1567"),"1b0w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1245"),"2b1w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1324"),"2b2w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1356"),"1b1w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1326"),"1b2w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "1342"),"1b3w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "4321"),"0b4w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "2345"),"0b3w");
    assertEquals(CodeFeedback.getValidFeedback("1234", "2456"),"0b2w");
    }
}
