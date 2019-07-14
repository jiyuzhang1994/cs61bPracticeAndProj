import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
public class practice extends Application {
    public class KeyEventHandler implements EventHandler<KeyEvent> {
        int textCenterX;
        int textCenterY;

        private static final int STARTING_FONT_SIZE = 20;
        private static final int STARTING_TEXT_POSITION_X = 250;
        private static final int STARTING_TEXT_POSITION_Y = 250;
        private Text displayText = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");
        private int fontSize = STARTING_FONT_SIZE;
        private String fontName = "Verdana";
        keyEventHandler (final Group root, int windowWidth, int windowHeight) {
            textCenterX = windowWidth/2;
            textCenterX = windowHeight/2;
            displayText = new Text(textCenterX, textCenterY, "");
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font(fontName, fontSize));
            root.getChildren().add(displayText);
        }

        @Override
        public void handle(KeyEvent keyEvent){
            if (keyEvent.getEventType() == keyEvent.KEY_TYPED) {
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    displayText.setText(characterTyped);
                    keyEvent.consume();
                }
            }
        }

    }

    @Override
    public void start(Stage stage) {
        Circle circ = new Circle (40, 40 ,30);
        Group root = new Group(circ);
        Scene scene = new Scene (root, 800, 600);

        stage.setTitle("practice");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {Application.launch(args);}
}
*/