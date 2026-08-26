# AI-Powered Product Verification & Legal Metrology Compliance Engine API

## 1. System Overview
**ProofMark** is a modular, AI-driven inspection and Legal Metrology compliance verification platform designed for packaged commodity inspection under the **Legal Metrology (Packaged Commodities) Rules, 2011** (LMPC 2011) and modern statutory amendments.

The architecture connects on-device computer vision, edge text recognition, cloud generative multimodal AI (Gemini 3.5/3.6 Vision), statutory database benchmarking, and automated rule evaluation into a real-time workflow.

---

## 2. Core Architecture Modules & Services

### 2.1 Live Camera & Image Analysis Module (`CameraScreen.kt`)
* **Live Camera Stream:** Continuous CameraX preview with touch-to-focus and auto-focus control.
* **Image Quality Assessment (`evaluateImageQuality`):**
  * **Blur Detection:** Calculates Laplacian variance threshold to detect motion or lens blur (`isBlurry`).
  * **Low-Light Detection:** Evaluates average pixel luminance (`isLowLight`).
  * **Perspective Correction:** Calculates edge alignment and skew angles (`perspectiveSkewDeg`).
* **Continuous Scanning & Mode Switcher:** Instant OCR overlay with real-time text block highlighting.

---

### 2.2 Dual-Tier OCR & Structured AI Perception Engine (`GeminiCompliancePerceptionService.kt` & `MLKitTextRecognitionService.kt`)
1. **Tier 1 (Multimodal Generative Vision AI):**
   - Model: `gemini-3.5-flash` / `gemini-3.6-flash`.
   - Extracts complete mandatory packaging declarations directly into a structured JSON schema.
   - Generates normalized field key-value pairs, perception confidence scores, and bounding box coordinates (`xMin`, `yMin`, `xMax`, `yMax`).
2. **Tier 2 (On-Device Google ML Kit Text Recognition):**
   - High-speed, zero-latency on-device OCR fallback when network connectivity is degraded or off-grid.
   - Extracts text blocks, lines, and bounding boxes.
   - Automated regex and heuristic parser for statutory keys (MRP, Net Qty, Mfg Date, Unit Sale Price, Address, Country of Origin, Consumer Care, FSSAI).
3. **Tier 3 (Deterministic Fallback Engine):**
   - Ensures continuous app reliability even when camera or API keys are unconfigured.

---

### 2.3 Legal Metrology Rules Engine (`LegalMetrologyRulesEngine.kt`)
Evaluates 10+ core rules under LMPC 2011 and amendments:

| Rule Code | Section Ref | Title | Description / Validation Logic |
|---|---|---|---|
| `LM-PC-6-1-A` | Rule 6(1)(a) & Sec 36(1) | Manufacturer / Packer / Importer Details | Validates complete name and physical address with postal code. Flags missing or vague location. |
| `LM-PC-6-1-B` | Rule 6(1)(b) | Generic / Common Commodity Name | Verifies generic or common name is prominently declared on Principal Display Panel (PDP). |
| `LM-PC-6-1-C` | Rule 6(1)(c) & Rule 12 | Net Quantity in Standard SI Units | Enforces metric SI units (g, kg, ml, l, m, N). Flags invalid abbreviations like "gms" or "cc". |
| `LM-PC-6-1-D` | Rule 6(1)(d) | Month & Year of Packing / Mfg | Checks valid month/year format (MM/YYYY or Mon YYYY). Flags missing dates or invalid formats. |
| `LM-PC-6-1-DA`| Rule 6(1)(da) | Mandatory Unit Sale Price (USP) | Enforces Unit Sale Price in ₹/g, ₹/ml, ₹/100g or ₹/100ml for multi-unit packages. |
| `LM-PC-6-1-E` | Rule 6(1)(e) & Sec 36(2) | MRP & Tax Qualification | Mandatory inclusive tax statement: `MRP ₹ XX.XX (inclusive of all taxes)`. Flags missing tax qualification. |
| `LM-PC-6-1-F` | Rule 6(1)(f) | Consumer Care Information | Checks for name, phone number, email, and address for consumer grievance redressal. |
| `LM-PC-6-10`  | Rule 6(1)(m) | Country of Origin | Verifies explicit declaration of country of origin ("Made in India" / "Product of India"). |
| `LM-PC-7-PDP` | Rule 7 & Table 1 | Minimum Numerals & Letters Height | Computes required font height (1.0mm - 6.0mm) based on Principal Display Panel (PDP) area in cm². |

---

### 2.4 Multi-Source Benchmark Verification (`ProductIntelligenceService.kt`)
Cross-checks extracted declarations against official regulatory indexes and public databases:
* **FSSAI License Index:** Validates 14-digit FSSAI license numbers for food safety registration.
* **AGMARK & BIS Standards:** Checks certification codes for agricultural products and ISI packaging standard marks.
* **Price & Market Benchmark:** Cross-references package MRP against online price databases and national retail indexes to detect over-charging violations under Section 36(2).
* **Scan vs. Online Comparison:** Generates side-by-side verification rows with match indicators (`MATCH`, `MISMATCH`, `SCAN_ONLY`, `ONLINE_ONLY`).

---

### 2.5 Security, Role-Based Access Control (RBAC) & Audit Trail
* **User Roles:**
  * `USER`: Standard consumer mode with public product intelligence, scan history, and label inspection.
  * `ENFORCEMENT_OFFICER`: Official mode with badge verification, seizure order generation, station jurisdiction, rule customization, and tamper-evident PDF export.
* **Immutable Audit Trail (`AuditLogEntity`):** Logs every system action (login, scan creation, rule override, PDF export, re-inspection trigger) with timestamp, officer badge ID, and IP/device fingerprint.

---

## 3. Database Schema & Persistence

Managed via **Room Database (`AppDatabase.kt`)**:

1. `inspections` table (`InspectionEntity`)
2. `declarations` table (`DeclarationEntity`)
3. `compliance_checks` table (`ComplianceCheckEntity`)
4. `products` table (`ProductEntity`)
5. `rules` table (`RuleEntity`)
6. `audit_logs` table (`AuditLogEntity`)
7. `users` table (`UserEntity`)

---

## 4. Re-Inspection & Remediation Workflow
When non-compliance or label deficiencies are flagged:
1. Status transitions to `NON_COMPLIANT` or `NEEDS_REVIEW`.
2. Officer attaches evidence notes and issues a statutory correction/re-inspection notice.
3. System logs re-inspection status with linked initial inspection ID.
4. Corrective packaging re-scans evaluate whether previously flagged violations were rectified.
