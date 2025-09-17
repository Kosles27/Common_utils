package drivers;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle invokation of delegate elements. Will receive any
 * legal WebElemet list and create a TesnetWebElement list wrapping the original list
 * @since July 2021
 * @author Nir Gallner
 */
public interface DelegateElements {

    /**
     * get list of WebElements and return list of DelegateRemoteWebElement
     * @param elements
     * @return List of Tesnet web elements
     */
    default List<WebElement> invokeDelegateElementsList(List<WebElement> elements){
        List<WebElement> newElements = new ArrayList<WebElement>();
        for (int i = 0; i < elements.size(); i++)
            newElements.add(new TesnetWebElement(elements.get(i)));
        return newElements;
    }

}
