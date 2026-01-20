# Ayy-Bay - Expense & Income Tracker 📊💰

A production-ready Android application built with **MVI Architecture**, **Jetpack Compose**, **Clean
Architecture**, and **Koin** dependency injection.

## 🎯 Features

- ✅ Track Income & Expenses with categorization
- ✅ Add, Edit, and Delete transactions
- ✅ Real-time monthly summary with balance calculations
- ✅ Clean, modern UI with Material 3 design
- ✅ Local data persistence using Room Database
- ✅ MVI (Model-View-Intent) architecture
- ✅ Clean separation of concerns with layered architecture

## 🏗 Architecture

This project follows **Clean Architecture** with **MVI pattern**:

```
com.ayybay.app
│
├── data                    # Data Layer
│   ├── local              # Room Database (Entities, DAO, Database)
│   ├── repository         # Repository implementations
│   └── mapper             # Entity ↔ Domain model mapping
│
├── domain                  # Domain Layer (Business Logic)
│   ├── model              # Domain models
│   ├── repository         # Repository interfaces
│   └── usecase            # Use cases (business operations)
│
├── presentation            # Presentation Layer
│   ├── screen             # Composable screens
│   ├── component          # Reusable UI components
│   ├── viewmodel          # MVI ViewModels
│   └── mvi                # UiState, UiIntent, UiEffect
│
├── di                      # Dependency Injection
│   ├── DatabaseModule     # Database provisioning
│   └── RepositoryModule   # Repository binding
│
└── ui/theme               # UI Theming
```

## 🧩 Tech Stack

| Component            | Technology                   |
|----------------------|------------------------------|
| Language             | Kotlin                       |
| UI Framework         | Jetpack Compose (Material 3) |
| Architecture         | MVI + Clean Architecture     |
| Dependency Injection | Koin                         |
| State Management     | StateFlow                    |
| Local Database       | Room                         |
| Coroutines           | kotlinx.coroutines           |
| Minimum SDK          | 24 (Android 7.0)             |
| Target SDK           | 36                           |

## 🎮 MVI Architecture

### UiState

Immutable data class representing the UI state:

```kotlin
data class TransactionUiState(
    val transactions: List<Transaction>,
    val isLoading: Boolean,
    val error: String?,
    val selectedMonth: Int,
    val selectedYear: Int
)
```

### UiIntent

Sealed class representing user intents:

```kotlin
sealed class TransactionUiIntent {
    object LoadTransactions
    data class AddTransaction(val transaction: Transaction)
    data class UpdateTransaction(val transaction: Transaction)
    data class DeleteTransaction(val transaction: Transaction)
    // ...
}
```

### UiEffect

Sealed class for side effects (navigation, toasts):

```kotlin
sealed class TransactionUiEffect {
    data class ShowToast(val message: String)
    data class NavigateToDetail(val transactionId: Long)
}
```

## 🔧 Key Components

### Data Layer

- **TransactionEntity**: Room database entity
- **TransactionDao**: Data access object for database operations
- **AppDatabase**: Room database configuration
- **TransactionRepositoryImpl**: Repository implementation

### Domain Layer

- **Transaction**: Domain model
- **TransactionRepository**: Repository interface
- **Use Cases**: Get/Add/Update/Delete operations

### Presentation Layer

- **TransactionViewModel**: MVI ViewModel with state management
- **HomeScreen**: Main screen with transaction list and summary
- **TransactionCard**: Individual transaction display component
- **AddTransactionDialog**: Dialog for adding/editing transactions

## 📱 UI Components

### Transaction Card

Displays transaction details with:

- Category name
- Description
- Date
- Amount (color-coded: green for income, red for expense)
- Edit and Delete actions

### Monthly Summary Card

Shows:

- Total income for the month
- Total expenses for the month
- Balance (income - expenses)
- Color-coded indicators

### Add Transaction Dialog

Form with:

- Transaction type selector (Income/Expense)
- Amount input
- Category input
- Description input
- Validation for required fields

## 🗃 Database Schema

### Transactions Table

```kotlin
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Date
)
```

## 🚀 Building the Project

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK 36

### Build Steps

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on emulator or device

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Install APK
./gradlew installDebug
```

## 📦 Dependencies

### Core

- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.activity:activity-compose`

### UI

- `androidx.compose:compose-bom`
- `androidx.compose.ui:*`
- `androidx.compose.material3:material3`

### Architecture

- `io.insert-koin:koin-core`
- `io.insert-koin:koin-android`
- `androidx.room:room-*`
- `androidx.navigation:navigation-compose`

### Coroutines

- `org.jetbrains.kotlinx:kotlinx-coroutines-*`

## 🧪 Testing

The project is structured to support:

- **Unit Tests**: Use cases, ViewModels, Mappers
- **Integration Tests**: Repository, Database
- **UI Tests**: Composable components, Navigation

## 🎨 Design Principles

- ✅ **SOLID Principles**: Single responsibility, dependency inversion
- ✅ **Immutability**: Data classes and state are immutable
- ✅ **Single Source of Truth**: StateFlow for state management
- ✅ **Separation of Concerns**: Clear layer boundaries
- ✅ **No Logic in UI**: Business logic in use cases
- ✅ **Proper Error Handling**: Try-catch with user feedback

## 📝 Usage

### Adding a Transaction

1. Tap the floating action button (+)
2. Select transaction type (Income/Expense)
3. Enter amount, category, and description
4. Tap "Add Transaction"

### Editing a Transaction

1. Tap the edit icon on any transaction card
2. Modify the details
3. Tap "Update Transaction"

### Deleting a Transaction

1. Tap the delete icon on any transaction card
2. Confirm deletion

### Viewing Monthly Summary

The summary card at the top displays:

- **Income**: Total income for current month
- **Expense**: Total expenses for current month
- **Balance**: Net balance (Income - Expense)

## 🔐 Permissions

The app uses standard Android permissions:

- No special permissions required
- All data is stored locally on device

## 🛡️ Privacy

- All data is stored locally on the device
- No network calls or data transmission
- No analytics or tracking
- Material 3 design system

## 🤝 Contributing

This is a production-ready template structure. To contribute:

1. Follow the existing architecture patterns
2. Maintain MVI pattern consistency
3. Write unit tests for new features
4. Update documentation

## 📄 License

This project is created as a demonstration of MVI architecture and Clean Architecture principles in
Android development.

## 👨‍💻 Author

Built as a production-ready Android application demonstrating industry-standard architecture
patterns.

## 🎮 Dependency Injection with Koin

### Why Koin?

**Koin** is a pragmatic lightweight dependency injection framework for Kotlin developers, offering:

- ✅ **Pure Kotlin**: No annotation processing needed
- ✅ **Zero Hazzle**: No code generation, no compilation overhead
- ✅ **Lightweight**: Minimal footprint (~300KB)
- ✅ **DSL-based**: Simple, Kotlin-friendly syntax
- ✅ **Fast Build Times**: No KSP or annotation processing
- ✅ **Easy to Learn**: Intuitive API with great readability

### Koin Module Structure

```kotlin
val appModule = module {
    
    // Database (Singleton)
    single { provideAppDatabase(androidContext()) }
    single { get<AppDatabase>().transactionDao() }
    
    // Repository (Singleton bound to interface)
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    
    // Use Cases (Factory - new instance each time)
    factory { GetAllTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetMonthlySummaryUseCase(get()) }
    
    // ViewModels (Scoped to Activity/Fragment)
    viewModel { TransactionViewModel(...) }
    viewModel { AyyBayViewModel(transactionViewModel = get()) }
}
```

### Scopes in Koin

- **`single`**: Singleton - one instance for the entire app
- **`factory`**: Factory - new instance each time it's requested
- **`viewModel`**: Scoped to the component lifecycle (Activity/Fragment)

### Getting Dependencies

**In any Class (Constructor Injection):**

```kotlin
class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao  // Koin injects this
) : TransactionRepository
```

**In Activity:**

```kotlin
class MainActivity : ComponentActivity() {
    // Koin injects the ViewModel
    private val viewModel by viewModel<AyyBayViewModel>()
}
```

**In Composable:**

```kotlin
@Composable
fun MyScreen(
    viewModel: AyyBayViewModel = viewModel() // Koin provides the ViewModel
) {
    // ...
}
```

---
**Ayy-Bay** - Track your expenses, grow your savings! 🚀