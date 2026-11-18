
import java.util.Scanner;
import java.util.Locale;

class PolynomialCalculator {

    // Node class to store coefficient and exponent
    static class Node {
        double coeff;
        int exp;
        Node next;

        Node(double c, int e) {
            coeff = c;
            exp = e;
            next = null;
        }
    }

    // Head pointer of linked list
    Node head = null;

    static final double EPS = 0.0000001;

    // Insert a term in descending order of exponent
    void insert(double coeff, int exp) {

        if (Math.abs(coeff) < EPS)
            return;

        Node ptr = head;
        Node prev = null;

        // Find correct position
        while (ptr != null && ptr.exp > exp) {
            prev = ptr;
            ptr = ptr.next;
        }

        // If exponent already exists, add coefficients
        if (ptr != null && ptr.exp == exp) {

            ptr.coeff += coeff;

            // Remove node if coefficient becomes zero
            if (Math.abs(ptr.coeff) < EPS) {

                if (prev == null)
                    head = ptr.next;
                else
                    prev.next = ptr.next;
            }

            return;
        }

        // Create and insert new node
        Node newNode = new Node(coeff, exp);

        if (prev == null) {
            newNode.next = head;
            head = newNode;
        } else {
            newNode.next = ptr;
            prev.next = newNode;
        }
    }

    // Create polynomial
    void create(Scanner sc) {

        head = null;

        System.out.print("Enter number of terms: ");
        int n = sc.nextInt();

        for (int i = 1; i <= n; i++) {

            System.out.print("Enter coefficient: ");
            double c = sc.nextDouble();

            System.out.print("Enter exponent: ");
            int e = sc.nextInt();

            if (e < 0) {
                System.out.println("Exponent cannot be negative.");
                i--;
                continue;
            }

            insert(c, e);
        }
    }

    // Format coefficient for display
    static String format(double value) {

        if (Math.abs(value) < EPS)
            value = 0;

        return String.format(Locale.US, "%.4f", value)
                     .replaceAll("0+$", "")
                     .replaceAll("\\.$", "");
    }

    // Display polynomial
    void display() {

        if (head == null) {
            System.out.println("0");
            return;
        }

        Node ptr = head;
        boolean first = true;

        while (ptr != null) {

            double c = ptr.coeff;

            if (!first) {
                if (c >= 0)
                    System.out.print(" + ");
                else
                    System.out.print(" - ");
            } else if (c < 0) {
                System.out.print("-");
            }

            double absCoeff = Math.abs(c);

            // Display coefficient
            if (ptr.exp == 0 || Math.abs(absCoeff - 1) > EPS) {
                System.out.print(format(absCoeff));
            }

            // Display variable and exponent
            if (ptr.exp > 0) {
                System.out.print("x");

                if (ptr.exp > 1)
                    System.out.print("^" + ptr.exp);
            }

            first = false;
            ptr = ptr.next;
        }

        System.out.println();
    }

    // Copy polynomial
    PolynomialCalculator copy() {

        PolynomialCalculator result = new PolynomialCalculator();

        Node ptr = head;

        while (ptr != null) {
            result.insert(ptr.coeff, ptr.exp);
            ptr = ptr.next;
        }

        return result;
    }

    // Addition of two polynomials
    static PolynomialCalculator add(
        PolynomialCalculator p1,
        PolynomialCalculator p2) {

        PolynomialCalculator result = p1.copy();

        Node ptr = p2.head;

        while (ptr != null) {
            result.insert(ptr.coeff, ptr.exp);
            ptr = ptr.next;
        }

        return result;
    }

    // Subtraction of two polynomials
    static PolynomialCalculator subtract(
        PolynomialCalculator p1,
        PolynomialCalculator p2) {

        PolynomialCalculator result = p1.copy();

        Node ptr = p2.head;

        while (ptr != null) {
            result.insert(-ptr.coeff, ptr.exp);
            ptr = ptr.next;
        }

        return result;
    }

    // Multiplication of two polynomials
    static PolynomialCalculator multiply(
        PolynomialCalculator p1,
        PolynomialCalculator p2) {

        PolynomialCalculator result = new PolynomialCalculator();

        Node ptr1 = p1.head;

        while (ptr1 != null) {

            Node ptr2 = p2.head;

            while (ptr2 != null) {

                double c = ptr1.coeff * ptr2.coeff;
                int e = ptr1.exp + ptr2.exp;

                result.insert(c, e);

                ptr2 = ptr2.next;
            }

            ptr1 = ptr1.next;
        }

        return result;
    }

    // Division of two polynomials
    // Returns quotient and remainder
    static PolynomialCalculator[] divide(
        PolynomialCalculator p1,
        PolynomialCalculator p2) {

        PolynomialCalculator quotient = new PolynomialCalculator();

        PolynomialCalculator remainder = p1.copy();

        if (p2.head == null) {
            System.out.println("Division by zero polynomial is not possible.");
            return null;
        }

        // Polynomial long division
        while (remainder.head != null &&
               remainder.head.exp >= p2.head.exp) {

            double c = remainder.head.coeff / p2.head.coeff;

            int e = remainder.head.exp - p2.head.exp;

            // Add term to quotient
            quotient.insert(c, e);

            Node ptr = p2.head;

            // Subtract divisor multiplied by quotient term
            while (ptr != null) {

                double newCoeff = -c * ptr.coeff;
                int newExp = e + ptr.exp;

                remainder.insert(newCoeff, newExp);

                ptr = ptr.next;
            }
        }

        return new PolynomialCalculator[] {
            quotient, remainder
        };
    }

    // Evaluate polynomial for a given value of x
    double evaluate(double x) {

        double result = 0;

        Node ptr = head;

        while (ptr != null) {

            result += ptr.coeff * Math.pow(x, ptr.exp);

            ptr = ptr.next;
        }

        return result;
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        PolynomialCalculator p1 = new PolynomialCalculator();
        PolynomialCalculator p2 = new PolynomialCalculator();

        PolynomialCalculator result;

        int choice;

        do {

            System.out.println("\n===== POLYNOMIAL CALCULATOR =====");

            System.out.println("1. Create First Polynomial");
            System.out.println("2. Create Second Polynomial");
            System.out.println("3. Display Polynomials");
            System.out.println("4. Addition");
            System.out.println("5. Subtraction");
            System.out.println("6. Multiplication");
            System.out.println("7. Division");
            System.out.println("8. Evaluate Polynomial");
            System.out.println("9. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("\nCreate First Polynomial:");
                    p1.create(sc);
                    break;

                case 2:
                    System.out.println("\nCreate Second Polynomial:");
                    p2.create(sc);
                    break;

                case 3:
                    System.out.print("First Polynomial: ");
                    p1.display();

                    System.out.print("Second Polynomial: ");
                    p2.display();
                    break;

                case 4:
                    result = add(p1, p2);

                    System.out.print("Addition Result: ");
                    result.display();
                    break;

                case 5:
                    result = subtract(p1, p2);

                    System.out.print("Subtraction Result: ");
                    result.display();
                    break;

                case 6:
                    result = multiply(p1, p2);

                    System.out.print("Multiplication Result: ");
                    result.display();
                    break;

                case 7:

                    PolynomialCalculator[] division =
                        divide(p1, p2);

                    if (division != null) {

                        System.out.print("Quotient: ");
                        division[0].display();

                        System.out.print("Remainder: ");
                        division[1].display();
                    }

                    break;

                case 8:

                    System.out.print(
                        "Enter 1 to evaluate first polynomial or 2 for second: ");

                    int ch = sc.nextInt();

                    System.out.print("Enter value of x: ");
                    double x = sc.nextDouble();

                    if (ch == 1) {
                        System.out.println(
                            "Result: " + format(p1.evaluate(x)));
                    } else if (ch == 2) {
                        System.out.println(
                            "Result: " + format(p2.evaluate(x)));
                    } else {
                        System.out.println("Invalid choice!");
                    }

                    break;

                case 9:
                    System.out.println(
                        "Thank you for using Polynomial Calculator!");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 9);

        sc.close();
    }
}

