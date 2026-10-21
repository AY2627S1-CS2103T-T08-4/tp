---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Sieve Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* Sieve is based on the
  [AddressBook-Level3 project](https://github.com/se-edu/addressbook-level3)
  created by the [SE-EDU initiative](https://se-education.org). Parts of Sieve's code and documentation were adapted
  from AddressBook-Level3.
* Sieve's UI uses the [Inter](https://github.com/rsms/inter) font by Rasmus Andersson and the Inter Project Authors,
  licensed under the [SIL Open Font License 1.1](https://openfontlicense.org). A copy of the license is included in
  `src/main/resources/fonts/Inter-LICENSE.txt`.
* Sieve uses [JavaFX](https://openjfx.io/) for its graphical user interface.
* Sieve uses [Jackson](https://github.com/FasterXML/jackson) for JSON data storage.
* Sieve uses [JUnit 5](https://junit.org/junit5/) for automated testing.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [
`Main`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/Main.java) and [
`MainApp`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in
charge of the app launch and shut down.

* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues
the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API
  interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other
components interact with a component through its interface rather than its concrete class, preventing them from coupling
to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [
`Ui.java`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and
`StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common
behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in
`src/main/resources/view`. For example, [
`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/resources/view/MainWindow.fxml)
specifies the layout of [
`MainWindow`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The look of the UI is defined in `.css` files in the same folder:

* `LightTheme.css` defines the colour palette once, as looked-up colours on `.root` (e.g. `-sieve-accent`), and every
  other rule refers to these names. To change a colour, change it in `.root` only. Each window is a separate JavaFX
  `Scene` with its own stylesheets, so both `MainWindow.fxml` and `HelpWindow.fxml` load `LightTheme.css`.
* `LightTheme.css` also loads the bundled Inter font from `src/main/resources/fonts` with `@font-face`, and sets it on
  `.root`, so all text uses it on every platform. JavaFX registers the SemiBold font as a separate family, so rules
  that need it use `-fx-font-family: "Inter SemiBold"` rather than `-fx-font-weight`.
* `Extensions.css` adds a few rules to the main window, such as the background of the empty rows below the last client.

A few parts of the main window are worth knowing about when changing them:

* **Command results.** `MainWindow` shows the result of a command with `ResultDisplay#setFeedbackToUser(String)`, or
  with `ResultDisplay#setErrorToUser(String)` if the command failed. `ResultDisplay` turns its `:error` pseudo-class on
  or off to match, and `LightTheme.css` uses it to show a green tick or a red cross and red text. The icons are empty
  `Region`s shaped with `-fx-shape`, so no image files are needed. The result line is hidden until the first command.

* **Tag colours.** `PersonCard` gives each tag a style class from `TagColor`, from `tag-color-0` to `tag-color-11`.
  `TagColor` picks it from the tag's name, ignoring case, so a tag has the same colour on every client and every
  launch. `LightTheme.css` has one `#tags .tag-color-N` rule for each class. To add a colour, add a rule and raise
  `TagColor.PALETTE_SIZE` to match.
* **Client count.** `PersonListPanel` binds the text of the count above the list to the displayed client list, which
  is the `Model`'s filtered list. The count therefore updates after any command that changes or narrows down the
  list, such as `delete` or `find`, without the commands having to update it.

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [
`Logic.java`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API
call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of
PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in
   turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which
   is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several
   interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.
1. **Read-only mode block:** If a `DataLoadingException` occurs during file parsing, `LogicManager` activates its
   internal `isReadOnlyMode` flag. When this flag is true, `LogicManager#execute()` intercepts any incoming
   data-mutating commands (such as `add`, `delete`, `edit`, `clear`, or `tag`) before parsing them, immediately throwing
   a `CommandException` to protect the hard disk storage files from being accidentally overwritten. Non-mutating
   commands like `list` and `help` remain fully operational. Here are the other classes in `Logic` (omitted from the
   class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:

* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a
  placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to
  parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that
  object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface
  so they can be treated similarly where appropriate, for example during testing.

### Model component

**API** : [
`Model.java`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It
  exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates
  when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed
  to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they
  should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and
each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead
of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>

### Storage component

**API** : [
`Storage.java`](https://github.com/AY2627S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,

* can save both address book data and user preference data in JSON format, and read them back into corresponding
  objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and
  `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects
  that belong to the `Model`)
* handles storage data file integrity by propagating the `DataLoadingException` context up to the main application layers instead of swallowing loading failures quietly, preventing Sieve from automatically replacing invalid JSON configurations with blank template resets.


### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Find feature

`find` filters the displayed client list by name keywords, phone numbers and tags. `FindCommandParser` validates the input and builds a `PersonMatchesSearchPredicate`, which decides which clients match.

**Parsing.** `FindCommandParser` tokenizes the arguments on `p/` and `t/`. The preamble holds the name keywords. Phone numbers are parsed with `ParserUtil.parsePhone` (so `9123 4567` becomes `91234567`) and tags with `ParserUtil.parseTag`, so `find` accepts and rejects exactly what `add` does. At least one keyword, phone number or tag must be given.

**Combining fields.** `PersonMatchesSearchPredicate` holds the name keywords, a set of `Phone`s and a set of `Tag`s. A client must match every field that has values (AND across fields), and any one value within a field (OR within a field). A field with no values places no restriction. Phones are compared with `Phone#equals`, and tags with `Tag#equals`, which ignores case. Name keywords are delegated to `NameContainsKeywordsPredicate`, described below.

**Name keyword validation.** `Name` exposes the characters allowed in a name as `Name.NAME_CHARACTERS` (letters, digits and the punctuation in `Name.NAME_PUNCTUATION`: `'`, `.`, `(`, `)` and `-`). `Name.VALIDATION_REGEX` and `FindCommandParser`'s keyword regex are both built from this constant, so `find` always accepts the same characters as names. A keyword must also contain at least one letter or digit, so keywords such as `-` are rejected.

**Name matching.** For each client, `NameContainsKeywordsPredicate` builds a set of lower-case name parts:

* each space-separated word with punctuation removed (`O'Brien` → `obrien`), and
* each piece of a word split on punctuation (`O'Brien` → `o`, `brien`).

Each keyword is lower-cased with punctuation removed, and a client matches if any keyword is a prefix of one of their name parts. For example, the parts of `Liam O'Brien` are `liam`, `obrien`, `o` and `brien`, so `find O'Brien`, `find obr` and `find Bri` all match, but `find rien` does not. Keywords that are empty after punctuation is removed never match.

If no client matches, `FindCommand` returns `FindCommand.MESSAGE_NO_CLIENTS_FOUND`, which explains the matching rule and suggests `list`.

#### Design considerations:

**Aspect: How a keyword matches a name**

* **Alternative 1 (current choice):** A keyword must be the start of a word or of a part of a word, ignoring case and punctuation.
  * Pros: Tutors can type the first few letters of a name (`Ami` for `Amirah`) and still find clients with hyphenated or apostrophised names. Matching only the start of a part keeps results close to what the tutor typed.
  * Cons: A very short keyword such as `a` matches many clients, and a keyword that joins two words (`Aisyah-Tan`) does not match `Nur-Aisyah Tan`.

* **Alternative 2:** A keyword must equal a whole word or a whole part of a word (the behaviour before prefix matching was added).
  * Pros: Results are the most predictable.
  * Cons: Tutors must type the full name part, so `Ami` does not find `Amirah`.

* **Alternative 3:** A keyword matches if it appears anywhere in the name (substring matching).
  * Pros: Requires the least typing.
  * Cons: Keywords match the middle of unrelated names (`an` matches `Tan`, `Brandon` and `Farhan`), which defeats the purpose of searching a large roster.

* **Alternative 4:** Whole space-separated words only (the original AB3 behaviour).
  * Pros: Simplest to implement.
  * Cons: Clients such as `O'Brien` or `Nur-Aisyah` cannot be found by part of their name.

**Aspect: How fields are combined**

* **Alternative 1 (current choice):** AND across fields, OR within a field.
  * Pros: Adding a field narrows the results, e.g. `find Liam t/sec4` finds the Liam in Sec 4 among several Liams. Keeps the existing OR behaviour between name keywords.
  * Cons: Users must run two searches to see clients matching a name *or* a tag.

* **Alternative 2:** OR across all fields.
  * Pros: One rule for everything.
  * Cons: Adding a field widens the results, so a tutor cannot narrow down a large roster.

### Undo/redo feature

#### Implementation

The `undo` command reverts the client list to its state before the last change, and the `redo` command reapplies a change that was undone. Changes can be undone one at a time, back to the client list at startup.

The history is kept by `VersionedAddressBook`, which extends `AddressBook` with three fields:

* `undoHistory`: a stack (`Deque`) of earlier client lists, with the most recent on top.
* `redoHistory`: a stack of client lists that were undone, with the most recently undone on top.
* `current`: a copy of the client list as it was at the last save to the history (or at startup).

It adds the following operations, which are exposed in the `Model` interface under the same names:

* `VersionedAddressBook#commitAddressBook()`: pushes `current` onto `undoHistory`, empties `redoHistory`, and saves a copy of the client list as `current`. Does nothing if the client list still matches `current`.
* `VersionedAddressBook#undoAddressBook()`: pushes `current` onto `redoHistory`, then pops the top of `undoHistory` and restores the client list to it.
* `VersionedAddressBook#redoAddressBook()`: pushes `current` onto `undoHistory`, then pops the top of `redoHistory` and restores the client list to it.
* `VersionedAddressBook#canUndoAddressBook()` and `VersionedAddressBook#canRedoAddressBook()`: return whether the matching stack has anything in it.

`LogicManager` calls `Model#commitAddressBook()` after every command that succeeds, so commands do not need to know about undo.

The example below shows how the history changes. In each diagram, `undoHistory` is on the left (oldest first), `current` is in the middle, and `redoHistory` is on the right (next to be redone first).

Step 1. The user launches the application. Both stacks are empty, and `current` is a copy of the starting client list, `ab0`.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` to delete the 5th client. After it succeeds, `LogicManager` calls `Model#commitAddressBook()`. The client list no longer matches `current`, so `ab0` is pushed onto `undoHistory` and the new client list is saved as `current` (`ab1`).

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new client. The commit after it pushes `ab1` onto `undoHistory`, and `ab2` becomes `current`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails, it throws an exception before `LogicManager` calls `Model#commitAddressBook()`, so nothing is saved to the history.
</box>

Step 4. The user executes `list`. `LogicManager` calls `Model#commitAddressBook()` as usual, but the client list still matches `current`, so the history is unchanged. This is why `list`, `find` and `help` do not use up an undo.

Step 5. The user decides that adding David was a mistake, and executes `undo`. `UndoCommand` calls `Model#undoAddressBook()`, which pushes `ab2` onto `redoHistory`, pops `ab1` from `undoHistory`, makes it `current`, and copies it into the client list. `UndoCommand` then shows the full client list, so that a restored client is not hidden by an earlier `find`.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />

<box type="info" seamless>

**Note:** If `undoHistory` is empty, there is no earlier client list to restore. `UndoCommand` checks this with `Model#canUndoAddressBook()` and shows an error instead of undoing.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component. The commit after `undo` does nothing, because `undoAddressBook()` has already set `current` to match the client list. This is also why the commit does not empty `redoHistory`.

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it
continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

Step 6. The user changes their mind and executes `redo`. `RedoCommand` calls `Model#redoAddressBook()`, which pushes `ab1` back onto `undoHistory`, pops `ab2` from `redoHistory`, makes it `current`, and copies it into the client list. The history is now the same as after Step 3. `redo` goes through the `Logic` and `Model` components in the same way as `undo`, with the two stacks swapped.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

<box type="info" seamless>

**Note:** If `redoHistory` is empty, there is no undone client list to restore. `RedoCommand` checks this with `Model#canRedoAddressBook()` and shows an error instead of redoing.
</box>

Step 7. The user executes `undo` again, which returns to the history in Step 5, and then executes `clear`. The commit after `clear` pushes `ab1` onto `undoHistory`, makes the empty client list (`ab3`) `current`, and empties `redoHistory`. `ab2` is no longer stored anywhere, so it can no longer be redone.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens to the history when a user executes a command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

<box type="warning" seamless>

**Caution:** A change to the client list only becomes its own undo step when `Model#commitAddressBook()` is called after it. A change made outside a command (e.g. by a future import feature) must call `Model#commitAddressBook()` itself, otherwise it will be merged into the undo step of the next command.
</box>

The history is kept in memory only, so it is lost when the app is closed. In read-only mode (when the data file is corrupted), `undo` and `redo` are blocked along with the other commands that change data.

#### Design considerations:

**Aspect: How undo executes:**

* **Alternative 1 (current choice):** Saves the entire client list after each change.
  * Pros: Easy to implement, and works for every command, including new ones, without extra code.
  * Cons: Uses more memory, as each change stores a full copy of the client list. This is acceptable for a tutor's client list of tens of clients.

* **Alternative 2:** Each command knows how to undo and redo itself.
  * Pros: Uses less memory (e.g. for `delete`, just save the client being deleted).
  * Cons: Every command that changes data needs its own undo and redo logic, and each must be correct.

**Aspect: Where the history is saved:**

* **Alternative 1 (current choice):** `LogicManager` calls `Model#commitAddressBook()` after every successful command, and `commitAddressBook()` skips unchanged client lists.
  * Pros: Commands do not need to know about undo, and a new command is undoable automatically.
  * Cons: Compares the client list with `current` after every command, including commands that change nothing.

* **Alternative 2:** Each command that changes data calls `Model#commitAddressBook()` itself.
  * Pros: No comparison is needed, as only commands that change data commit.
  * Cons: A new command that forgets the call silently cannot be undone, and `LogicManager` cannot tell whether a command changed anything.

**Aspect: How many levels of undo:**

* **Alternative 1 (current choice):** Multiple levels, using a stack of earlier client lists.
  * Pros: The user can undo several mistakes in a row, and `redo` only needs a second stack for the undone client lists.
  * Cons: The history grows with every change until the app is closed.

* **Alternative 2:** A single level, keeping only the client list before the last change.
  * Pros: Simplest to implement, with a fixed memory cost.
  * Cons: The user can only undo their last change, and a second mistake cannot be recovered.

**Aspect: What happens to undone changes after a new change:**

* **Alternative 1 (current choice):** A new change empties `redoHistory`.
  * Pros: The history stays a single line of changes, which is easy for users to predict and matches most other apps.
  * Cons: Undone changes are lost once the user makes a new change.

* **Alternative 2:** Keep every branch of the history, so undone changes can still be reached.
  * Pros: Nothing is ever lost.
  * Cons: Much harder to implement, and users would need a way to choose which branch to redo.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a self-employed private tuition teacher
* works independently
* manages a large and growing roster of clients
* teaches clients across varying subjects and education levels
* manages irregular and frequently changing schedules

**Value proposition**: Tutors managing many clients with different subjects, levels, and constantly shifting schedules
often lose track of details across scattered notes or apps. Sieve keeps every client organised in one place and lets
tutors instantly find or filter anyone, keeping admin work out of the way of teaching.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ...                               | I can ...                                                                                             | So that ...                                                                             |
|----------|----------------------------------------|-------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `* * *`  | potential user exploring the app       | see the app populated with sample data                                                                | I can easily see how the app will look when it is in use                                |
| `* * *`  | user ready to start using the app      | purge all current data                                                                                | I can get rid of sample/experimental data I used for exploring the app                  |
| `* * *`  | new user                               | add a client with their name, phone, email, and address                                               | I have their contact details on record                                                  |
| `* * *`  | forgetful user                         | see an error message that shows the correct format and which part of my command was wrong             | I can fix it immediately instead of guessing                                            |
| `* * *`  | new user                               | see a list of all commands and their formats                                                          | I can get started without feeling lost or leaving the app to look up syntax             |
| `* * *`  | tutor                                  | edit the details of an existing client                                                                | I can fix or update information without retyping the whole entry                        |
| `* * *`  | tutor                                  | find a client by name keyword                                                                         | I can pull up their details immediately when needed                                     |
| `* * *`  | tutor                                  | delete a client I no longer tutor, e.g. a student who has graduated                                   | I can maintain an updated and clean list                                                |
| `* * *`  | tutor                                  | tag a client with free-form labels, and add or remove a single tag without retyping the whole contact | I can group clients the way my business actually works and keep small corrections cheap |
| `* * *`  | tutor                                  | see all my clients as a numbered list                                                                 | I can refer to any of them by index in later commands                                   |
| `* * *`  | careless typist                        | undo my last command                                                                                  | a mistyped delete isn't a disaster                                                      |
| `* *`    | careless typist                        | redo a change I undid                                                                                 | undoing one step too far is easy to fix                                                 |
| `* *`    | new user switching from other tools    | bulk-add multiple clients at once                                                                     | I don't have to re-enter my existing client list one by one                             |
| `* *`    | tutor                                  | filter clients by one or more tags, such as day, subject, and level, in a single command              | I can answer questions like "Sec 4 physics on Thursday" in one go                       |
| `* *`    | tutor                                  | mark a client as inactive (e.g. paused for exams or a break) instead of deleting them                 | I keep their record and can resume lessons later without re-adding them from scratch    |
| `* *`    | long-time user                         | hide inactive clients from my main list and see only the clients I am currently teaching              | I am not distracted by irrelevant data and can quickly see who is still active          |
| `* *`    | tutor                                  | add free-text notes to a client                                                                       | I can record details that don't fit neatly into a tag                                   |
| `* *`    | tutor                                  | export my client list to a file                                                                       | I can back it up or move it to a new computer                                           |
| `* *`    | tutor considering a new client         | check whether a proposed time slot clashes with an existing client's schedule                         | I avoid double-booking myself                                                           |
| `* *`    | tutor                                  | record whether a client has paid for the current month                                                | I know who has paid and who is still outstanding, even for parents who pay at month-end |
| `* *`    | tutor                                  | see which clients have outstanding payments at a glance                                               | I don't have to check each client individually                                          |
| `*`      | tutor                                  | link a client to a family member who is also my client, such as a sibling                             | I can remember which students belong to the same household                              |
| `*`      | tutor with limited time                | sort or flag clients by priority                                                                      | I know who to focus on when my own schedule is tight                                    |
| `*`      | tutor                                  | see a quick count of my active clients                                                                | I can gauge my current workload at a glance                                             |
| `*`      | tutor                                  | detect and remove duplicate client entries                                                            | my list doesn't contain confusing repeats                                               |
| `*`      | expert user                            | create shortcuts for tasks                                                                            | I can save time on frequently performed tasks                                           |
| `*`      | tutor                                  | have overdue payments flagged automatically                                                           | I don't have to manually track payment deadlines for every client                       |
| `*`      | long-time tutor                        | view a family's history with me across multiple children                                              | I can build rapport when starting with a new sibling                                    |
| `*`      | user with low confidence in technology | get confirmation that my data has been saved successfully                                             | I trust the app is keeping my records safely                                            |
| `*`      | tutor who runs group classes           | record a single session covering multiple students at once                                            | I don't have to repeat the same entry per student                                       |
| `*`      | tutor who runs group classes           | see which enrolled students are attending a specific session                                          | I know who to expect and who is away                                                    |
| `*`      | tutor who runs group classes           | bill a group class as a single unit                                                                   | I don't have to track payment separately for each student in it                         |

### Use cases

(For all use cases below, the **System** is `Sieve` and the **Actor** is the `tutor`, unless specified otherwise)

**Use case: UC1 - Add a client**

**MSS**

1. Tutor requests to add a client by providing the name, phone number, email address and address
2. Sieve validates the details
3. Sieve adds the client to the end of the client list
4. Sieve saves the updated client list to the data file
5. Sieve shows a confirmation message and the new client

   Use case ends.

**Extensions**

* 1a. A required prefix is missing.

    * 1a1. Sieve shows an error message with the expected command format.

      Use case resumes at step 1.

* 2a. One of the details is invalid (e.g. a phone number that is not 8 digits).

    * 2a1. Sieve shows an error message for the invalid field.

      Use case resumes at step 1.

* 2b. A client with the same name (ignoring case) already exists.

    * 2b1. If the phone number also matches, Sieve tells the tutor the client already exists. Otherwise, Sieve asks the
      tutor to check the spelling of the name, and suggests adding an identifier to tell the clients apart (e.g.
      `John Tan (NHHS)`). No client is added.

      Use case resumes at step 1.

* 4a. Sieve cannot write to the data file.

    * 4a1. Sieve shows a warning that the client was added to the current view but not saved.

      Use case ends.

**Use case: UC2 - Find a client and update their details**

**MSS**

1. Tutor requests to find clients by a name keyword
2. Sieve shows the clients whose names match the keyword
3. Tutor requests to edit a specific client in the list with the new details
4. Sieve validates the new details
5. Sieve updates the client and saves the change
6. Sieve shows a confirmation message and the updated client

   Use case ends.

**Extensions**

* 1a. The keyword is blank, contains characters not allowed in names, or contains no letter or digit.

    * 1a1. Sieve shows an error message.

      Use case resumes at step 1.

* 2a. No client matches the keyword.

    * 2a1. Sieve shows that 0 clients were found, with tips for searching again.

      Use case ends.

* 3a. The given index is out of bounds.

    * 3a1. Sieve shows an error message.

      Use case resumes at step 3.

* 3b. No field to edit is provided.

    * 3b1. Sieve shows an error message with the expected command format.

      Use case resumes at step 3.

* 4a. One of the new details is invalid.

    * 4a1. Sieve shows an error message for the invalid field and leaves the client unchanged.

      Use case resumes at step 3.

* 4b. The edit would give the client the same name (ignoring case) as another client.

    * 4b1. Sieve shows the same duplicate messages as in UC1, extension 2b, and leaves the client unchanged.

      Use case resumes at step 3.

**Use case: UC3 - Tag clients to organise them**

**MSS**

1. Tutor requests to list all clients
2. Sieve shows all clients in a numbered list
3. Tutor requests to tag a specific client with one or more tags (e.g. a subject, a level or a lesson day)
4. Sieve adds the tags to the client's existing tags and saves the change
5. Sieve shows a confirmation message and the client with all their tags

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is out of bounds.

    * 3a1. Sieve shows an error message.

      Use case resumes at step 3.

* 3b. No tag is provided.

    * 3b1. Sieve shows an error message with the expected command format.

      Use case resumes at step 3.

* 3c. A tag is invalid (not 1-20 characters, or contains characters other than letters, numbers and spaces).

    * 3c1. Sieve shows an error message.

      Use case resumes at step 3.

* 4a. The client already has the given tag.

    * 4a1. Sieve ignores the duplicate tag and keeps the other tags as given.

      Use case continues from step 5.

**Use case: UC4 - Delete a client who no longer needs tuition**

**MSS**

1. Tutor requests to find the client by name
2. Sieve shows the matching clients
3. Tutor requests to delete a specific client in the list
4. Sieve removes the client and saves the change
5. Sieve shows a confirmation message

   Use case ends.

**Extensions**

* 2a. No client matches the keyword.

    * 2a1. Sieve shows that 0 clients were found, with tips for searching again.

      Use case ends.

* 3a. The given index exceeds the size of the displayed client list.

    * 3a1. Sieve shows:
      `Error: The index provided is out of bounds! Current list only contains X clients.`

      Use case resumes at step 3.

* 3b. The displayed client list is empty.

    * 3b1. Sieve shows:
      `Error: Cannot delete from an empty client list.`

      Use case ends.

**Use case: UC5 - Start with a clean client list on first use**

**MSS**

1. Tutor launches Sieve for the first time
2. Sieve finds no data file and loads sample clients
3. Tutor explores the sample clients using the other commands
4. Tutor requests to clear all data
5. Sieve removes all clients and saves the empty list
6. Tutor starts adding real clients (see UC1)

   Use case ends.

**Extensions**

* 2a. A data file exists but is corrupted.

    * 2a1. Sieve starts with an empty list, shows an error message and leaves the file untouched.

      Use case resumes at step 6.

* 4a. Tutor decides to keep the sample clients.

  Use case ends.

### Non-Functional Requirements

1. Sieve should work on any mainstream OS that has Java `25` or later installed.
2. Sieve should support at least 1,000 client records without noticeable sluggishness during typical usage.
3. Sieve should respond to a user command within two seconds when managing 1,000 client records, excluding delays caused
   by the operating system or file system.
4. Sieve should store client data locally and should not require an Internet connection for its core features.
5. If the data file is missing or corrupted, Sieve should remain usable and should not overwrite the corrupted file
   before informing the user of the loading error.
6. Tutor with above-average typing speed for regular English text should be able to perform most client-management tasks
   faster using commands than using mouse-based interactions.

### Glossary

* **Client**: A person receiving private tuition from the tutor and whose details are managed in Sieve. In the current
  implementation and some inherited documentation, a client may also be referred to as a *person* or *contact*.
* **Client record**: The collection of details stored in Sieve for one client, consisting of a name, phone number, email
  address, physical address, and zero or more tags.
* **Client list**: The collection of all client records stored in Sieve.
* **Currently displayed client list**: The numbered list of clients presently shown in the application. It may contain
  all clients or only the clients returned by a search.
* **Index**: The positive integer shown beside a client in the currently displayed client list. Commands such as `edit`
  and `delete` use this number to identify a client.
* **Mainstream OS**: A currently supported version of Windows, Linux, or macOS that can run Java `25` or later.
* **Prefix**: A command marker, such as `n/`, `p/`, `e/`, `a/`, or `t/`, that identifies the type of client detail
  following it.
* **Tag**: A user-defined alphanumeric label attached to a client record for categorisation, such as a subject,
  education level, or lesson day.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on; testers are expected to do more
*exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

    1. Download the JAR file and copy it into an empty folder.

    1. Double-click the JAR file.<br>
       Expected: The GUI opens with a set of sample clients. The window size may not be optimal.

1. Saving window preferences

    1. Resize the window to an optimal size. Move the window to a different location. Close the window.

    1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

### Finding clients

1. Finding clients by part of a name

    1. Prerequisites: Add a client with `add n/Liam O'Brien p/91234567 e/liam@example.com a/Blk 1 Clementi Rd`, and another with `add n/Nur-Aisyah Tan p/92345678 e/nur@example.com a/Blk 2 Clementi Rd`.

    1. Test case: `find O'Brien`, then `find Brien`, then `find obrien`, then `find Bri`<br>
       Expected: Each command lists `Liam O'Brien`.

    1. Test case: `find Nur`, then `find Aisyah`, then `find Ais`<br>
       Expected: Each command lists `Nur-Aisyah Tan`.

    1. Test case: `find Liam Aisyah`<br>
       Expected: Both clients are listed.

1. Finding by phone number and tag

    1. Prerequisites: Start with the sample data (delete `data/sieve.json` if it exists, or use a fresh folder).

    1. Test case: `find p/9112 2334`<br>
       Expected: Only `Farhan Ali` is listed.

    1. Test case: `find t/sec4`, then `find t/SEC4`<br>
       Expected: Each command lists `Amirah Tan` and `Brandon Sim`.

    1. Test case: `find amirah t/sec4`<br>
       Expected: Only `Amirah Tan` is listed.

    1. Test case: `find farhan t/sec4`<br>
       Expected: No clients are listed, because Farhan Ali is not tagged `Sec4`.

    1. Test case: `find p/9123`<br>
       Expected: The displayed list does not change. An error message about the phone number format is shown.

1. Finding with no matches

    1. Test case: `find rah`<br>
       Expected: No clients are listed (`rah` is the end of `Amirah`, not the start). The status message shows `0 clients found matching your search term(s).`, followed by tips for searching again.

1. Finding with invalid keywords

    1. Test case: `find Amirah_Tan`<br>
       Expected: The displayed list does not change. An error message lists the characters allowed in keywords.

    1. Test case: `find -`<br>
       Expected: Same as previous.

    1. Test case: `find`<br>
       Expected: The displayed list does not change. An invalid command format error is shown.

### Deleting a person

1. Deleting a client from the displayed list

    1. Prerequisites: Use the `list` command to display multiple clients.

    1. Test case: `delete 1`<br>
       Expected: The first displayed client is deleted. The status message shows the deleted client's details.

1. Deleting using an out-of-bounds index

    1. Prerequisites: Use the `list` command and note the number of displayed clients, `N`.

    1. Test case: Enter `delete N+1`, replacing `N+1` with an actual index greater than the displayed list size.<br>
       Expected: No client is deleted. The status message shows:<br>
       `Error: The index provided is out of bounds! Current list only contains N clients.`

1. Deleting using an index outside a filtered list

    1. Prerequisites: Use `find KEYWORD` so that exactly one client is displayed.

    1. Test case: `delete 2`<br>
       Expected: No client is deleted. The status message shows:<br>
       `Error: The index provided is out of bounds! Current list only contains 1 client.`

1. Deleting from an empty displayed list

    1. Prerequisites: Use `find KEYWORD`, where `KEYWORD` does not match any client.

    1. Test case: `delete 1`<br>
       Expected: No client is deleted. The status message shows:<br>
       `Error: Cannot delete from an empty client list.`

1. Deleting without specifying an index

    1. Test case: `delete`<br>
       Expected: No client is deleted. The status message shows:<br>
       `Invalid command format!`<br>
       `Error: Index not provided!`<br>
       `Expected format: delete INDEX`

1. Deleting using an invalid index

    1. Test cases: `delete 0`, `delete -1`, and `delete x`.<br>
       Expected: No client is deleted. The status message indicates that the command format or index is invalid and
       displays the usage instructions.

### Undoing changes

1. Undoing the last change

    1. Prerequisites: Use the `list` command to display multiple clients.

    1. Test case: `delete 1`, then `undo`<br>
       Expected: The deleted client is back in the list. The status message shows `Undid the last change. Showing all clients.`

1. Undoing several changes in a row

    1. Test case: `delete 1`, then `delete 1`, then `undo` twice<br>
       Expected: The first `undo` restores the second deleted client, and the second `undo` restores the first one.

1. Commands that change nothing do not use up an undo

    1. Test case: `delete 1`, then `list`, then `find KEYWORD`, then `undo`<br>
       Expected: The deleted client is restored, and the full client list is shown instead of the `find` results.

1. Failed commands do not use up an undo

    1. Test case: `delete 1`, then `delete 0`, then `undo`<br>
       Expected: `delete 0` shows an error. `undo` restores the client deleted by `delete 1`.

1. Undoing a clear

    1. Test case: `clear`, then `undo`<br>
       Expected: All clients are restored.

1. Nothing to undo

    1. Prerequisites: Relaunch the app, or `undo` until every change is undone.

    1. Test case: `undo`<br>
       Expected: The client list is unchanged. The status message shows `Error: There is nothing to undo.`

### Redoing changes

1. Redoing an undone change

    1. Prerequisites: Use the `list` command to display multiple clients.

    1. Test case: `delete 1`, then `undo`, then `redo`<br>
       Expected: The client is deleted again. The status message shows `Redid the last undone change. Showing all clients.`

1. Redoing several undone changes in a row

    1. Test case: `delete 1`, then `delete 1`, then `undo` twice, then `redo` twice<br>
       Expected: The first `redo` deletes the first client again, and the second `redo` deletes the second one again.

1. A new change clears the redo history

    1. Test case: `delete 1`, then `undo`, then `delete 2`, then `redo`<br>
       Expected: `redo` shows `Error: There is nothing to redo.` and the client list is unchanged.

1. Commands that change nothing do not clear the redo history

    1. Test case: `delete 1`, then `undo`, then `list`, then `redo`<br>
       Expected: The client is deleted again.

1. Nothing to redo

    1. Prerequisites: Relaunch the app.

    1. Test case: `redo`<br>
       Expected: The client list is unchanged. The status message shows `Error: There is nothing to redo.`
