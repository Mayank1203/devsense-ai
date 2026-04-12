package com.devsense.model.enums;

public enum Severity {
    CRITICAL,  // score -2.0  — must fix before release
    HIGH,      // score -1.0  — fix soon
    MEDIUM,    // score -0.5  — fix in next sprint
    LOW,       // score -0.2  — nice to fix
    INFO       // score -0.0  — informational only
}
