# MoneySaving – Android App System Specification

## Project Overview

Develop an **Android personal finance management app** that helps users track **income, expenses, loans, and budgets**.

The system must allow users to:

- Manage financial transactions
- Create and manage budgets
- Analyze financial data with statistics
- Filter financial data before a selected time point (day, week, month, year)
- Configure preferred language and currency
- Use the app fully **offline**
- Optionally **sync data with cloud**

The application must be designed for **scalability and maintainability**.

---

# Technology Requirements

Mandatory stack:

- **Language:** Kotlin (latest stable)
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM
- **Dependency Injection:** Hilt
- **Local Database:** Room
- **Remote Backend:** Firebase Authentication + Cloud Firestore
- **Async Processing:** Coroutines + Flow
- **Navigation:** Navigation Compose
- **Settings Storage:** DataStore
- **Charts:** MPAndroidChart 

---

# Authentication System

The application must support optional user authentication using **Google Sign-In via Android Credential Manager**.

Authentication must use:

- Android Credential Manager
- Google ID Token
- Firebase Authentication

Users may choose to:

- Sign in with Google
- Use the application without signing in

If the user does not sign in, the application must still function fully in **offline mode**.

---

## Login Flow

1. Request Google credentials using Credential Manager.
2. Retrieve the **Google ID Token**.
3. Send the ID Token to **Firebase Authentication**.
4. Firebase verifies the token and returns a **Firebase user session**.

When login succeeds, the application must obtain the **Firebase UID**.

---

## First-Time Login Behavior

When a user logs in for the first time:

1. Authenticate using Firebase Authentication.
2. Retrieve the Firebase `uid`.
3. Check if a user document exists in Firestore.
4. If not, create a new document.

Firestore collection: users

Document ID: {firebaseUid}

User profile fields:

email: String
name: String
avatar: String
createdAt: Timestamp


The `name` and `avatar` values must be retrieved from the Google account profile.

---

## Authentication State Handling

The application must observe the Firebase authentication state.

If the user is already authenticated:

- automatically enable cloud synchronization.

If the user logs out:

- cloud synchronization must stop
- the application continues operating using **local Room data only**.

# Core Functional Requirements

## 1. Transaction Management

The application must allow users to:

- Create transactions
- Edit transactions
- Soft delete transactions
- Assign transactions to predefined categories
- Optionally associate transactions with a budget
- Add optional notes or references (for example loan-related information)

Transactions must support **time-based filtering** for search, analytics and reporting.

Transaction type must **not be stored directly** and must instead be **derived from the category type**.

---

## 2. Budget Management

The system must support:

- Creating budgets
- Defining budget periods
- Associating transactions with budgets
- Tracking spending within budget periods

Budget usage must be **calculated dynamically from related transactions**.

Budget must support **time-based filtering** for search, analytics.

The database must **not store calculated values such as used amount**.

The budget UI is represented by an icon with a random color from the default color list at initialization.

---

## 3. Statistics and Insights

The application must provide financial insights including:

- Spending by category
- Income vs expense overview
- Budget usage
- Time-based analysis

All statistics must support filtering by the selected month of the year.

Charts must visualize aggregated transaction data.

---

## 4. Offline-First Operation

The application must be designed with an **offline-first architecture**.

Rules:

- Local database (**Room**) is the **source of truth**
- All features must work without internet
- Network availability should only affect cloud synchronization

---

## 5. Optional Cloud Synchronization

Cloud functionality must use:

- **Firebase Authentication**
- **Cloud Firestore**

Users must be able to enable or disable cloud synchronization.

When synchronization is disabled:

- All features must still work locally
- No cloud operations should occur

Cloud synchronization can only be enabled when the user is authenticated.

If the user signs in successfully:

- synchronization may be enabled
- local data must be synchronized with Firestore.

If the user signs out:

- synchronization must stop immediately.

---

# Synchronization Strategy

The application must synchronize data between the local Room database and Firestore.

Room remains the **single source of truth**.

Synchronization must be **bidirectional**.

Only user-defined models must be synchronized.

System-defined models such as Category, Language, and Currency must never be synchronized to Firestore.

---

## Push Synchronization (Local → Cloud)

Local records must be pushed to Firestore when:

If the remote document does not exist, create it.

If the document exists and local.updatedAt > remote.updatedAt, update the remote document.

---

## Pull Synchronization (Cloud → Local)

Remote records must update the local database when:

remote.updatedAt > local.updatedAt

If the record does not exist locally, insert it.

If the record exists and remote.updatedAt > local.updatedAt, update the local record.

---

## Conflict Resolution

Conflicts must be resolved using a **last-write-wins strategy** based on:

updatedAt

The record with the newest timestamp must overwrite the older version.

---

## Deleted Records

Deleted records must use soft deletion:

isDeleted = true

Deleted records must still be synchronized between devices.

---

## Synchronization Trigger

Synchronization may occur:

- when the user enables cloud sync
- when the application starts
- Synchronization must only occur when network connectivity is available.
If the device is offline, synchronization must be postponed.

---

# Firestore Data Structure

All synchronized user data must be stored under the authenticated user's document.

Structure:
users
 └── {uid}
      email
      name
      avatar
      createdAt
      ├── transactions (collection)
      │     └── {transactionId}
      └── budgets (collection)
            └── {budgetId}

Rules:

- All financial data must be scoped to the authenticated user.
- No global collections for transactions or budgets are allowed.
- This ensures complete user data isolation.

# Data Model Classification

The system must clearly separate two types of data models.

---

## 1. User-Defined Models

These models represent data created and owned by the user.

Characteristics:

- Stored in Room
- Eligible for cloud synchronization
- Created, edited, and deleted by users

User-defined models include:

- Transaction
- Budget

Field structures are intentionally **not predefined in this specification** and should be designed during implementation.

However, the following constraints must be respected:

- Transactions must store categoryId referencing a predefined category.
- Transactions must support soft deletion
- Synchronization must rely on timestamps for conflict resolution
- Transaction type must be derived from category type
- Transactions may reference a budget using budgetId.
- Transactions must store budgetId referencing a predefined budget. If a referenced budget does not exist locally, the system must set budgetId to null when importing data.

All user-defined models must include the following synchronization fields:

id: String (UUID)
createdAt: Timestamp
updatedAt: Timestamp
isDeleted: Boolean

Deletion must be implemented using soft deletion by setting:

isDeleted = true

All queries must ignore records where isDeleted = true.

---

## 2. System-Defined Models

These models are **predefined by the system** and must remain **read-only**.

Characteristics:

- Not created by users
- Not editable
- Not synced to cloud
- Stored locally or defined statically

The selected language and currency must be exposed as reactive application settings.

UI components must automatically update when these values change.

The settings must be observable across the entire application lifecycle.

---

### Category

Categories represent financial classifications.

Properties:

- Predefined list
- Includes icon and color
- Has a fixed type

Category types:

- EXPENSE
- INCOME
- LOAN

Rules:

- Users cannot edit categories
- Users cannot delete categories
- Categories are not synced to Firestore

---

### Language

Represents supported application languages.

Supported languages:

- English (`en`)
- Vietnamese (`vn`)

Rules:

- Selected language code must be stored in **DataStore**
- No Room storage
- No cloud synchronization

---

### Currency

Represents supported currencies.

Example currencies:

- USD
- VND

Rules:

- Currency list is predefined
- Only selected currency code is stored in **DataStore**
- No Room storage
- No cloud synchronization

---

# **Application Startup Flow**

### **First Launch Flow**

1. `IntroScreen`
2. `LanguageScreen`
3. `CurrencyScreen`
4. `AuthenticationScreen` (optional login)
5. `OnboardingBudgetScreen` (only displayed if the user is not logged in)
6. `HomeScreen`

### **Subsequent Launches**

* If the user is logged in:

* If the user has synchronization enabled: → activate automatic synchronization.

* If the user is not logged in: → run in **local-only** mode.

> **Note:** The application always starts from the `HomeScreen` after initialization.

---

# **Navigation Destinations**

The application has **5 main navigation destinations**:

1. Home
2. Statistics
3. Transaction Editor
4. Budget
5. Settings

---

# **Home Flow**

* `HomeScreen` (click option)

→ `TransactionDetailScreen` (click transaction item)

→ `TransactionSearchScreen` (click search bar)

→ `TransactionDetailScreen` (click transaction item)

**Behavior Notes:**

* When navigating to `TransactionSearchScreen` → use **SharedElementTransition** for the search bar.

* Transaction lists can be filtered by **type** and **time**; data filtered by type can be **cached**.

---

# **Statistics Flow**

* `StatisticsScreen` (placeholder, can be developed later)

---

# **TransactionEditor Flow**

* Activated from FAB (Floating Action Button)

* All actions **navigate back** to the previous destination.

* `TransactionEditorScreen` can be reused for **editing transactions**.

---

# **Budget Flow**

* `BudgetListScreen` (click option)

→ `BudgetDetailScreen` (click budget item)

→ `BudgetEditorScreen` (click Add button)

**Behavior Notes:**

* `BudgetEditorScreen` is a **dialog**, consisting only of a simple input form.

---

# **Settings Flow**

* `SettingsScreen` (click option) 
→ `AccountScreen` (click header arrow button) 
→ `LanguageScreen` (click language) 
→ `CurrencyScreen` (click currency) 
→ `SyncScreen` (toggle sync changed) 
→ `RateScreen` (click language) 
→ `CurrencyScreen` (click currency)

**Behavior Notes:**

* `SyncScreen` and `RateScreen` are **dialog**.
* `RateScreen` uses emoticons with text: `😊, 😭, 😢, 😟, 😊, 😍`.
* `SyncScreen` is only called when **sync is actually necessary**.
* Hide the Arrow Button header when the user is not logged in.
* `Log out` button text changes to `Log in` after user logs out successfully. And this button function changes to log in.

---

# Typography

## Colors

| Name       | Hex      | Usage |
|------------|----------|-------|
| Primary    | #333333  | Main text |
| Secondary  | #939393  | Hint, placeholder text, loan type |
| Title      | White    | Titles, headers (based on images) |
| Red        | #DC2626  | Error messages, negative prices |
| Green      | #64D852  | Positive prices |
| Blue       | #4F80FC  | Budget prices |

---

## Font Sizes & Weights

| Weight / Style       | Size | Usage |
|---------------------|------|-------|
| Bold                | 20sp  | Main titles, headers |
| Semibold → Regular  | 14sp  | Secondary text, input labels |
| Medium → Small      | 11sp  | Hints, placeholders, minor labels |

---

## Usage Notes

- **Primary color** → dùng cho text chính  
- **Secondary color** → dùng cho hint, placeholder, thông tin phụ  
- **Red / Green / Blue** → dùng để nhấn mạnh trạng thái giá tiền hoặc cảnh báo  
- Kết hợp typography và màu sắc để đảm bảo **tính rõ ràng và dễ đọc** trong UI  