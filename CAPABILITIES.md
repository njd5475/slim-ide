# CAPABILITIES

* Capability for the user to load a single file via the command line.
* Capability for the user to show current line being edited and a non blinking cursor.
* Capability for the user to allow inserting text into any part of the file.
* Capability for the user to scroll via the mouse wheel
* Capability for the user to see next file that could be opened by the editor from the current directory minus the files already opened if there are no more files to open and there are other directories in the current directory those should be open next for files.
* Capability for the user to have multiple files displayed simultaneously one after the other in the editor and be allowed to scroll between them.
* Capability for the user to clear see where one file ends and another begins
* Capability for the user to tell which file is currently visible

## Missing Capabilities (in priority order)

* Capability for the user to choose to create a new file with a single click if the file they passed to the program to open does not exist and the capability for the user to choose to create all parent directories for new files that do not exist.
* Capability for code to recursively index any directory into an in memory master list as quickly as possible.
* Capability for code to determine what nearest directory is a code project directory.
* Capability for the user to be able to insert new characters and delete existing ones and save the changes.
* Capability for the user to know where the current scroll position is so they know approximately how far through the current number of lines they are.
* Capability for the user to open files on click from the current directory if no files are opened from the command line.
* Capability for the user to scroll using the cursor line if the user moves the cursor outside of the visible area the scroll offset should adjust to display the current line.
* Capability for the user to move the cursor from the beginning of the line to the end of the line.
* Capability for the user to control whether or not line wrapping is turned on or horizontal scrolling will be used. If line wrapping is used then any line with characters beyond the width of the window will be placed on a non counted extra line and be part of the editors total height. If no line wrapping then a horizontal scroll bar will be used to allow the user to move the view to display the additional characters.

