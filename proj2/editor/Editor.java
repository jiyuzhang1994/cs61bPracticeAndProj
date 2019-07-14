package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;


public class Editor extends Application {
    private static int WINDOW_WIDTH = 500;
    private static int WINDOW_HEIGHT = 500;

    private static final int STARTING_FONT_SIZE = 12;
    private static final int STARTING_TEXT_POSITION_X = 5;
    private static final int STARTING_TEXT_POSITION_Y = 0;
    private int MARGIN_LEFT = 5;
    private int MARGIN_RIGHT = WINDOW_WIDTH - 5;

    //cursor parameters
    private final Rectangle cursor = new Rectangle(1, 0);
    private int cursorHeight;

    private Group root;
    private Group textRoot;

    private int lineHeight;

    //font parameters
    private String fontName = "Verdana";
    private int fontSize = STARTING_FONT_SIZE;

    //line parameters
    private editorLinkedList textData = new editorLinkedList();
    private ArrayList<textNode> lines;
    private int lineIndex;

    //undo/redo parameters
    private Stack<Action> undoStack = new Stack<Action>();
    private int undoSize = 0;
    private int redoSize = 0;
    private Stack<Action> redoStack = new Stack<Action>();
    private textNode lastExecuted = textData.getSentinel(); // the last aadded/deleted text;
    private Action actionToAdd;

    public Editor() {
        Text lineEg = new Text(" ");
        lineEg.setFont(Font.font(fontName, fontSize));
        cursorHeight = (int) Math.round(lineEg.getLayoutBounds().getHeight());
        cursor.setX(STARTING_TEXT_POSITION_X);
        cursor.setY(STARTING_TEXT_POSITION_Y);
        cursor.setHeight(cursorHeight);
    }

    public void undo() {
        Action toPop = undoStack.pop();
        undoSize -= 1;
        lastExecuted = toPop.nodeExecuted;
        textData.setCurrText(lastExecuted);

        Action newAction;
        Text t;

        //last action was adding;
        if (toPop.add) {
            textNode toDelete = lastExecuted;
            t = textData.delete();
            textRoot.getChildren().remove(t);
            render();
            cursorUpdate();
            newAction = new Action(t, false, true, toDelete);
        } /* last action was deleting */else {
            textNode toAdd = lastExecuted;
            t = lastExecuted.text;
            textNode before = lastExecuted.prev;
            textNode after = lastExecuted.next;
            before.next = lastExecuted;
            after.prev = lastExecuted;
            textData.sizePlus();
            textData.setCurrText(toAdd);
            textRoot.getChildren().add(toAdd.text);
            render();
            cursorUpdate();
            newAction = new Action(t, true, false, toAdd);
        }

        if (redoSize == 100) {
            redoStack.remove(0);
            redoSize -= 1;
        }
        redoStack.push(newAction);
        redoSize += 1;
    }

    public void redo() {
        Action toPop = redoStack.pop();
        redoSize -= 1;
        lastExecuted = toPop.nodeExecuted;
        textData.setCurrText(lastExecuted);

        Action newAction;
        Text t;

        //last action was adding;
        if (toPop.add) {
            textNode toDelete = lastExecuted;
            t = textData.delete();
            textRoot.getChildren().remove(t);
            render();
            cursorUpdate();
            newAction = new Action(t, false, true, toDelete);
        } else {
            textNode toAdd = lastExecuted;
            t = lastExecuted.text;
            textNode before = lastExecuted.prev;
            textNode after = lastExecuted.next;
            before.next = lastExecuted;
            after.prev = lastExecuted;
            textData.setCurrText(toAdd);
            textRoot.getChildren().add(t);
            render();
            cursorUpdate();
            newAction = new Action(t, true, false, toAdd);
        }
        if (undoSize == 100) {
            undoStack.remove(0);
            undoSize -= 1;
        }
        undoStack.push(newAction);
        undoSize += 1;

    }

    public void render() {
        textNode textToRender;
        Text t;
        lines = new ArrayList<textNode>();
        lineIndex = 0;
        textNode lastSpace = null;
        int idxOfSpace = lineIndex; //lineIndex of lastSpace;
        int tWidth;
        int posX = STARTING_TEXT_POSITION_X;
        int posY = STARTING_TEXT_POSITION_Y;
        //Calculate height of the font;
        Text lineEg = new Text(" ");
        lineEg.setFont(Font.font(fontName, fontSize));
        lineHeight = (int) Math.round(lineEg.getLayoutBounds().getHeight());

        Iterator enumerator = textData.iterator();
        while (enumerator.hasNext()) {
            textToRender = (textNode) enumerator.next();
            t = textToRender.text;
            t.setFont(Font.font(fontName, fontSize));
            tWidth = (int) Math.round(t.getLayoutBounds().getWidth());

            //update lastSpace;
            if (t.getText().equals(" ")) {
                lastSpace = textToRender;
                idxOfSpace = lineIndex;
            }

            //new line because of symbol "\r";
            if (t.getText().equals("\r")) {
                t.setX(posX);
                t.setY(posY);
                posX = MARGIN_LEFT;
                posY = posY + lineHeight;
                lines.add(lineIndex, textToRender);
                lineIndex += 1;
            } /*new line because exceeding boundary; */ else if (posX + tWidth > MARGIN_RIGHT) {
                //not space;
                if ( !t.getText().equals(" ") ) {
                    /* exists lastSpace*/
                    if (lastSpace != null && idxOfSpace == lineIndex) {
                        lines.add(lineIndex, lastSpace);
                        lineIndex += 1;
                        posX = MARGIN_LEFT;
                        posY = posY + lineHeight;
                        textNode toWrap = lastSpace.next;
                        while (toWrap != textToRender) {
                            Text w = toWrap.text;
                            w.setFont(Font.font(fontName, fontSize));
                            int wWidth = (int) Math.round(w.getLayoutBounds().getWidth());
                            w.setX(posX);
                            w.setY(posY);
                            posX = posX + wWidth;
                            toWrap = toWrap.next;
                        }
                        t.setX(posX);
                        t.setY(posY);
                        posX = posX + tWidth;
                    } /* is null or no lastSpace last line*/else {
                        lines.add(lineIndex, textToRender.prev);
                        lineIndex += 1;
                        posX = MARGIN_LEFT;
                        posY = posY + lineHeight;
                        t.setX(posX);
                        t.setY(posY);
                        posX = posX + tWidth;
                    }
                }/* is space*/ else {
                    t.setX(MARGIN_RIGHT);
                    t.setY(posY);
                    posX = MARGIN_RIGHT;
                    //cursor to edge
                    cursor.setX(MARGIN_RIGHT);
                }
            } else {
                t.setX(posX);
                t.setY(posY);
                posX = posX + tWidth;
            }
        }
    }

    /** An EventHandler to handle keys that get pressed. */
    private class KeyEventHandler implements EventHandler<KeyEvent> {


        KeyEventHandler(Group root, int windowWidth, int windowHeight) {

        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.isShortcutDown()) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.Z) {
                    if (!undoStack.empty()) {
                        undo();
                    }
                } else if (code == KeyCode.Y) {
                    if (!redoStack.empty()) {
                        redo();
                    }
                } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                    fontSize += 4;
                    render();
                    cursorUpdate();
                } else if (code == KeyCode.MINUS) {
                    fontSize = Math.max(0, fontSize -4);
                    render();
                    cursorUpdate();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    // Ignore control keys, which have non-zero length, as well as the backspace
                    // key, which is represented as a character of value = 8 on Windows.
                    redoStack = new Stack<Action>();
                    Text textToAdd = new Text(characterTyped);
                    textToAdd.setTextOrigin(VPos.TOP);
                    textToAdd.setFont(Font.font(fontName, fontSize));
                    textData.addText(textToAdd);
                    textRoot.getChildren().add(textToAdd);
                    actionToAdd = new Action(textToAdd, true, false, textData.getCurrText());
                    if (undoSize == 100) {
                        undoStack.remove(0);
                        undoSize -= 1;
                    }
                    undoStack.push(actionToAdd);
                    undoSize += 1;
                    render();
                    cursorUpdate();
                    keyEvent.consume();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.UP) {
                    textNode end; // the end of last line;
                    textNode closest;
                    int currentIdx = (int) (cursor.getY()/ lineHeight);
                    int x = (int) cursor.getX();
                    //the first line;
                    if (currentIdx == 0) {
                        textData.setCurrText(textData.getSentinel());
                        cursor.setX(MARGIN_LEFT);
                        cursor.setY(0);
                    }/* not first line*/ else {
                        //find the end of last line;
                        end = lines.get(currentIdx - 1);
                        closest = end;
                        //find the "almost" closest char;
                        while ((int) closest.text.getX() > x) {
                            closest = closest.prev;
                        }
                        if ((int) cursor.getX() == MARGIN_LEFT) {
                            cursor.setX(MARGIN_LEFT);
                            cursor.setY((int)cursor.getY() - lineHeight);
                            if (currentIdx - 1 == 0) {
                                textData.setCurrText(textData.getSentinel());
                            } else {
                                textData.setCurrText(lines.get(currentIdx - 2));
                            }
                        } else {
                            int leftDistance = x - (int) closest.text.getX();
                            int rightDistance = (int) closest.next.text.getX() - x;
                            if (rightDistance > leftDistance) {
                                textData.setCurrText(closest.prev);
                            } else {
                                textData.setCurrText(closest);
                            }
                            if (textData.getCurrText().text.getText().equals("\r")){
                                textData.moveLeft();
                            }
                            cursorUpdate();
                        }
                    }
                } else if (code == KeyCode.DOWN) {
                    textNode end;
                    textNode closest;
                    int x = (int) cursor.getX();
                    int currentIdx = (int) (cursor.getY()/ lineHeight);
                    textNode last = textData.getBack().prev;
                    if ((int) cursor.getY() == (int) last.text.getY()) {
                        textData.setCurrText(last);
                        cursorUpdate();
                    } else if (textData.getCurrText() == last) {
                        cursorUpdate();
                    } else {
                        int lastIdx = (int) (last.text.getY()/ lineHeight); // index of last line;
                        end = (lastIdx == currentIdx + 1)? last : lines.get(currentIdx + 1);
                        closest = end;
                        while ((int) closest.text.getX() > x) {
                            closest = closest.prev;
                        }
                        if ((int) cursor.getX() == MARGIN_LEFT) {
                            textData.setCurrText(end);
                            cursor.setX(MARGIN_LEFT);
                            cursor.setY((int)cursor.getY() + lineHeight);
                        } else {
                            int leftDistance = x - (int) closest.text.getX();
                            int rightDistance = (int) closest.next.text.getX() - x;
                            if (rightDistance > leftDistance) {
                                textData.setCurrText(closest.prev);
                            } else {
                                textData.setCurrText(closest);
                            }
                            if (textData.getCurrText().text.getText().equals("\r")){
                                textData.moveLeft();
                            }
                            cursorUpdate();
                        }
                    }
                } else if(code == KeyCode.LEFT){
                    textNode current = textData.getCurrText();
                    if (current != textData.getSentinel()) {
                        textData.moveLeft();
                        //if the lastSpace encountered, set the cursor at the beginning of the next line;
                        if (lines.contains(current.prev) && current.prev.text.getText().equals(" ")) {
                            cursor.setX(MARGIN_LEFT);
                        } else {
                            cursorUpdate();
                        }
                    }
                } else if (code == KeyCode.RIGHT) {
                    textNode current = textData.getCurrText();
                    if (current.next != textData.getBack()) {
                        textData.moveRight();
                        current = textData.getCurrText();
                        // similar to LEFT above;
                        if (lines.contains(current) && current.text.getText().equals(" ")) {
                            cursor.setX(MARGIN_LEFT);
                            cursor.setY((int) current.text.getY() + lineHeight);
                        } else {
                            cursorUpdate();
                        }
                    }
                } else if(code == KeyCode.BACK_SPACE) {
                    //clear redo;
                    redoStack = new Stack<Action>();
                    textNode toDelete = textData.getCurrText();
                    Text t = textData.delete();
                    textRoot.getChildren().remove(toDelete.text);
                    actionToAdd = new Action(t, false, true, toDelete);
                    if (undoSize == 100) {
                        undoStack.remove(0);
                        undoSize -= 1;
                    }
                    undoStack.push(actionToAdd);
                    undoSize += 1;
                    render();
                    cursorUpdate();
                }
            }
        }

    }

    public void cursorUpdate() {
        Text current = textData.getCurrText().text;
        if (textData.getCurrText() == textData.getSentinel()) {
            cursor.setX(STARTING_TEXT_POSITION_X);
            cursor.setY(STARTING_TEXT_POSITION_Y);
        } else if (current.getText().equals("\r")) {
            cursor.setX(MARGIN_LEFT);
            cursor.setY((int) current.getY() + lineHeight);
        } else {
            int cWidth = (int) Math.round(current.getLayoutBounds().getWidth());
            cursor.setX((int) Math.round((int) current.getX()) + cWidth);
            cursor.setY((int) Math.round(current.getY()));
        }
        cursor.setHeight(lineHeight);
    }

    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors = {Color.BLACK, Color.TRANSPARENT};

        RectangleBlinkEventHandler() {
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    public void makeRectangleColorChange() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            textNode last; // the last char;
            textNode closest;
            System.out.println(mousePressedX + ", " + mousePressedY);
            int idx = (int) Math.floor(mousePressedY / lineHeight); //which line you clicked;
            if (textData.isEmpty()) {
                cursorUpdate();
            } else {
                last = textData.getBack().prev;
                int lastBoundaryX = (int) last.text.getX() + (int) Math.round(last.text.getLayoutBounds().getWidth());
                int lastBoundaryY = (int) last.text.getY() + lineHeight;
                if (mousePressedY > lastBoundaryY ) {
                    textData.setCurrText(last);
                    if (last.text.getText().equals("\r")) {
                        textData.moveLeft();
                    }
                    cursorUpdate();
                } /* at last line */else if (mousePressedY >= lastBoundaryY - lineHeight) {
                    if (mousePressedX >= lastBoundaryX) {
                        textData.setCurrText(last);
                        if (last.text.getText().equals("\r")) {
                            textData.moveLeft();
                        }
                        cursorUpdate();
                    } else if (mousePressedX <= MARGIN_LEFT ) {
                        textData.setCurrText(  (idx == 0)? textData.getSentinel() : lines.get(idx - 1));
                        cursor.setX(MARGIN_LEFT);
                        cursor.setY(idx * lineHeight);
                    } else {
                        closest = last;
                        while (closest.text.getX() > mousePressedX) {
                            closest = closest.prev;
                        }
                        double leftDist = mousePressedX - closest.text.getX();
                        double rightDist = closest.next.text.getX() - mousePressedX;
                        if (rightDist > leftDist) {
                            textData.setCurrText(closest.prev);
                            if (idx > 0 && closest.prev == lines.get(idx -1)) {
                                cursor.setX(MARGIN_LEFT);
                                cursor.setY(idx * lineHeight);
                            } else {
                                cursorUpdate();
                            }
                        } else {
                            textData.setCurrText(closest);
                            cursorUpdate();
                        }
                    }
                } else  {
                    textNode right = lines.get(idx);
                    int rightBound = (int) right.text.getX() + (int) Math.round(right.text.getLayoutBounds().getWidth());
                    if (mousePressedX >= rightBound) {
                        textData.setCurrText(lines.get(idx));
                        if (lines.get(idx).text.getText().equals("\r")) {
                            textData.moveLeft();
                            cursorUpdate();
                        } else {
                            cursor.setX(rightBound);
                            cursor.setY(idx * lineHeight);
                        }
                    } else if (mousePressedX <= MARGIN_LEFT) {
                        if (idx == 0) {
                            textData.setCurrText(textData.getSentinel());
                            cursorUpdate();
                        } else {
                            textData.setCurrText(lines.get(idx - 1));
                            cursor.setX(MARGIN_LEFT);
                            cursor.setY(idx * lineHeight);
                        }
                    } else {
                        closest = lines.get(idx);
                        while (closest.text.getX() > mousePressedX) {
                            closest = closest.prev;
                        }
                        double leftDist = mousePressedX - closest.text.getX();
                        double rightDist = closest.next.text.getX() - mousePressedX;
                        if (rightDist > leftDist) {
                            textData.setCurrText(closest.prev);
                            if (idx > 0 && closest.prev == lines.get(idx -1)) {
                                cursor.setX(MARGIN_LEFT);
                                cursor.setY(idx * lineHeight);
                            } else {
                                cursorUpdate();
                            }
                        } else {
                            textData.setCurrText(closest);
                            cursorUpdate();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        root = new Group();
        textRoot = new Group();
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new Editor.KeyEventHandler(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler());

        root.getChildren().add(textRoot);
        textRoot.getChildren().add(cursor);
        makeRectangleColorChange();

        primaryStage.setTitle("Jiyu Editor");

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}