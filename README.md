# SOLID Kata (Java)

A hands-on refactoring kata to practice the five SOLID principles in Java using Maven and JUnit 5. The repository contains intentionally naïve implementations and a suite of tests that act as acceptance criteria for your refactors.

## What you will practice
- S — Single Responsibility Principle
- O — Open/Closed Principle
- L — Liskov Substitution Principle
- I — Interface Segregation Principle
- D — Dependency Inversion Principle

Each package in `src/main/java` contains a small example with smells. For each principle there is a corresponding test in `src/test/java` describing the expected behavior after your refactor.

## Prerequisites
- JDK 21+
- Maven 4.0+

Verify your setup:
- `java -version` → should show 21+
- `mvn -version` → Maven installed

## Getting started
1. Clone this repository.
2. Run all tests:
   - `mvn -q test`
3. Pick one principle at a time and refactor the production code until its test(s) pass.
4. Commit in small steps.

Tip: You can run an individual test class while working on a specific principle, for example:
- `mvn -q -Dtest=SrpRefactoTest test`

## Repository structure
- `src/main/java/srp` — SRP exercise (Invoice responsibilities)
- `src/main/java/ocp` — OCP exercise (discount calculation)
- `src/main/java/lsp` — LSP exercise (birds and flying)
- `src/main/java/isp` — ISP exercise (multifunction devices)
- `src/main/java/dip` — DIP exercise (notifications and senders)
- `src/test/java/...` — Refactoring tests per principle
- `pom.xml` — Maven configuration (Java 21, JUnit Jupiter)

## The exercises
Below is a short brief of the smells you will find and typical refactoring directions. Do not take this as the only solution; if the tests pass and the design aligns with the principle, you’re good.

### 1) SRP — Single Responsibility (package `srp`)
Current code: `Invoice` knows how to format, persist, and send itself.

Goal: Split responsibilities so each class has one reason to change.
- Keep `Invoice` focused on invoice data and domain behavior (e.g., `summary()`).
- Extract persistence into a separate collaborator (e.g., `InvoiceRepository` or `InvoiceFileStorage`).
- Extract communication into another collaborator (e.g., `EmailService`).
- Inject or pass collaborators instead of hard-coding side effects.

Acceptance: `SrpRefactoTest` should pass.

### 2) OCP — Open/Closed (package `ocp`)
Current code: `DiscountCalculator` switches on `customerType` string.

Goal: Make it easy to add new discount types without modifying existing code.
- Introduce a `DiscountPolicy` abstraction with implementations per customer type.
- Replace the `switch` with strategy lookup (map/registry/factory) based on type.
- Keep default behavior for unknown types clearly defined.

Acceptance: `OcpRefactoTest` should pass.

### 3) LSP — Liskov Substitution (package `lsp`)
Current code: `Bird` likely exposes a behavior not valid for all birds (e.g., `fly`), making `Ostrich` problematic.

Goal: Subtypes must be substitutable for their base without breaking expectations.
- Separate capabilities (e.g., split `Flyable` from `Bird`) so non-flying birds don’t inherit invalid behavior.
- Ensure clients relying on the base type don’t get runtime errors for a valid subtype.

Acceptance: `LspRefactoTest` should pass.

### 4) ISP — Interface Segregation (package `isp`)
Current code: `MultiFunctionDevice` forces `PhotoCopier` to implement unsupported operations (`fax`).

Goal: Prefer many client-specific interfaces over one fat interface.
- Split `MultiFunctionDevice` into narrow interfaces like `Printer`, `Scanner`, `Fax`.
- Have concrete devices implement only what they support.
- Compose devices when needed instead of forcing unused methods.

Acceptance: `IspRefactoTest` should pass.

### 5) DIP — Dependency Inversion (package `dip`)
Current code: `NotificationService` depends directly on concrete senders and string channels.

Goal: Depend on abstractions, not concretions.
- Introduce a `MessageSender` abstraction with `send(String)`.
- Provide implementations like `EmailSender`, `SmsSender`.
- Invert selection via injection or a factory/registry; avoid `new` and string switches in high-level policy.

Acceptance: `DipRefactoTest` should pass.

## Working style suggestions
- Change one principle at a time. Keep commits small and focused.
- Preserve behavior; use tests as a safety net.
- Prefer constructor injection for required dependencies.
- Keep public APIs stable unless tests indicate a required change.

## Running tests
- All tests: `mvn -q test`
- One test class: `mvn -q -Dtest=IspRefactoTest test`
- One test by name: `mvn -q -Dtest=OcpRefactoTest#should_apply_vip_discount test`

## FAQ
- Q: Can I introduce new classes and interfaces?  
  A: Yes—especially to express roles/abstractions for each principle.
- Q: Can I change method signatures?  
  A: If the tests rely on them, prefer adding overloads/adapters instead of breaking changes.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

Copyright (c) 2025 java-solid-kata contributors

---
Have fun, refactor safely, and focus on clarity over cleverness!