import org.junit.Assert;
import org.junit.Test;

public class StringComparatorTest {
    String numberOne;
    String numberTwo;
   StringComparatorTest stringComparatorTest;

    @Test
     public void verifyThatNumOneIsGreaterThanNumTwo() {
        // this is added here in case one want to compile the StringComparator itself
        if(getNumberTwo() == null || getNumberOne() == null) {
            numberOne = "1.2";
            numberTwo = "1.1";
        }

        String actualValue = StringComparator.compareNumbersConatainedIntoString(getNumberOne(), getNumberTwo());
        Assert.assertEquals(getNumberOne() + " is greater than " + getNumberTwo(), actualValue);
    }


    @Test
    public void verifyThatNumOneIsLowerThanNumTwo() {

        // this is added here in case one want to compile the StringComparator itself
        if( getNumberOne() == null || getNumberTwo() == null) {
            numberOne = "1.1";
            numberTwo = "1.2";
        }


        String actualValue = StringComparator.compareNumbersConatainedIntoString(getNumberOne(), getNumberTwo());
        Assert.assertEquals(getNumberOne() + " is lower than " + getNumberTwo(), actualValue);


    }

    @Test
    public void verifyThatNumOneIsEqualToNumTwo() {
        // this is added here in case one want to compile the StringComparator itself
        if(getNumberTwo() == null || getNumberOne() == null){
            numberOne = "1.1";
            numberTwo = "1.1";
        }

        String actualValue = StringComparator.compareNumbersConatainedIntoString(getNumberOne(), getNumberTwo());
        Assert.assertEquals( getNumberOne() + " is equal to " + getNumberTwo(), actualValue);

       }

    public String getNumberOne() {
        return numberOne;
    }

    public void setNumberOne(String numberOne) {
        this.numberOne = numberOne;
    }

    public String getNumberTwo() {
        return numberTwo;
    }

    public void setNumberTwo(String numberTwo) {
        this.numberTwo = numberTwo;
    }

}