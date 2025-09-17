package imageUtils;

import Managers.WebDriverInstanceManager;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *  Class that holds methods to handle images
 */
@SuppressWarnings("unused")
public class ImageUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static File generateBarChart(String title, String xAxisTitle, String yAxisTitle , Map<String, ? extends Number> data, String filePath) throws IOException {
        CategoryChart chart = new CategoryChartBuilder()
                .width(700).height(400)
                .title(title)
                .xAxisTitle(xAxisTitle).yAxisTitle(yAxisTitle)
                .build();

        List<String> dateLabels = new ArrayList<>();
        List<Number> rateValues = new ArrayList<>();

        for (Map.Entry<String, ? extends Number> entry : data.entrySet()) {
            String date = entry.getKey();
            dateLabels.add(date);

            rateValues.add(entry.getValue());
        }

        chart.addSeries("Trend",
                        dateLabels,
                        rateValues)
                .setMarker(SeriesMarkers.CIRCLE);

        BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
        return new File(filePath);
    }

    public static File generateLineChart(String title, String xAxisTitle, String yAxisTitle,
                                         Map<String, ? extends Number> data, String filePath) throws IOException {

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(450)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .build();

        List<String> sortedLabels = new ArrayList<>(data.keySet());
        List<Double> xValues = new ArrayList<>();
        List<Number> yValues = new ArrayList<>();
        Map<Double, String> labelMap = new HashMap<>();

        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (int i = 0; i < sortedLabels.size(); i++) {
            Number y = data.get(sortedLabels.get(i));
            double yVal = y.doubleValue();

            xValues.add((double) i);
            yValues.add(yVal);
            labelMap.put((double) i, sortedLabels.get(i));

            minY = Math.min(minY, yVal);
            maxY = Math.max(maxY, yVal);
        }

        chart.addSeries("Trend", xValues, yValues).setMarker(SeriesMarkers.CIRCLE);

        chart.setCustomXAxisTickLabelsFormatter(labelMap::get);
        chart.getStyler().setXAxisLabelRotation(45);
        chart.getStyler().setYAxisMin(minY);
        chart.getStyler().setYAxisMax(maxY);
        chart.getStyler().setYAxisTicksVisible(true);
        chart.getStyler().setYAxisDecimalPattern("0.00"); // ensures 99.96 isn't shown as 100

        BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
        return new File(filePath);
    }

    // Helper to map indices to date strings for x-axis
    private static Map<Double, Object> buildTickLabelMap(List<Integer> xValues, List<String> labels) {
        Map<Double, Object> map = new HashMap<>();
        for (int i = 0; i < xValues.size(); i++) {
            map.put(xValues.get(i).doubleValue(), labels.get(i));
        }
        return map;
    }

    /** image verification method to verify if images are the same.
     * before using this method an image of the element needs to be created once so that future comparison will work.
     * example of how to create image to compare too:
     * Screenshot logoImageScreenshot = new AShot().takeScreenshot(driver, draftIcon);
     * ImageIO.write(logoImageScreenshot.getImage(),"png",new File(REPORT_IMAGE_FOLDER_PATH + "LinuxDraftIcon.png"));
     * pay attention that the image you create localy is not the same that is captured when running with linux.
     * to create the linux image run the capture images lines (24-25) with the desired path using unit test.
     * save the image that was created on <a href="https://devops.corp.zim.com/selenium_unittests/l2a_uiux_new/qa0/seperated_reports/images/">...</a> to the ExpectedImagePath
     * @param imageElement the element of the image
     * @param ExpectedImagePath the path of the image to compare too
     * @return true if there is a difference or false if not
     * @author umflat.lior
     * @since 1.1.2022
     */
    public static boolean verifyImageElement(WebElement imageElement, String ExpectedImagePath) throws Exception {
        BufferedImage expectedImage;
        try
        {
            expectedImage = ImageIO.read(new File(ExpectedImagePath));
        }
        catch (Exception e)
        {
            logger.info("Error Reading Image File: " + ExpectedImagePath);
            throw new Exception("Error Reading Image File: " + ExpectedImagePath);
        }

        Screenshot imageScreenShot = new AShot().takeScreenshot(WebDriverInstanceManager.getDriverFromMap(),imageElement);
        BufferedImage actualImage = imageScreenShot.getImage();
        ImageDiffer imageDiff = new ImageDiffer();
        ImageDiff diff = imageDiff.makeDiff(actualImage,expectedImage);
        //return true if there is a difference or false if not
        return diff.hasDiff();
    }
}
