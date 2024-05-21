package overlap;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		var quit = true;
		Line line = null, newLine = null;
		while (quit){


			var scanner = new Scanner(System.in);
			System.out.println("----------Menu-------------");
			System.out.println("1-)create Lines\n2-)check if they overlap\n3-)quit");
			var choice = scanner.nextLine();

			switch (choice){
				case "1" -> {
					System.out.println("1-)Enter Line one coordinates (e.g: 1 10)");
					var choiceOne = scanner.nextLine();
					var splitOne = choiceOne.split(" ");
					var start = Integer.parseInt(splitOne[0]);
					var end = Integer.parseInt(splitOne[1]);

					while ( start > end) {
						System.out.println("startLine must be inferior to endLine");
						choiceOne = scanner.nextLine();
						splitOne = choiceOne.split(" ");
						start = Integer.parseInt(splitOne[0]);
						end = Integer.parseInt(splitOne[1]);
					}
					line = new Line(start, end);
					line.setSelf(line);

					System.out.println("2-)Enter Line Two coordinates (e.g: 5 15)");
					var choiceOneS = scanner.nextLine();
					splitOne = choiceOneS.split(" ");
					start = Integer.parseInt(splitOne[0]);
					end = Integer.parseInt(splitOne[1]);

					while (start > end){
						System.out.println("startLine must be inferior to endLine");
						choiceOneS = scanner.nextLine();
						splitOne = choiceOneS.split(" ");
						start = Integer.parseInt(splitOne[0]);
						end = Integer.parseInt(splitOne[1]);
					}
					newLine = new Line(start, end);
					newLine.setSelf(newLine);
				}
				case "2" ->{
					try{

						if (line != null) {
							System.out.println(Line.overlaps(line.getSelf(),newLine.getSelf()));
						}

					}catch (Exception e){
						System.out.println("Set Lines first");
					}

				}
				case "3" ->{
					quit = false;
					System.out.println("Astaluego");
				}
				default -> System.out.println("make a choice");
			}



		}

	}

}
