public class StringComparator {

    private static String numOne;

    private static String numTwo;

    public static String compareNumbersConatainedIntoString(String strOne, String strTwo) {
        // since this static method we need to set the va
        setNumOne(strOne);
        setNumTwo(strTwo);

        try {
            if (Double.parseDouble(strOne) > Double.parseDouble(strTwo))
                return strOne + " is greater than " + strTwo;

            if (Double.parseDouble(strOne) < Double.parseDouble(strTwo))
                return strOne + " is lower than " + strTwo;

            return strOne + " is equal to " + strTwo;

        } catch (Exception e) {
            return "takes only String numbers";
        }
    }

    public static String getNumOne() {
        return numOne;
    }

    public static void setNumOne(String numOne) {
        StringComparator.numOne = numOne;
    }

    public static String getNumTwo() {
        return numTwo;
    }

    public static void setNumTwo(String numTwo) {
        StringComparator.numTwo = numTwo;
    }
}
