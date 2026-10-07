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
  created by the [SE-EDU initiative](https://se-education.org). Parts of
  Sieve's code and documentation were adapted from AddressBook-Level3.
* Sieve uses [JavaFX](https://openjfx.io/) for its graphical user interface.
* Sieve uses [Jackson](https://github.com/FasterXML/jackson) for JSON data
  storage.
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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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

**Value proposition**: Tutors managing many clients with different subjects, levels, and 
constantly shifting schedules often lose track of details across scattered notes or apps. 
Sieve keeps every client organised in one place and lets tutors instantly find or filter anyone, 
keeping admin work out of the way of teaching.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ... | I can ... | So that ... |
| -------- | -------- | --------- | ----------- |
| `* * *` | potential user exploring the app | see the app populated with sample data | I can easily see how the app will look when it is in use |
| `* * *` | user ready to start using the app | purge all current data | I can get rid of sample/experimental data I used for exploring the app |
| `* * *` | new user | add a client with their name, phone, email, and address | I have their contact details on record |
| `* * *` | forgetful user | see an error message that shows the correct format and which part of my command was wrong | I can fix it immediately instead of guessing |
| `* * *` | new user | see a list of all commands and their formats | I can get started without feeling lost or leaving the app to look up syntax |
| `* * *` | tutor | edit the details of an existing client | I can fix or update information without retyping the whole entry |
| `* * *` | tutor | find a client by name keyword | I can pull up their details immediately when needed |
| `* * *` | tutor | delete a client I no longer tutor, e.g. a student who has graduated | I can maintain an updated and clean list |
| `* * *` | tutor | tag a client with free-form labels, and add or remove a single tag without retyping the whole contact | I can group clients the way my business actually works and keep small corrections cheap |
| `* * *` | tutor | see all my clients as a numbered list | I can refer to any of them by index in later commands |
| `* * *` | careless typist | undo my last command | a mistyped delete isn't a disaster |
| `* *` | new user switching from other tools | bulk-add multiple clients at once | I don't have to re-enter my existing client list one by one |
| `* *` | tutor | filter clients by one or more tags, such as day, subject, and level, in a single command | I can answer questions like "Sec 4 physics on Thursday" in one go |
| `* *` | tutor | mark a client as inactive (e.g. paused for exams or a break) instead of deleting them | I keep their record and can resume lessons later without re-adding them from scratch |
| `* *` | long-time user | hide inactive clients from my main list and see only the clients I am currently teaching | I am not distracted by irrelevant data and can quickly see who is still active |
| `* *` | tutor | add free-text notes to a client | I can record details that don't fit neatly into a tag |
| `* *` | tutor | export my client list to a file | I can back it up or move it to a new computer |
| `* *` | tutor considering a new client | check whether a proposed time slot clashes with an existing client's schedule | I avoid double-booking myself |
| `* *` | tutor | record whether a client has paid for the current month | I know who has paid and who is still outstanding, even for parents who pay at month-end |
| `* *` | tutor | see which clients have outstanding payments at a glance | I don't have to check each client individually |
| `*` | tutor | link a client to a family member who is also my client, such as a sibling | I can remember which students belong to the same household |
| `*` | tutor with limited time | sort or flag clients by priority | I know who to focus on when my own schedule is tight |
| `*` | tutor | see a quick count of my active clients | I can gauge my current workload at a glance |
| `*` | tutor | detect and remove duplicate client entries | my list doesn't contain confusing repeats |
| `*` | expert user | create shortcuts for tasks | I can save time on frequently performed tasks |
| `*` | tutor | have overdue payments flagged automatically | I don't have to manually track payment deadlines for every client |
| `*` | long-time tutor | view a family's history with me across multiple children | I can build rapport when starting with a new sibling |
| `*` | user with low confidence in technology | get confirmation that my data has been saved successfully | I trust the app is keeping my records safely |
| `*` | tutor who runs group classes | record a single session covering multiple students at once | I don't have to repeat the same entry per student |
| `*` | tutor who runs group classes | see which enrolled students are attending a specific session | I know who to expect and who is away |
| `*` | tutor who runs group classes | bill a group class as a single unit | I don't have to track payment separately for each student in it |

### Use cases

(For all use cases below, the **System** is `Sieve` and the **Actor** is the `tutor`, unless specified otherwise)

**Use case: UC1 - Add a client**

**MSS**

1.  Tutor requests to add a client by providing the name, phone number, email address and address
2.  Sieve validates the details
3.  Sieve adds the client to the end of the client list
4.  Sieve saves the updated client list to the data file
5.  Sieve shows a confirmation message and the new client

    Use case ends.

**Extensions**

* 1a. A required prefix is missing.

    * 1a1. Sieve shows an error message with the expected command format.

      Use case resumes at step 1.

* 2a. One of the details is invalid (e.g. a phone number that is not 8 digits).

    * 2a1. Sieve shows an error message for the invalid field.

      Use case resumes at step 1.

* 2b. A client with the same name and phone number already exists.

    * 2b1. Sieve shows a duplicate error message.

      Use case resumes at step 1.

* 4a. Sieve cannot write to the data file.

    * 4a1. Sieve shows a warning that the client was added to the current view but not saved.

      Use case ends.

**Use case: UC2 - Find a client and update their details**

**MSS**

1.  Tutor requests to find clients by a name keyword
2.  Sieve shows the clients whose names match the keyword
3.  Tutor requests to edit a specific client in the list with the new details
4.  Sieve validates the new details
5.  Sieve updates the client and saves the change
6.  Sieve shows a confirmation message and the updated client

    Use case ends.

**Extensions**

* 1a. The keyword is blank or contains characters other than letters.

    * 1a1. Sieve shows an error message.

      Use case resumes at step 1.

* 2a. No client matches the keyword.

    * 2a1. Sieve shows that 0 clients were found.

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

* 4b. The edit would make the client a duplicate of another client (same name and phone number).

    * 4b1. Sieve shows a duplicate error message and leaves the client unchanged.

      Use case resumes at step 3.

**Use case: UC3 - Tag clients to organise them**

**MSS**

1.  Tutor requests to list all clients
2.  Sieve shows all clients in a numbered list
3.  Tutor requests to tag a specific client with one or more tags (e.g. a subject, a level or a lesson day)
4.  Sieve adds the tags to the client's existing tags and saves the change
5.  Sieve shows a confirmation message and the client with all their tags

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

1.  Tutor requests to find the client by name
2.  Sieve shows the matching clients
3.  Tutor requests to delete a specific client in the list
4.  Sieve removes the client and saves the change
5.  Sieve shows a confirmation message

    Use case ends.

**Extensions**

* 2a. No client matches the keyword.

    * 2a1. Sieve shows that 0 clients were found.

      Use case ends.

* 3a. The given index exceeds the size of the displayed client list.

    * 3a1. Sieve shows:
      `Error: The index provided is out of bounds! Current list only contains X items.`

      Use case resumes at step 3.

* 3b. The displayed client list is empty.

    * 3b1. Sieve shows:
      `Error: Cannot delete from an empty client list.`

      Use case ends.

**Use case: UC5 - Start with a clean client list on first use**

**MSS**

1.  Tutor launches Sieve for the first time
2.  Sieve finds no data file and loads sample clients
3.  Tutor explores the sample clients using the other commands
4.  Tutor requests to clear all data
5.  Sieve removes all clients and saves the empty list
6.  Tutor starts adding real clients (see UC1)

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
3. Sieve should respond to a user command within two seconds when managing 1,000 client records, excluding delays caused by the operating system or file system.
4. Sieve should store client data locally and should not require an Internet connection for its core features.
5. If the data file is missing or corrupted, Sieve should remain usable and should not overwrite the corrupted file before informing the user of the loading error.
6. Tutor with above-average typing speed for regular English text should be able to perform most client-management tasks faster using commands than using mouse-based interactions.


### Glossary

* **Client**: A person receiving private tuition from the tutor and whose details are managed in Sieve. In the current implementation and some inherited documentation, a client may also be referred to as a *person* or *contact*.
* **Client record**: The collection of details stored in Sieve for one client, consisting of a name, phone number, email address, physical address, and zero or more tags.
* **Client list**: The collection of all client records stored in Sieve.
* **Currently displayed client list**: The numbered list of clients presently shown in the application. It may contain all clients or only the clients returned by a search.
* **Index**: The positive integer shown beside a client in the currently displayed client list. Commands such as `edit` and `delete` use this number to identify a client.
* **Mainstream OS**: A currently supported version of Windows, Linux, or macOS that can run Java `25` or later.
* **Prefix**: A command marker, such as `n/`, `p/`, `e/`, `a/`, or `t/`, that identifies the type of client detail following it.
* **Tag**: A user-defined alphanumeric label attached to a client record for categorisation, such as a subject, education level, or lesson day.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a client from the displayed list

    1. Prerequisites: Use the `list` command to display multiple clients.

    1. Test case: `delete 1`<br>
       Expected: The first displayed client is deleted. The status message shows the deleted client's details.

1. Deleting using an out-of-bounds index

    1. Prerequisites: Use the `list` command and note the number of displayed clients, `N`.

    1. Test case: Enter `delete N+1`, replacing `N+1` with an actual index greater than the displayed list size.<br>
       Expected: No client is deleted. The status message shows:<br>
       `Error: The index provided is out of bounds! Current list only contains N items.`

1. Deleting using an index outside a filtered list

    1. Prerequisites: Use `find KEYWORD` so that exactly one client is displayed.

    1. Test case: `delete 2`<br>
       Expected: No client is deleted. The status message shows:<br>
       `Error: The index provided is out of bounds! Current list only contains 1 items.`

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
       Expected: No client is deleted. The status message indicates that the command format or index is invalid and displays the usage instructions.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
