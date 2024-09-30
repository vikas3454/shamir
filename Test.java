package json;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ffff {

    public static void main(String[] args) throws IOException, ParseException {
        // Test case 1 and Test case 2 input files
    	String testCase1Path = ".\\Json files\\file.json"; // Adjust path as needed
        String testCase2Path = ".\\Json files\\file2.json";// Adjust path as needed
        
        // Process both test cases
        processTestCase(testCase1Path);
        processTestCase(testCase2Path);
    }

    private static void processTestCase(String filePath) throws IOException, ParseException {
        // Parse JSON input
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(filePath);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

        // Extract keys (n and k values)
        JSONObject keys = (JSONObject) jsonObject.get("keys");
        int n = Integer.parseInt(String.valueOf(keys.get("n")));
        int k = Integer.parseInt(String.valueOf(keys.get("k")));

        // Extract x and y values (roots)
        List<Double> xValues = new ArrayList<Double>();
        List<Double> yValues = new ArrayList<Double>();
        Map<Double, Double> originalPoints = new HashMap<Double, Double>();

        for (int i = 1; i <= n; i++) {
            if (jsonObject.containsKey(String.valueOf(i))) {
                JSONObject root = (JSONObject) jsonObject.get(String.valueOf(i));

                // Extract base and value
                int base = Integer.parseInt((String) root.get("base"));
                String value = (String) root.get("value");

                // Decode the y value based on the base
                BigInteger decodedValue = new BigInteger(value, base);
                xValues.add((double) i); // x-values are the keys 1, 2, 3, ...
                yValues.add(decodedValue.doubleValue()); // decoded y-value
                originalPoints.put((double) i, decodedValue.doubleValue()); // Store original points for later
            }
        }

        // Ensure we have enough points to solve the polynomial
        if (xValues.size() < k) {
            System.out.println("Not enough points to solve the polynomial.");
            return;
        }

        // Solve the polynomial using Lagrange interpolation
        double secretC = lagrangeInterpolation(xValues, yValues);
        System.out.println("The secret constant term (c) is: " + secretC);

        // Find wrong points
        if (filePath.contains("test_case_2")) {
            List<Double> wrongPoints = findWrongPoints(originalPoints, xValues, yValues);
            System.out.println("Wrong points: " + wrongPoints);
        }
    }

    // Lagrange interpolation method
    public static double lagrangeInterpolation(List<Double> x, List<Double> y) {
        double secret = 0.0;
        int n = x.size();

        for (int i = 0; i < n; i++) {
            double term = y.get(i);
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term = term * (0 - x.get(j)) / (x.get(i) - x.get(j)); // Lagrange basis polynomial
                }
            }
            secret += term;
        }

        return secret;
    }

    // Find wrong points that do not lie on the polynomial curve
    private static List<Double> findWrongPoints(Map<Double, Double> originalPoints, List<Double> xValues, List<Double> yValues) {
        List<Double> wrongPoints = new ArrayList<Double>();

        // Loop through each x-value to check if the corresponding y-value lies on the curve
        for (int i = 0; i < xValues.size(); i++) {
            Double x = xValues.get(i);
            Double actualY = originalPoints.get(x);
            
            // Use Lagrange interpolation to calculate the expected y-value at this x
            double expectedY = lagrangeInterpolationWithFixedX(xValues, yValues, x);

            // If the expected y-value doesn't match the actual one, mark it as a wrong point
            if (Math.abs(expectedY - actualY) > 1e-6) { // Allow a small margin for floating-point comparison
                wrongPoints.add(x);
            }
        }

        return wrongPoints;
    }

    // Helper function to compute the expected y-value for a given x using Lagrange interpolation
    private static double lagrangeInterpolationWithFixedX(List<Double> x, List<Double> y, Double xVal) {
        double result = 0.0;
        int n = x.size();

        for (int i = 0; i < n; i++) {
            double term = y.get(i);
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term = term * (xVal - x.get(j)) / (x.get(i) - x.get(j)); // Lagrange basis polynomial for x = xVal
                }
            }
            result += term;
        }

        return result;
    }
}

