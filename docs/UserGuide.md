---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# Sieve User Guide

Sieve is a **desktop app that helps self-employed private tuition teachers keep track of their tutees**. Sieve is Optimised for use through a Command Line Interface (CLI) while still providing you with a Graphical User Interface (GUI) to your results. If you can type fast, Sieve lets you find and update any client's details quicker than hunting through chats, notes apps and spreadsheets, so you spend less time on admin and more time teaching.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-T08-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for Sieve.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar sieve.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all clients.

   * `add n/Jason Quek p/91234567 e/jasonquek@gmail.com a/Blk 123 Yishun Ave 5 #04-12` : Adds a client named `Jason Quek` to Sieve.

   * `delete 3` : Deletes the 3rd client shown in the current list.

   * `clear` : Deletes all clients.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `Amirah Tan`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/Amirah Tan t/Math` or as `n/Amirah Tan`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/Math` or `t/Math t/Sec4`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, `clear`, `undo` and `redo`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a client: `add`

Adds a client to Sieve.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [nt/NOTE] [t/TAG]... `

<box type="tip" seamless>

**Tip:** A client can have any number of tags, including zero.

A note is free text of up to 200 characters, for anything you want to remember about the client, e.g. `nt/Weak at algebra, prelims in March`. Leave it out if you have nothing to record.
</box>

<box type="info" seamless>

**Duplicate clients:**
* Sieve does not allow two clients with the same name. Names are compared ignoring case, so `John Tan` and `john tan` count as the same name.
* If the phone number also matches, Sieve tells you that the client already exists.
* If the phone number is different, Sieve asks you to check the name and phone number. If it really is a different client, add a detail to the name to tell them apart, e.g. `John Tan (NHHS)`.
* Different clients may share a phone number (e.g. siblings using a parent's number), as long as their names are different.
* The same rules apply when you change a client's name with `edit`.
</box>

Examples:
* `add n/Jason Quek p/91234567 e/jasonquek@gmail.com a/Blk 123 Yishun Ave 5 #04-12`
* `add n/Brandon Sim p/92345678 e/brandonsim@sample.com a/10 Queenstown Rd nt/Parent prefers WhatsApp after 6pm` adds a client with a note.
* `add n/Matthias Tan t/Physics e/matthias.tan@gmail.com a/10 Queenstown Rd p/8123 4567 t/Sec4` adds a client with two tags. Fields can be given in any order, and spaces in the phone number are ignored.

### Listing all clients: `list`

Shows a list of all clients in Sieve.

Format: `list`

### Editing a client: `edit`

Edits an existing client in Sieve.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [nt/NOTE] [t/TAG]... `

* Edits the client at the specified `INDEX`. The index refers to the index number shown in the displayed client list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the client's existing tags are removed; adding tags is not cumulative.
* To remove all of a client's tags, enter `t/` without a tag after it.
* Editing the note replaces the whole note; it does not add to the existing one. To remove a client's note, enter `nt/` without any text after it.
* A note cannot contain text that looks like another field, such as ` t/` or ` p/`, because Sieve reads it as the start of that field.

Examples:
*  `edit 1 p/81112222 e/amirah.tan01@gmail.com` Edits the phone number and email address of the 1st client to be `81112222` and `amirah.tan01@gmail.com` respectively.
*  `edit 2 n/Brandon Tan t/` Edits the name of the 2nd client to be `Brandon Tan` and clears all existing tags.
*  `edit 3 nt/Exam in March` Sets the note of the 3rd client to `Exam in March`.
*  `edit 3 nt/` Clears the note of the 3rd client.

### Locating clients by name: `find`

Finds clients by name keywords.

Format: `find KEYWORD [MORE_KEYWORDS]...`

* At least one keyword must be given.
* Keywords can contain the same characters as names: letters, digits, hyphens (`-`), apostrophes (`'`), full stops (`.`) and parentheses (`()`). Each keyword must contain at least one letter or digit.
* The search is case-insensitive; for example, `amirah` matches `Amirah`.
* Keyword order does not matter; for example, `Tan Amirah` matches `Amirah Tan`.
* A keyword matches the **start** of a word of a name, or the start of a part of a word separated by punctuation; for example, `Ami` matches `Amirah`, `Bri` matches `O'Brien`, `Ais` matches `Nur-Aisyah`, and `NHHS` matches `John Tan (NHHS)`.
* Punctuation is ignored; for example, `obrien` and `O'Brien` both match `O'Brien`.
* A keyword does not match the middle or end of a word; for example, `rien` does not match `O'Brien`, and `rah` does not match `Amirah`.
* Clients matching at least one keyword are returned (an `OR` search); for example, `Amirah Farhan` returns `Amirah Tan` and `Farhan Ali`.

If no client matches, Sieve shows `0 clients found` with tips for searching again. Use `list` to see all clients again.

Examples:
* `find amirah` returns `Amirah Tan`
* `find amirah farhan` returns `Amirah Tan` and `Farhan Ali`
* `find ami` returns `Amirah Tan`
* `find Brien` returns `Liam O'Brien` (if you have added that client)<br>
  ![result for 'find Brien'](images/findBrienResult.png)

<box type="info" seamless>

**Limitations:**
* `find` searches names only. To search by phone number or tag, use [`filter`](#filtering-clients-by-phone-number-or-tags-filter).
* A keyword is matched against one word of a name at a time, so a keyword that joins two words does not match. For example, `Aisyah-Tan` does not match `Nur-Aisyah Tan`; use `find Aisyah Tan` instead.
* A short keyword can match many clients; for example, `find a` returns every client with a name part starting with `a`. Type more letters to narrow the results.
</box>

### Filtering clients by phone number or tags: `filter`

Finds clients who match the specified phone and tag filters.

Format: `filter [p/PHONE]... [t/TAG]...`

* At least one phone number or tag must be given.
* `p/PHONE` must be a complete 8-digit phone number. Spaces, hyphens and parentheses are ignored.
* If several phone numbers are supplied, clients matching any of them are returned.
* Tag matching is case-insensitive; for example, `t/physics` matches `Physics`.
* Tag order does not matter.
* A client must have every specified tag.
* When phone numbers and tags are combined, a client must match both fields.

Examples:
* `filter p/9123 4567` returns the client with phone number `91234567`.
* `filter p/91234567 p/92345678` returns the clients with either phone number.
* `filter t/Physics` returns every client tagged `Physics`.
* `filter t/Physics t/Sec4` returns clients tagged with both `Physics` and `Sec4`.
* `filter p/92345678 t/Sec4` returns the client matching that phone number who is also tagged `Sec4`.

### Tagging a client: `tag`

Adds one or more tags to an existing client.

Format: `tag INDEX t/TAG [t/MORE_TAGS]...`

* The index refers to the index number shown in the displayed client list and must be a positive integer.
* Tags must be 1-20 characters long and contain only letters, numbers, and spaces.
* New tags are added alongside the client's existing tags.
* Adding a tag the client already has does not create a duplicate.
* The success message lists only newly added tags. If the client already has every specified tag, Sieve reports that the client "has all of the tag(s)" and leaves the client unchanged.

Examples:
* `tag 1 t/physics t/sec4`
* `tag 2 t/thursday`

### Removing tags from a client: `untag`

Removes one or more tags from an existing client.

Format: `untag INDEX t/TAG [t/MORE_TAGS]...`

* The index refers to the index number shown in the displayed client list and must be a positive integer.
* Tag matching is case-insensitive; for example, `t/math` removes an existing `Math` tag.
* Tags not currently attached to the client are ignored.
* Other tags remain unchanged.
* The success message lists only tags that were actually removed. If none of the specified tags exist, Sieve reports that the client does not have any of them and leaves the client unchanged.

Examples:
* `untag 1 t/Math`
* `untag 2 t/Physics t/Sec4`

### Deleting clients: `delete`

Deletes one or more specified clients from Sieve.

Format: `delete INDEX [, INDEX]...`

* Deletes the clients at the specified indices.
* Each index refers to the index number shown in the displayed client list and **must be a positive integer** 1, 2, 3, ...
* To delete multiple clients, provide at least two comma-separated indices. Spaces around the commas are optional.
* Duplicate indices are not allowed.
* All indices are checked before any client is deleted. If any index is invalid, no clients will be deleted.
* If the displayed client list is empty, no client will be deleted and the following error will be shown:
    `Error: Cannot delete from an empty client list.`
* If any `INDEX` exceeds the size of the displayed client list, no client will be deleted and the following error will be shown:
  `Error: The index provided is out of bounds! Current list only contains X clients.`
  Here, `X` is the number of clients in the displayed list (`1 client` when only one client is displayed).
* If `INDEX` is not provided, the following error will be shown:
  `Error: Index not provided!
   Expected format: delete INDEX`

Examples:
* `list` followed by `delete 2` deletes the 2nd client in Sieve.
* `find Amirah` followed by `delete 1` deletes the 1st client in the results of the `find` command.
* `list` followed by `delete 1, 3, 5` deletes the 1st, 3rd and 5th clients in Sieve.
* `filter t/physics` followed by `delete 1,2` deletes the 1st and 2nd clients in the filtered results.

### Clearing all clients: `clear`

Deletes all clients from Sieve.

Format: `clear`

### Undoing a change: `undo`

Reverts the last change to your client list.

Format: `undo`

* Changes can be undone one at a time, back to how your client list was when you opened Sieve.
* Only commands that change your client list can be undone: `add`, `edit`, `delete`, `tag`, `untag` and `clear`.
* Commands that do not change anything, such as `list`, `find` and `help`, are skipped. Commands that fail are also skipped.
* An `edit`, `tag`, or `untag` that leaves the client exactly as before is also skipped. For example, this happens when the edited value is unchanged, every requested tag already exists, or none of the requested tags exist. An `undo` after it reverts the preceding change instead.
* After an undo, the full client list is shown, so that the restored client is not hidden by an earlier `find`.
* The undone change is saved to the data file straight away, like any other change.
* If there is nothing left to undo, the following error is shown: `Error: There is nothing to undo.`
* An undo can be reversed with [`redo`](#redoing-an-undone-change-redo).

<box type="warning" seamless>

**Caution:** Undo and redo history are not kept after you close Sieve. Once you reopen it, earlier changes cannot be undone or redone.
</box>

Examples:
* `delete 2` followed by `undo` brings back the client you just deleted.
* `clear` followed by `undo` brings back all your clients.
* `delete 1`, then `find Jason`, then `undo` brings back the deleted client and shows the full list again.

### Redoing an undone change: `redo`

Reapplies the last change you reverted with `undo`.

Format: `redo`

* Undone changes can be redone one at a time, starting from the most recently undone.
* Once you make a new change after an `undo`, such as `add` or `delete`, the undone changes can no longer be redone.
* Commands that do not change anything, such as `list`, `find` and `help`, are skipped and do not affect what can be redone.
* After a redo, the full client list is shown.
* The redone change is saved to the data file straight away, like any other change.
* If there is nothing to redo, the following error is shown: `Error: There is nothing to redo.`

Examples:
* `delete 2`, then `undo`, then `redo` deletes the same client again.
* `delete 1`, then `undo`, then `delete 3`, then `redo` shows an error, because `delete 3` is a new change made after the `undo`.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

Sieve automatically saves data after every command. You do not need to save manually.

### Editing the data file

Sieve data is saved automatically as a JSON file `[JAR file location]/data/sieve.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, Sieve launches in a read-only fallback mode, displaying an empty list and showing a warning popup message at startup. Sieve will completely block any subsequent command that attempts to mutate data, ensuring your original corrupted file is left completely untouched on disk so you can fix it safely. Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause Sieve to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous Sieve home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [nt/NOTE] [t/TAG]... ` <br> e.g., `add n/Immanuel Lim p/98765432 e/immanuel.lim@gmail.com a/Blk 456 Bishan St 21 #11-22 t/Chemistry t/JC1`
**Clear**  | `clear`
**Delete** | `delete INDEX [, INDEX]...`<br> e.g., `delete 3`, `delete 1, 3, 5`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [nt/NOTE] [t/TAG]... `<br> e.g., `edit 1 p/81112222 e/amirah.tan01@gmail.com`, `edit 2 nt/Exam in March`
**Filter** | `filter [p/PHONE]... [t/TAG]...`<br> e.g., `filter p/92345678 t/sec4`
**Find**   | `find KEYWORD [MORE_KEYWORDS]...`<br> e.g., `find Amirah O'Brien`
**List**   | `list`
**Tag**    | `tag INDEX t/TAG [t/MORE_TAGS]...`<br> e.g., `tag 1 t/physics t/sec4`
**Untag**  | `untag INDEX t/TAG [t/MORE_TAGS]...`<br> e.g., `untag 1 t/physics`
**Undo**   | `undo`
**Redo**   | `redo`
**Help**   | `help`
