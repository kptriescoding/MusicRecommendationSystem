package Buttons;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;

public class Home extends Button {

    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 2, 2, 2, 2;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 3 1 1 3;";

    public Home() {
        super("Home");
        ImageView image = new ImageView("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHwAAAB8CAMAAACcwCSMAAAAYFBMVEUAAAD///+enp6oqKijo6NfX1/39/fu7u5MTEzV1dXz8/M4ODiampoNDQ07Ozs+Pj7m5uaRkZFHR0dSUlIqKippaWlycnKHh4exsbF8fHwyMjJYWFjd3d2/v78gICBkZGREKZ8lAAACwUlEQVRoge3bb3uyIBQG8ONKcKTTMp9ZW/X9v+Vqpjk8/BE4+rzgfjlcv4RzgRpC4pwqz4oiyyv3TwDn/9zDM/vFcZbCkJQti7MaRqkddUf8A/7kY0GcSfZddzp3F1wcZBvgIJbBeT217+POJwdW70OmjW64yDAbIJPO/VJfX43H5j0EzpA+f/b8eNxFIzcj08FsfFJro6obHYZ8xZMvLkq1DVAOPX/Cmi9+OFeMd5/sWVgCbT164SZ70Ld467cHrq61V7qqQ3sdoPXAd2YbYPc48h/e9uaMMyv7rrPwOLfo8y4HHho319ormVCMuSOumlPx1Cn+d0dcO7dYxwm3rTUKXD+n0uJzai00Hs6ej9vMqWR4oFpzwUVIeyZuP6eGxwPW2mwcuz5fDA863vPwUHOqCx641mbhoWttDk5iW+Is1DrmgBPUmjXONfdj1DhFndviNLVmhwefU+fgRLX2m60eD7t+yymFDqertS4HrsYJa+2ZjKtwylrr8+fcR/j0uSJFdgzDqeZUjT7gnGYtQVJyGaeu83GGcYel6nycvuY7nGj9VqZkL5ydl7UBzqzHaedUPDvR4WzR8e6TsV9c8fSGOukD35uPo8k+gWotG+50vh6ewyrV1iWDYj28WBdftdtXLbjv9fAKks+17M/H9Dr57W+ZNN2qtsqw5/16LtpT2uVGelVR3p7MqRWTm4ZE+XNcmBju1ZI3Stx0lxrxiP93+LnJt1LyxubS3x8vNui+E74xXxl444VyC1pl1L1xfLtNd/LU+EZtJ8mGFj9qN2CxIyne6OwkuZHiuR43XBl44tohNw56xCMe8YhHPOIRj3jEIx7xiEc84hG3wltK3PQqCf7uTSBcfqdtsjHriw7/kg+f4Pqfurxw+e0hZDOedtR9cHnE0W2Il6v6A9zx6+SFMXwDJmtvKt8Rv95a7OnhD95KKDw8/jesAAAAAElFTkSuQmCC");
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        setGraphic(image);
        setStyle(STYLE_NORMAL);
        setOnMousePressed(event -> setStyle(STYLE_PRESSED));
        setOnMouseReleased(event -> setStyle(STYLE_NORMAL));
        setContentDisplay(ContentDisplay.TOP);
    }

}