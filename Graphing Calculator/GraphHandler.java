import java.util.ArrayList;
import java.util.Scanner;

//This class is the "main" of our program. It calculates all the data points and calls 
public class GraphHandler {

	private GraphFrame frame; //The frame
	private GraphThread thread; //The thread we are running this on
	private ArrayList<Coordinate> coordinates; //A list of each coordinate to graph
	
	//Construct objects, start the thread, run the graphing function
	public GraphHandler() {
		coordinates = new ArrayList<Coordinate>();
		frame = new GraphFrame(this);
		graphingCalculator();
		newThreadStart();
	}

	private void newThreadStart() {
		((Thread) (thread = new GraphThread(this))).start();
	}

	//Fills the coordinates ArrayList with points based on a function
	public void graphingCalculator(){
		
		printDirections();
		
		getCoordinates(getFormula(), getLeftBound(), getRightBound(), getStep());
		
		getScale(getLeftBound(), getRightBound());
		
		}

	private void getCoordinates(String formula, double leftBound, double rightBound, double step) {
		String revisedFormula;
		for (double i = leftBound; i <= rightBound; i += step) {
					String input = changeToString(i); 
					revisedFormula = replaceWithInput(formula, input); 
					addCoordinate(revisedFormula, i); 
		}
	}

	private String replaceWithInput(String formula, String input) {
		return formula.replaceAll("x", "(" + input + ")");
	}

	private void addCoordinate(String revisedFormula, double i) {
		coordinates.add(new Coordinate(i, evaluate(revisedFormula)));
	}

	private String changeToString(double i) {
		return Double.toString(i);
	}

	private double getStep() {
		Scanner scan = new Scanner(System.in);
		System.out.print("Step of your function (the increments at which the function is evaluated at: ");
		double step = scan.nextDouble();
		scan.close();
		return step;
	}

	private String getFormula() {
		Scanner scan = new Scanner(System.in);
		String formula = scan.nextLine(); //The formula
		scan.close();
		return formula;
	}

	private double getRightBound() {
		Scanner scan = new Scanner(System.in);
		System.out.print("Right bound of your domain? ");
		double rightBound = scan.nextDouble();
		scan.close();
		System.out.println();
		return rightBound;
	}

	private double getLeftBound() {
		Scanner scan = new Scanner(System.in);
		System.out.println();

		System.out.print("Left bound of your domain? ");
		double leftBound = scan.nextDouble();
		System.out.println();
		scan.close();
		return leftBound;
	}

	private void printDirections() {
		System.out.println(
				"Welcome to our graphing calcalculator!");
		System.out.println();
		System.out.println("Diections: When multiplying terms, always use the multiplication (*). Ex: 2x^2 should be 2*x^2");
		System.out.println();
		System.out.print("Please insert your equation in terms of x. Separate each term with a space (Ex: x^2 + 2*x + 3  \n You MUST include all signs (2x should be 2*x)): \ny = ");
	}

	
	public void getScale(double leftBound, double rightBound) {
		getGraphFrame().getGraphPanel().setXScale(rightBound - leftBound + 20);
	}
	
	
	public void updateGraphPanel() {
		repaintCanvas();
	}

	private void repaintCanvas() {
		getGraphFrame().getGraphPanel().repaint();
	}
	
	//Author: https://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
		private static double evaluate(final String mathExpression) {
		    return new Object() {
		        int position = -1, aChar;

		        void nextChar() {
		            aChar = getNextChar(mathExpression);
		        }

				private int getNextChar(final String mathExpression) {
					return (++position < mathExpression.length()) ? mathExpression.charAt(position) : -1;
				}

		        boolean eat(int charToEat) {
		            while (aChar == ' ') nextChar();
		            if (aChar == charToEat) {
		                nextChar();
		                return true;
		            }
		            return false;
		        }

		        double parse() {
		            nextChar();
		            double result = parseExpression();
		            if (position < mathExpression.length())
						unexpectedCharError();
		            return result;
		        }

		        // Grammar:
		        // expression = term | expression `+` term | expression `-` term
		        // term = factor | term `*` factor | term `/` factor
		        // factor = `+` factor | `-` factor | `(` expression `)`
		        //        | number | functionName factor | factor `^` factor

		        double parseExpression() {
		            double result = parseTerm();
		            for (;;) {
		                if      (eat('+')) result += parseTerm(); // addition
		                else if (eat('-')) result -= parseTerm(); // subtraction
		                else return result;
		            }
		        }

		        double parseTerm() {
		            double result = parseFactor();
		            for (;;) {
		                if      (eat('*')) result *= parseFactor(); // multiplication
		                else if (eat('/')) result /= parseFactor(); // division
		                else return result;
		            }
		        }

		        double parseFactor() {
		        	boolean isUnaryPlus=eat('+');
		        	boolean isUnaryMinus=eat('-');
		        	
		            if (isUnaryPlus) return parseFactor();
		            if (isUnaryMinus) return -parseFactor(); 

		            double result;
		            int startPos = this.position;
		            boolean isParentheses = eat('(');
		            boolean isNumbers = (aChar >= '0' && aChar <= '9') || aChar == '.';
		            boolean isFunctions = aChar >= 'a' && aChar <= 'z';
		            boolean isExponentiation = eat('^');
		            
		            if (isParentheses) { 
		                result = parseExpression();
		                eat(')');
		            } else if (isNumbers) {
		                while (isNumbers) nextChar();
		                result = changeToDouble(getSubString(mathExpression, startPos));
		            } else if (isFunctions) {
		                while (isFunctions) nextChar();
		                String function = getSubString(mathExpression, startPos);
		                result = parseFactor();
		                if (function.equals("sqrt")) result = Math.sqrt(result);
		                else if (function.equals("sin")) result = Math.sin(changeToRadians(result));
		                else if (function.equals("cos")) result = Math.cos(changeToRadians(result));
		                else if (function.equals("tan")) result = Math.tan(changeToRadians(result));
		                else throw new RuntimeException("Unknown function: " + function);
		            } else {
		                throw new RuntimeException("Unexpected: " + (char)aChar);
		            }

		            if (isExponentiation) result = getExponentiation(result); 

		            return result;
		        }

				private double changeToRadians(double result) {
					return Math.toRadians(result);
				}

				private String getSubString(final String mathExpression, int startPos) {
					return mathExpression.substring(startPos, this.position);
				}

				private double changeToDouble(final String Expression) {
					return Double.parseDouble(Expression);
				}

				private double getExponentiation(double result) {
					return Math.pow(result, parseFactor());
				}

				private void unexpectedCharError() {
					throw new RuntimeException("Unexpected: " + (char)aChar);
				}
		    }.parse();
		}

	public GraphFrame getGraphFrame() {
		return frame;
	}

	public GraphThread getGraphThread() {
		return thread;
	}

	public ArrayList<Coordinate> getCoordinates() {
		return coordinates;
	}

}
