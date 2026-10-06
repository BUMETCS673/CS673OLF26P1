// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Enum definition for movement types matching database
// seeds.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.domain;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create domain enum representing inventory movement types
// based on database schema. Map movement type codes for type safety."
// AI Contribution: Initial draft (~100%)
// Modifications: Created enum constants matchingFlyway migration scripts.
// Verification: Unit tests, DB schema validation.
// Confidence: High
public enum MovementTypeCode {
    SALE,
    RESTOCK,
    ADJUSTMENT,
    RETURN
}