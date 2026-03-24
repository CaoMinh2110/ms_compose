You are implementing code for the **MoneySaving Android application**.

Follow all architecture, synchronization, and coding rules defined in **instruction.md** and **coding-rules.md**.

Your task is to generate Android code according to the design image I provide.

---

# Architecture Requirements

You must follow the project architecture strictly:

Clean Architecture
MVVM
Jetpack Compose UI

Do not bypass layers.

Data access must only occur through repositories.

UI must remain stateless and state must come from ViewModels.

---

# UI Implementation Requirements

The UI must match the **design image exactly**.

Requirements:

* Layout structure must match the image
* Spacing and alignment must follow the design
* Component hierarchy must reflect the visual structure
* Use reusable Compose components where possible

If the exact visual result cannot be reproduced:

You must still generate the **correct component tree structure**.

---

# Image and Icon Handling

If the design requires icons or images that are not available:

Do NOT invent assets.

Instead, insert a placeholder comment like this:

// icon placeholder - add asset here

or

// image placeholder - replace with actual asset

Example:

Icon(
painter = painterResource(...),
contentDescription = null
)

// icon placeholder - add asset here

---

# Component Structure

When generating UI:

Use this structure:

Screen
→ Section
→ Component

Example:

HomeScreen
TransactionSummarySection
TransactionItem

Avoid placing all UI inside one large Composable.

---

# Code Quality Requirements

Follow these rules:

* Use small composables
* Prefer stateless components
* Avoid business logic inside UI
* Use ViewModel for state management
* Use StateFlow for UI state

---

# Missing Information Handling

If any required information is missing, unclear, or ambiguous:

Do NOT guess.

You must ask clarifying questions before generating the code.

Examples of missing information:

* unclear UI structure
* unknown assets
* unknown data models
* missing interactions

---

# Output Format

When generating code:

1. Show the composable tree first
2. Then generate the Compose implementation
3. Then generate the ViewModel if required

---

# Important Rule

If something in the design cannot be implemented exactly:

You must still preserve the **correct component hierarchy** and insert placeholder comments for missing resources.

Always wait for my confirmation before rewrite any code.
