package com.argos999;


import com.google.common.collect.Ordering;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppTest {
    public static WebDriver driver;
    public static String expected;
    public static String expected1;
    public static String wishList1;
    public static String location1;

    @Before
    public void launchApp() {
        //setup the Chrome browser
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        //get the url
        driver.get("https://www.argos.co.uk/");
        //maximize the window
        driver.manage().window().maximize();
        //implicit wait for web elements
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        //page load time out for loading the page
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        //delete all cookies
        driver.manage().deleteAllCookies();
    }

    @After
    public void closeApp() {
        //close appdriver.quit();
    }

    @Test
    public void getUrlTest() {
        //get the current url
        String actualUrl = getCurrentUrl();
        assertThat("different Home page " + actualUrl, Matchers.endsWith("co.uk/"));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Test
    public void logInTest() {
        logIn("reshmareddyinjeti@gmail.com", "sandipani555");
        //String actual = logInsuccess();
        //assertThat("user not able to see user name :", actual, Matchers.startsWith("Hello"));

    }


    public void logIn(String email, String password) {
        driver.findElement(By.linkText("Account")).click();
        driver.findElement(By.id("email-address")).sendKeys(email);
        driver.findElement(By.id("current-password")).sendKeys(password);
        driver.findElement(By.cssSelector(".sign-in-form")).click();
    }

    public String logInsuccess() {

        return driver.findElement(By.cssSelector(".dc-menu-trigger")).getText();
    }


    public void doSearchProductTest(String customerSelectedProduct) {
        driver.findElement(By.id("searchTerm")).sendKeys(customerSelectedProduct);
        driver.findElement(By.cssSelector("._2mKaC")).click();
    }

    @Test
    public void filterRatingTest() throws InterruptedException {
        doSearchProductTest("nike");
        selectARating("4 or more");
        List<Double> actualList = getAllRatingsOnFilteredProduct();
        assertThat("List is storing wrong value or filter broken. ", actualList, everyItem(greaterThanOrEqualTo(4.0)));
    }

    @Test
    public void FilterPriceTest() throws InterruptedException {
        doSearchProductTest("nike");
        selectCustomerPrice("£15 - £20");
        List<Double> actualList = getAllPricesOnFilterProduct();
        assertThat("List is sorting wrong value .", actualList, everyItem(greaterThanOrEqualTo(15.00)));
        assertThat("List is sorting wrong value.", actualList, everyItem(lessThanOrEqualTo(20.0)));
    }

    @Test
    public void sortByTest() throws InterruptedException {
        doSearchProductTest("nike");
        customerSortedProduct("Price: Low - High");
        List<Double> actual = getAllSortedProductOnPrice();
        boolean sorted = Ordering.natural().isOrdered(actual);
        assertThat("Price is not sorted. ", sorted, is(equalTo(true)));
    }


    public void selectARating(String customerSelectedRating) throws InterruptedException {
        Thread.sleep(3000);
        List<WebElement> customerRatings = driver.findElements(By.cssSelector(".ac-accordion .ac-facet__label--rating"));
        for (WebElement ratingWebElement : customerRatings) {
            if (ratingWebElement.getText().equalsIgnoreCase(customerSelectedRating)) {
                new WebDriverWait(driver, 20)
                        .until(ExpectedConditions.elementToBeClickable(ratingWebElement));
                ratingWebElement.click();
                break;

            }
        }
    }

    public List<Double> getAllRatingsOnFilteredProduct() {

        List<Double> collectedRating = new ArrayList<>();

        List<WebElement> ratingWebElements = driver.findElements(By.cssSelector(".ac-star-rating"));
        for (WebElement ratingWedelement : ratingWebElements) {
            String ratingInSting = ratingWedelement.getAttribute("data-star-rating");
            double ratingInDouble = Double.parseDouble(ratingInSting);
            System.out.println("Collected rating :" + collectedRating);
            collectedRating.add(ratingInDouble);

        }
        return collectedRating;
    }

    //price filter test
    public void selectCustomerPrice(String customerSelectedPrice) {
        //finding the list wedElements for price
        List<WebElement> priceWebElements = driver.findElements(By.cssSelector(".ac-facet__filters .ac-facet__label--default"));
        for (WebElement priceWebElement : priceWebElements) {
            if (priceWebElement.getText().equalsIgnoreCase(customerSelectedPrice)) {
                priceWebElement.click();
                break;
            }
        }
    }

    //assertion for price test
    public List<Double> getAllPricesOnFilterProduct() throws InterruptedException {
        Thread.sleep(3000);
        List<Double> collectedPrice = new ArrayList<>();
        List<WebElement> priceWebelements = driver.findElements(By.cssSelector(".ac-product-price__amount"));

        for (WebElement priceWebelement : priceWebelements) {
            //get the text of price webelement
            String priceInString = priceWebelement.getText().replace("£", "");
            System.out.println(priceInString);
            //converting the variable string to double
            double priceInDouble = Double.parseDouble(priceInString);
            collectedPrice.add(priceInDouble);
        }
        return collectedPrice;
    }


    public void customerSortedProduct(String customerSelectedpSort) throws InterruptedException {
        WebElement sortSelect = driver.findElement(By.cssSelector(".sort-icon"));
        //sortSelect.click();
        Select select = new Select(sortSelect);
        Thread.sleep(3000);
        select.selectByVisibleText(customerSelectedpSort);

    }

    public List<Double> getAllSortedProductOnPrice() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Double> collectedSort = new ArrayList<>();
        List<WebElement> priceAmount = driver.findElements(By.cssSelector(".ac-product-price__amount"));
        for (WebElement sortByWebElement : priceAmount) {
            String sortInString = sortByWebElement.getText();
            double sortInDouble = Double.parseDouble(sortInString);
            System.out.println(collectedSort);
            collectedSort.add(sortInDouble);
        }
        return collectedSort;

    }

    @Test
    public void basketTest() throws InterruptedException {
        doSearchProductTest("nike");
        String actual = BasketMethod();
        List<String> expected = assertForBasket();
        System.out.println(expected);
        assertThat(expected, Matchers.hasItem(actual));
        // dropDown();

    }

    public int random(int size) {
        //random method
        Random random = new Random();
        return random.nextInt(size - 1);
    }

    public String BasketMethod() throws InterruptedException {
        //fined the web element for all product and stored in list
        List<WebElement> listOfProducts = driver.findElements(By.cssSelector(".ProductCardstyles__Title-l8f8q8-11.kLyOND"));
        int sizeOfProduct = listOfProducts.size();
        int randomProduct = random(sizeOfProduct);
        String randomProductInString = listOfProducts.get(randomProduct).getText();
        privacyfooter();
        Thread.sleep(3000);
        listOfProducts.get(randomProduct).click();
        //Thread.sleep(3000);
       // driver.findElement(By.cssSelector(".add-to-trolley-main .Buttonstyles__Button-q93iwm-2")).click();
       // driver.findElement(By.linkText("Go to Trolley")).click();


        return randomProductInString;
    }

    public void privacyfooter() {
        driver.findElement(By.cssSelector(".privacy-prompt-footer")).click();
    }

    public List<String> assertForBasket() {
        List<String> collectedProduct = new ArrayList<>();
        List<WebElement> productInBasket = driver.findElements(By.cssSelector(".ProductCard__title__2s1rf .ProductCard__titleLink__1PgaZ"));
        for (WebElement productInBaskets : productInBasket) {
            String productInString = productInBaskets.getText();
            System.out.println(productInString);
            collectedProduct.add(productInString);
        }
        return collectedProduct;

    }

    @Test
    public void quantityTest() throws InterruptedException {
        doSearchProductTest("nike");
        BasketMethod();
        // dropDown();
        removeFromBasket();
        String actual = emptyTrolly();
        assertThat(actual, Matchers.equalToIgnoringCase(expected));
        System.out.println(expected = actual);


    }

    public void dropDown() throws InterruptedException {
        WebElement a = driver.findElement(By.cssSelector(".ProductCard__quantitySelect__2y1R3"));
        Select select = new Select(a);
        //select.selectByValue(String.valueOf(3));
        select.selectByValue(String.valueOf(7));


    }

    public void isSelected() throws InterruptedException {
        Thread.sleep(3000);
        // List<WebElement> price= driver.findElements(By.cssSelector(".ProductCard__titleLink__1PgaZ"));
        List<WebElement> price = driver.findElements(By.cssSelector(".ProductCard__quantitySelect__2y1R3"));
        int size = price.size();
        System.out.println(size);


    }

    public void removeFromBasket() throws InterruptedException {
        Thread.sleep(3000);
        driver.findElement(By.cssSelector(".ProductCard__removeButton__2O6Cw")).click();
    }

    public String emptyTrolly() {
        String e = driver.findElement(By.cssSelector(".EmptyBasketPanel__title__2L-Wf")).getText();
        expected = e;
        return expected;
    }

    @Test
    public void undo() throws InterruptedException {
        doSearchProductTest("nike");
        BasketMethod();
        removeFromBasket();
        emptyTrolly();
        undoMethod();
        String actual = undoAssert();
        assertThat(expected1, Matchers.equalToIgnoringCase(actual));}

    public void undoMethod() {
        driver.findElement(By.cssSelector(".ProductCard__undoButton__2jMy1")).click();
    }

    public String undoAssert() {
        String expected2= driver.findElement(By.cssSelector(".Title__title__34rH4")).getText();
         expected1=expected2;
         return expected1;
    }

    @Test
    public void wishList() throws InterruptedException {
        doSearchProductTest("nike");
        BasketMethod();
        wishListMethod();
        clickWishList();
        String actual=wishListAssert();
        assertThat(actual,Matchers.equalToIgnoringCase(wishList1));

    }
    public void wishListMethod() {
        driver.findElement(By.linkText("Add to Your Wishlist")).click();
    }
    public void clickWishList(){
        Actions actions=new Actions(driver);
        actions.moveToElement(driver.findElement(By.linkText("Wishlist"))).click().build().perform();
    }
    public String wishListAssert(){
        String wishListheader=driver.findElement(By.cssSelector(".wishlist--heading")).getText();
        wishList1=wishListheader;
        return wishList1;

    }
    @Test
    public void storeLocation(){
        storeLocationMethod("Hounslow");
       String actual= loctionAssert();
       assertThat(actual,Matchers.equalToIgnoringCase(location1));


    }
    public void storeLocationMethod(String location){
        driver.findElement(By.linkText("Stores")).click();
        driver.findElement(By.id("searchbox")).sendKeys(location+ Keys.ENTER);
      //  driver.findElement(By.cssSelector(".sc-search-bar-icon")).click();

    }
    public String loctionAssert(){
       String actualLocation= driver.findElement(By.cssSelector(".sc-store-name")).getText();
        location1=actualLocation;
        return location1;
    }
    @Test
    public void help(){
        helpMethod("Do I get longer than 30 days to return or exchange Christmas gifts?");
       String actual= helpAssertion();
        System.out.println(actual);
        assertThat(actual,Matchers.containsString("search results for"));

    }
    public void helpMethod(String message)
    {
        driver.findElement(By.linkText("Help")).click();
        driver.findElement(By.cssSelector(".ac-search-bar__input--icon")).sendKeys(message);
        driver.findElement(By.cssSelector(".button--primary")).click();
    }
    private String helpAssertion(){
      return  driver.findElement(By.cssSelector(".page__title--search")).getText();

    }
@Test
public void shop()throws InterruptedException{
       shopLIn();
       String actual=shopLinkAssertion();
    System.out.println(actual);
    assertThat(actual,Matchers.equalToIgnoringCase("Bedroom Furniture"));

}


    public void shopLink(String shopLinks) {
        privacyfooter();
        driver.findElement(By.cssSelector("#ShopLink")).click();
        List<WebElement> listOfLinkes = driver.findElements(By.cssSelector("._2Dr6a ._33QNI"));
        System.out.println(listOfLinkes);
        for (WebElement listOfLink : listOfLinkes) {
            if (listOfLink.getText().equalsIgnoreCase(shopLinks)) {
                listOfLink.click();
                break;
            }
        }
    }
    public void shopLIn()throws InterruptedException {
        privacyfooter();
        driver.findElement(By.cssSelector("#ShopLink")).click();
        Actions actions = new Actions(driver);
        Thread.sleep(3000);
        actions.moveToElement(driver.findElement(By.linkText("Home & Furniture"))).build().perform();
        Thread.sleep(3000);

        actions.moveToElement(driver.findElement(By.linkText("Bedroom Furniture"))).click().build().perform();

    }
    public String shopLinkAssertion(){
       return driver.findElement(By.cssSelector(".browse__category-name")).getText();
    }
}





