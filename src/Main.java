import org.junit.ComparisonFailure;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        var quit = true;
        var numOne = "";
        var numTwo = "";
        var stringComparatorTest = new StringComparatorTest();
        while (quit){


            var scanner = new Scanner(System.in);
            System.out.println("----------Menu-------------");
            System.out.println("""
                    1-)Compare Numbers
                    2-)verify That NumOne is Greater Than NumTwo
                    3-)verify That NumOne is Lower Than NumTwo
                    4-)verify That NumOne is equal To NumTwo
                    5-)quit""");
            var choice = scanner.nextLine();

            switch (choice){
                case "1" -> {
                    System.out.println("1-)Enter values (e.g: 1.2 1.1 or 1 2)");
                    choice = scanner.nextLine();
                    var split = choice.split(" ");
                    numOne = split[0];
                    numTwo = split[1];
                    System.out.println(StringComparator.compareNumbersConatainedIntoString(numOne, numTwo));
                    stringComparatorTest.setNumberOne(numOne);
                    stringComparatorTest.setNumberTwo(numTwo);

                    break;
                }
                case "2" ->{
                    System.out.println("Verifying numOne is greater than numTwo...");
                    try {
                        stringComparatorTest.verifyThatNumOneIsGreaterThanNumTwo();
                        System.out.println("numOne is indeed greater than numTwo");
                    }catch (ComparisonFailure e){
                        System.out.println("false");
                    }

                    break;
                }
                case "3" ->{
                    System.out.println("Verifying numOne is lower than numTwo...");
                    try {
                        stringComparatorTest.verifyThatNumOneIsLowerThanNumTwo();
                        System.out.println("numOne is indeed lower than numTwo");
                    }catch (ComparisonFailure e){
                        System.out.println("false");
                    }
                    break;
                }
                case "4" ->{

                    System.out.println("Verifying numOne is equal to numTwo...");
                    try {
                        stringComparatorTest.verifyThatNumOneIsEqualToNumTwo();
                        System.out.println("numOne is indeed equal to numTwo");
                    }catch (ComparisonFailure e){
                        System.out.println("false");
                    }
                    break;
                }
                case "5" ->{
                    quit = false;
                    System.out.println("Astaluego");
                    break;
                }
                default -> System.out.println("make a choice");
            }



        }

    }
}