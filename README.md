# 📊 Performance Analyzer for Java Collections
⠀
## 🧪 Try It Online
⠀
🚀 [Click here to run the project in JDoodle](https://www.jdoodle.com/ga/GldE87XXbS0i6tJ9%2BuD3RA%3D%3D)
<br></br>
## 📘 Description

This project is a command-line based tool for **benchmarking the performance of various Java Collection types** using different data and test types. It was created as part of an academic assignment, following strict guidelines: enums for configuration, interfaces for structure, DRY principle(tried to make it), and clean modular architecture.

The application allows users to:
- Choose collection type (List / Set)
- Choose data type (Primitive or Object)
- Select a test (access, insert/remove, search)
- Pick output format (Console or CSV)
- Use CLI arguments or interactive mode

---

## ⚙️ Features
- `No third-party libraries used`
  
- `You can put arguments in ANY way,there is a generic class that allows us to do so`
  
- `Can be easily extended with more collections, test types, or data objects`

### ✅ Data Types
- `Integer`
- `Double`
- `Person` — with `String` name and `int` year of birth
- `MyColor` — with RGB fields and their sum
- `Subject` — with name and ID
- `Game` — with name and genre

All objects are generated via **Java Stream API** and use custom `.equals()` / `.hashCode()` / `.compareTo()` implementations when needed.

---

### ✅ Supported Collections
- `ArrayList`
- `LinkedList`
- `HashSet`
- `TreeSet`

---

### ✅ Test Types
- `ByIndex` — access by index (Lists only)
- `IRFrequency` — insert and then remove all elements
- `Searching` — search for a known value
- `IsInColl` — search for a potentially non-existent value

⚠️ If `ByIndex` is selected with a Set, the program stops with warning.

---

### ✅ Output Options
- `Console` — formatted output in terminal
- `CSV` — results saved to `test_result.csv` (auto-versioned)

Example CSV:
```csv
Index-based access
0,000002200 sec
```
---

## 📦 Usage
### ▶️ 1. CLI Mode
**Using numbers (1,2,3...)**
It looks like this:
```csv
Please enter which CollectionType you want to perform on:
1.ArrayList
2.LinkedList
3.HashSet
4.TreeSet
```

### ▶️ 2. Terminal Mode
You can run the program directly with **five arguments**, in **any order**, for instance:

```bash
java PerformanceAnalyzerForJavaCollections CollectionType DataType Number TestType OutputFormat
```

**📄 Example CSV Output**
```csv
Insertion;Removing
0.001000000 sec;0.002000000 sec
```

<h2>Final</h2>

<p align="center">
  <strong>🙏 THANK YOU FOR READING THIS!</strong><br><br>
  <img src="https://media1.giphy.com/media/ghCH2X0XYC9LTDf1KG/giphy.gif" alt="genius gif">
</p>

📚 This project was created for a university assignment and reflects the course’s practical requirements for Java development such as **ENUMS, Collections, Streams, Generics, Interfaces**

PS it was required that the program be in one file:>


