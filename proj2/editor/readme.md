Text Editor

**(1) Functionalities**

This editor currently supports the following functionalities:

1.undo/redo

Pressing shortcut keys(command in MAC) + "z" can undo last action (adding/deleting a character), which has a limit of last at most 100 actions; (Also see the proof of correctness at this [link](https://jiyuzhang1994.github.io/ProofOfUndo/))

Pressing command + "y" keys can redo your last undoed action.

2.font adjusting

Pressing command + "+/=" keys can increase the size of the font by 4. 

Pressing command + "-"(minus) keys can decrease the size of the font by 4.

3.word wrap

When typing in a word, if the length of words at the current line exceeds the maximum length (by default setting) of the line. The last incomplete word will automatically be transferred to the next line.

4.cursor moving

Press arrows (up, down, left, right) can change the position (both logical position and cursor position) of your text. Also, mouse clicks can also change the position of text.

**(2) Data Structure and Operation Run time**

The data structure to store texts is a specially implemented doubly linked list. By "doubly" we mean every node contains pointers to both previous node and next node. There is also a sentinel node to track the head of the list and a back node to track the tail of the list.

Insertion: Adding a character to the text takes constant time.

Deletion : Deleting a character from the text takes constant time.

Cursor Postion: Changing curosor position using arrows or mouse clicks take constant time.

Rendering text: Render the text on the window take linear time.

Undo/Redo: Undo and Redo take constant time.