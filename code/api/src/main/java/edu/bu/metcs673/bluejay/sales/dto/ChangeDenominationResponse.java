package edu.bu.metcs673.bluejay.sales.dto;

public record ChangeDenominationResponse(
    long valueCents,
    String denomination,
    long quantity
) {
}