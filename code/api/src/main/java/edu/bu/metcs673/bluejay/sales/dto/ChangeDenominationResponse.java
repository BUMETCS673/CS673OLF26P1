/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Class creation
    Human Contributions: None
    Notes:
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.sales.dto;

public record ChangeDenominationResponse(
    long valueCents,
    String denomination,
    long quantity
) {
}