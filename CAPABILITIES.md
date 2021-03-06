# CAPABILITIES

* Capability for the user to load a single file via the command line.
* Capability for the user to show current line being edited and a non blinking cursor.
* Capability for the user to allow inserting text into any part of the file.
* Capability for the user to scroll via the mouse wheel
* Capability for the user to see next file that could be opened by the editor from the current directory minus the files already opened if there are no more files to open and there are other directories in the current directory those should be open next for files.
* Capability for the user to have multiple files displayed simultaneously one after the other in the editor and be allowed to scroll between them.
* Capability for the user to clear see where one file ends and another begins
* Capability for the user to tell which file is currently visible
* Capability for code to recursively index any directory into an in memory master list up to 10000 files as quickly as possible.
* Capability for the user to be able to insert new characters and delete existing ones and save the changes.
* Capability for the user to create new lines by pressing enter.
* Capability of basic cursor navigation home bring cursor to beginning of the line end to the end of the current line, backspace removes the character before the cursor or joins the current line to the previous line if there is one.
* Capability to open the next file suggested for opening

## Missing Capabilities (in priority order)

* Capability for rendering methods to have separate contexts which provide a graphics object to
  use for rendering. As well as define other unique rendering properties which can be inherited
  or cloned to other context instances.
* 
* Capability to search through all the files currently opened and jump through the matches.
* Capability to open a file by searching through the listing of files.
* Capability to move the cursor to any line by clicking on that line
* Capability to render a scrollable selectable view of a file anywhere within any window and of any size.
* Capability for the user to choose to create a new file with a single click if the file they passed to the program to open does not exist and the capability for the user to choose to create all parent directories for new files that do not exist.
* Capability for code to determine what nearest directory is a code project directory.
* Capability for the user to know where the current scroll position is so they know approximately how far through the current number of lines they are.
* Capability for the user to open files on click from the current directory if no files are opened from the command line.
* Capability for the user to scroll using the cursor line if the user moves the cursor outside of the visible area the scroll offset should adjust to display the current line.
* Capability for the user to control whether or not line wrapping is turned on or horizontal scrolling will be used. If line wrapping is used then any line with characters beyond the width of the window will be placed on a non counted extra line and be part of the editors total height. If no line wrapping then a horizontal scroll bar will be used to allow the user to move the view to display the additional characters.
* Capability for the user to know exactly what line the cursor is one.
