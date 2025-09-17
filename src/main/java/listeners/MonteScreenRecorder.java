package listeners;

import dateTimeUtils.DateUtils;
import enumerations.MessageLevel;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.*;
import static reportUtils.Report.reportAndLog;

public class MonteScreenRecorder extends ScreenRecorder {

    public static ScreenRecorder screenRecorder;
    public static String videoName;

    public MonteScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                               Format screenFormat, Format mouseFormat, Format audioFormat,
                               File movieFolder, String name) throws IOException, AWTException {

        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        MonteScreenRecorder.videoName = name + "_" + DateUtils.getUniqueTimestamp();

        reportAndLog(String.format("MonteScreenRecorder constructor with name: %s", name), MessageLevel.INFO);
        reportAndLog(String.format("MonteScreenRecorder constructor with videoName: %s", MonteScreenRecorder.videoName), MessageLevel.INFO);
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {

        reportAndLog("Entered createMovieFile method", MessageLevel.INFO);

        if (!movieFolder.exists()) {
            reportAndLog("Creating movie folder", MessageLevel.INFO);
            movieFolder.mkdirs();
        }
        else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }

        File file = new File(movieFolder,
                videoName + "." + Registry.getInstance().getExtension(fileFormat));

        reportAndLog(String.format("Recorded Screen Cast File is now created: %s", file.getAbsolutePath()), MessageLevel.INFO);

        return file;
    }

    //Starting Record
    public static void startRecord(String methodName) throws Exception {

        reportAndLog(String.format("Start record with method name: %s", methodName), MessageLevel.INFO);

        File file = new File("./report/videos/");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        Rectangle captureSize = new Rectangle(0, 0, width, height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice()
                .getDefaultConfiguration();

        screenRecorder = new MonteScreenRecorder(gc, captureSize,
                         new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                        Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                null, file, methodName);

        screenRecorder.start();
    }

    //Stop Record
    public static void stopRecord() throws Exception {
        screenRecorder.stop();
        reportAndLog("Recorded Screen Cast File Stop Recording",MessageLevel.INFO);
    }
}

