# UML Діаграми — Coffee Van

## 1. Use Case Diagram (Діаграма варіантів використання)

```mermaid
flowchart TD
    User(["👤 Користувач / Менеджер"])

    User --> UC1["Переглянути список кави"]
    User --> UC2["Додати товар до фургону"]
    User --> UC3["Редагувати товар"]
    User --> UC4["Видалити товар"]
    User --> UC5["Очистити фургон"]
    User --> UC6["Сортувати за ціна/вага"]
    User --> UC7["Знайти за рівнем якості"]
    User --> UC8["Фільтрувати список"]
    User --> UC9["Скасувати видалення (Undo)"]
    User --> UC10["Копіювати товар (Duplicate)"]
    User --> UC11["Налаштувати параметри фургону"]
    User --> UC12["Переглянути аналітику"]
    User --> UC13["Експортувати в CSV"]
    User --> UC14["Імпортувати з CSV"]

    UC2 --> UC_V["«include»\nПеревірити об'єм і бюджет"]
    UC3 --> UC_V
```

---

## 2. Class Diagram (Діаграма класів)

```mermaid
classDiagram
    class Coffee {
        <<abstract>>
        -int id
        -String name
        -double pricePerKg
        -double weightKg
        -double volumeLiters
        -PackagingType packagingType
        -int qualityScore
        +getCoffeeType() String
        +getTotalPrice() double
        +getPriceToWeightRatio() double
        +getId() int
        +getName() String
        ... getters/setters ...
        +equals(Object) boolean
        +hashCode() int
        +toString() String
    }

    class BeanCoffee {
        -String origin
        -String roastLevel
        +getCoffeeType() String
        +getOrigin() String
        +getRoastLevel() String
        +toString() String
    }

    class GroundCoffee {
        -String grindSize
        +getCoffeeType() String
        +getGrindSize() String
        +toString() String
    }

    class InstantCoffeeJar {
        -int granules
        +getCoffeeType() String
        +getGranules() int
        +toString() String
    }

    class InstantCoffeeSachet {
        -int sachetsCount
        +getCoffeeType() String
        +getSachetsCount() int
        +toString() String
    }

    class PackagingType {
        <<enumeration>>
        PACKAGE
        BAG
        BOX
        JAR
        BULK
        +getDisplayName() String
    }

    class Van {
        -double maxVolumeLiters
        -double maxBudget
        -double usedVolumeLiters
        -double spentBudget
        +canLoad(Coffee) boolean
        +load(Coffee) void
        +unload(Coffee) void
        +getRemainingVolume() double
        +getRemainingBudget() double
    }

    class VanService {
        -CoffeeDao coffeeDao
        -VanSettingsDaoInterface vanSettingsDao
        -Van van
        +addCoffee(Coffee) boolean
        +removeCoffee(Coffee) void
        +updateCoffee(Coffee, Coffee) void
        +getAllCoffee() List~Coffee~
        +sortByPriceToWeightRatio() List~Coffee~
        +findByQualityRange(int, int) List~Coffee~
        +filterBy(String, String, int, double) List~Coffee~
        +clearVan() void
        +updateVanSettings(double, double) void
    }

    class CoffeeDao {
        <<interface>>
        +save(Coffee) int
        +findById(int) Optional~Coffee~
        +findAll() List~Coffee~
        +update(Coffee) void
        +delete(int) void
        +deleteAll() void
    }

    class CoffeeDaoImpl {
        -Connection connection
        +save(Coffee) int
        +findById(int) Optional~Coffee~
        +findAll() List~Coffee~
        +update(Coffee) void
        +delete(int) void
        +deleteAll() void
    }

    class VanSettingsDaoInterface {
        <<interface>>
        +load() Van
        +save(Van) void
    }

    class VanSettingsDao {
        -Connection connection
        +load() Van
        +save(Van) void
    }

    class DatabaseManager {
        <<singleton>>
        -Connection connection
        +getInstance() DatabaseManager
        +getConnection() Connection
        +close() void
        -initSchema() void
    }

    class SmtpEmailNotifier {
        <<utility>>
        +sendCriticalAlert(String, String) void
    }

    Coffee <|-- BeanCoffee
    Coffee <|-- GroundCoffee
    Coffee <|-- InstantCoffeeJar
    Coffee <|-- InstantCoffeeSachet
    Coffee --> PackagingType

    VanService --> Van
    VanService --> CoffeeDao
    VanService --> VanSettingsDaoInterface

    CoffeeDaoImpl ..|> CoffeeDao
    VanSettingsDao ..|> VanSettingsDaoInterface

    DatabaseManager --> CoffeeDaoImpl : provides Connection
    DatabaseManager --> VanSettingsDao : provides Connection

    DatabaseManager ..> SmtpEmailNotifier : «uses on fatal error»
```

---

## 3. Database Schema (Схема бази даних)

```mermaid
erDiagram
    coffee {
        INTEGER id PK
        TEXT coffee_type
        TEXT name
        REAL price_per_kg
        REAL weight_kg
        REAL volume_liters
        TEXT packaging
        INTEGER quality_score
        TEXT extra1
        TEXT extra2
        TEXT extra3
    }

    van_settings {
        INTEGER id PK
        REAL max_volume
        REAL max_budget
    }
```

**Правила зберігання полів `extra1..3` для кожного підтипу кави:**

| Тип (`coffee_type`) | extra1 | extra2 | extra3 |
| :--- | :--- | :--- | :--- |
| `Зернова` | Походження (origin) | Рівень обсмаження (roastLevel) | NULL |
| `Мелена` | Розмір помолу (grindSize) | NULL | NULL |
| `Розчинна (банка)` | Кількість гранул (granules) | NULL | NULL |
| `Розчинна (пакетик)` | Кількість пакетиків (sachetsCount) | NULL | NULL |

---

## 4. Package Structure (Структура пакетів)

```
ua.lpnu.coffevan
├── App.java                     ← Точка входу (JavaFX Application)
├── model/
│   ├── Coffee.java              ← Абстрактний базовий клас
│   ├── BeanCoffee.java          ← Зернова кава
│   ├── GroundCoffee.java        ← Мелена кава
│   ├── InstantCoffeeJar.java    ← Розчинна (банка)
│   ├── InstantCoffeeSachet.java ← Розчинна (пакетик)
│   ├── Van.java                 ← Модель фургону
│   └── PackagingType.java       ← Enum типів упаковки
├── dao/
│   ├── CoffeeDao.java           ← Інтерфейс DAO для кави
│   ├── CoffeeDaoImpl.java       ← SQLite реалізація
│   ├── VanSettingsDaoInterface.java  ← Інтерфейс DAO налаштувань
│   ├── VanSettingsDao.java      ← SQLite реалізація налаштувань
│   └── DatabaseManager.java     ← Singleton-менеджер з'єднань
├── service/
│   └── VanService.java          ← Бізнес-логіка
├── ui/
│   ├── MainWindow.java          ← Головне вікно JavaFX
│   ├── CoffeeDialog.java        ← Діалог додавання/редагування
│   ├── SearchDialog.java        ← Діалог пошуку за якістю
│   ├── VanSettingsDialog.java   ← Діалог налаштувань фургону
│   └── AnalyticsDialog.java     ← Вікно аналітики
└── util/
    └── SmtpEmailNotifier.java   ← Email-нотифікатор про критичні помилки
```
