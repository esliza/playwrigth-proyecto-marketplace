package com.example.screenplay.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CsvDataReader {

    /*
     * Legacy: Reads src/test/resources/testdata/purchase-data.csv and returns
     * Stream<Arguments>
     * Columns: testName,product,quantity
     *
     * Kept commented because current tests use scenario-specific CSVs.
     */
    // public static Stream<Arguments> purchaseData() throws Exception {
    // InputStream is = Thread.currentThread().getContextClassLoader()
    // .getResourceAsStream("testdata/purchase-data.csv");
    // if (is == null) {
    // throw new IllegalStateException("Resource testdata/purchase-data.csv not
    // found");
    // }
    // List<Arguments> out = new ArrayList<>();
    // try (BufferedReader br = new BufferedReader(new InputStreamReader(is,
    // StandardCharsets.UTF_8))) {
    // String header = br.readLine(); // skip header
    // String line;
    // while ((line = br.readLine()) != null) {
    // if (line.trim().isEmpty())
    // continue;
    // String[] parts = line.split(",");
    // if (parts.length < 3)
    // continue;
    // String testName = parts[0].trim();
    // String product = parts[1].trim();
    // int qty = Integer.parseInt(parts[2].trim());
    // out.add(Arguments.of(testName, product, qty));
    // }
    // }
    // return out.stream();
    // }

    /*
     * Legacy: Reads src/test/resources/testdata/login-data.csv and returns
     * Stream<Arguments>
     * Columns: email,password
     *
     * Not used currently (we use loginSuccessData/loginFailureData). Kept
     * commented for possible future use.
     */
    // public static Stream<Arguments> loginData() throws Exception {
    // InputStream is = Thread.currentThread().getContextClassLoader()
    // .getResourceAsStream("testdata/login-data.csv");
    // if (is == null) {
    // throw new IllegalStateException("Resource testdata/login-data.csv not
    // found");
    // }
    // List<Arguments> out = new ArrayList<>();
    // try (BufferedReader br = new BufferedReader(new InputStreamReader(is,
    // StandardCharsets.UTF_8))) {
    // String header = br.readLine(); // skip header
    // String line;
    // while ((line = br.readLine()) != null) {
    // if (line.trim().isEmpty())
    // continue;
    // String[] parts = line.split(",");
    // if (parts.length < 2)
    // continue;
    // String email = parts[0].trim();
    // String password = parts[1].trim();
    // out.add(Arguments.of(email, password));
    // }
    // }
    // return out.stream();
    // }

    /**
     * Reads src/test/resources/testdata/login-success.csv and returns
     * Stream<Arguments>
     * Columns: email,password
     */
    public static Stream<Arguments> loginSuccessData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/login-data.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/login-data.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 3)
                    continue;
                String email = parts[0].trim();
                String password = parts[1].trim();
                String expected = parts[2].trim();
                if ("success".equalsIgnoreCase(expected)) {
                    out.add(Arguments.of(email, password));
                }
            }
        }
        return out.stream();
    }

    /**
     * Reads src/test/resources/testdata/login-failure.csv and returns
     * Stream<Arguments>
     * Columns: email,password
     */
    public static Stream<Arguments> loginFailureData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/login-data.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/login-data.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 3)
                    continue;
                String email = parts[0].trim();
                String password = parts[1].trim();
                String expected = parts[2].trim();
                if ("failure".equalsIgnoreCase(expected)) {
                    out.add(Arguments.of(email, password));
                }
            }
        }
        return out.stream();
    }

    /*
     * Legacy: Reads src/test/resources/testdata/login-data.csv and returns
     * Stream<Arguments>
     * Columns: email,password,expected
     *
     * Not currently used (tests call loginSuccessData/loginFailureData).
     */
    // public static Stream<Arguments> loginAllData() throws Exception {
    // InputStream is = Thread.currentThread().getContextClassLoader()
    // .getResourceAsStream("testdata/login-data.csv");
    // if (is == null) {
    // throw new IllegalStateException("Resource testdata/login-data.csv not
    // found");
    // }
    // List<Arguments> out = new ArrayList<>();
    // try (BufferedReader br = new BufferedReader(new InputStreamReader(is,
    // StandardCharsets.UTF_8))) {
    // String header = br.readLine(); // skip header
    // String line;
    // while ((line = br.readLine()) != null) {
    // if (line.trim().isEmpty())
    // continue;
    // String[] parts = line.split(",");
    // if (parts.length < 3)
    // continue;
    // String email = parts[0].trim();
    // String password = parts[1].trim();
    // String expected = parts[2].trim();
    // out.add(Arguments.of(email, password, expected));
    // }
    // }
    // return out.stream();
    // }

    /*
     * Legacy: Reads src/test/resources/testdata/register-data.csv and returns
     * Stream<Arguments>
     * Columns:
     * name,email,telefono,password,confirmPassword,expected,expectedMessage
     *
     * Currently tests use registerSuccessData/registerFailureData; keep commented
     * for reference.
     */
    // public static Stream<Arguments> registerAllData() throws Exception {
    // InputStream is = Thread.currentThread().getContextClassLoader()
    // .getResourceAsStream("testdata/register-data.csv");
    // if (is == null) {
    // throw new IllegalStateException("Resource testdata/register-data.csv not
    // found");
    // }
    // List<Arguments> out = new ArrayList<>();
    // try (BufferedReader br = new BufferedReader(new InputStreamReader(is,
    // StandardCharsets.UTF_8))) {
    // String header = br.readLine(); // skip header
    // String line;
    // while ((line = br.readLine()) != null) {
    // if (line.trim().isEmpty())
    // continue;
    // String[] parts = line.split(",");
    // if (parts.length < 7)
    // continue;
    // String name = parts[0].trim();
    // String email = parts[1].trim();
    // String telefono = parts[2].trim();
    // String password = parts[3].trim();
    // String confirmPassword = parts[4].trim();
    // String expected = parts[5].trim();
    // String expectedMessage = parts[6].trim();
    // out.add(Arguments.of(name, email, telefono, password, confirmPassword,
    // expected, expectedMessage));
    // }
    // }
    // return out.stream();
    // }

    public static Stream<Arguments> registerSuccessData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/register-data.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/register-data.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 7)
                    continue;
                String expected = parts[5].trim();
                if ("success".equalsIgnoreCase(expected)) {
                    out.add(Arguments.of(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(),
                            parts[4].trim(), parts[5].trim(), parts[6].trim()));
                }
            }
        }
        return out.stream();
    }

    public static Stream<Arguments> registerFailureData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/register-data.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/register-data.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 7)
                    continue;
                String expected = parts[5].trim();
                if ("failure".equalsIgnoreCase(expected)) {
                    out.add(Arguments.of(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(),
                            parts[4].trim(), parts[5].trim(), parts[6].trim()));
                }
            }
        }
        return out.stream();
    }

    /**
     * Reads src/test/resources/testdata/purchase-addOneProduct.csv and returns
     * Stream<Arguments>
     * Columns:
     * searchQuery,productText,address,paymentMethod,expected,expectedMessage
     */
    public static Stream<Arguments> purchaseAddOneProductData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/purchase-addOneProduct.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/purchase-addOneProduct.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 6)
                    continue;
                String searchQuery = parts[0].trim();
                String productText = parts[1].trim();
                String address = parts[2].trim();
                String paymentMethod = parts[3].trim();
                String expected = parts[4].trim();
                String expectedMessage = parts[5].trim();
                out.add(Arguments.of(searchQuery, productText, address, paymentMethod, expected, expectedMessage));
            }
        }
        return out.stream();
    }

    /**
     * Reads src/test/resources/testdata/purchase-addServiceAndProduct.csv and
     * returns
     * Stream<Arguments>
     * Columns:
     * productQuery,productText,serviceQuery,serviceText,address,paymentMethod,expected,expectedMessage
     */
    public static Stream<Arguments> purchaseAddServiceAndProductData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/purchase-addServiceAndProduct.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/purchase-addServiceAndProduct.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 8)
                    continue;
                String productQuery = parts[0].trim();
                String productText = parts[1].trim();
                String serviceQuery = parts[2].trim();
                String serviceText = parts[3].trim();
                String address = parts[4].trim();
                String paymentMethod = parts[5].trim();
                String expected = parts[6].trim();
                String expectedMessage = parts[7].trim();
                out.add(Arguments.of(productQuery, productText, serviceQuery, serviceText, address, paymentMethod,
                        expected, expectedMessage));
            }
        }
        return out.stream();
    }

    /**
     * Reads src/test/resources/testdata/purchase-addFiveProducts.csv and returns
     * Stream<Arguments>
     * Columns: productIdentifier,quantity,expected,expectedMessage
     */
    public static Stream<Arguments> purchaseAddFiveProductsData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/purchase-addFiveProducts.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/purchase-addFiveProducts.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 4)
                    continue;
                String productIdentifier = parts[0].trim();
                int quantity;
                try {
                    quantity = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String expected = parts[2].trim();
                String expectedMessage = parts[3].trim();
                out.add(Arguments.of(productIdentifier, quantity, expected, expectedMessage));
            }
        }
        return out.stream();
    }

    /**
     * Reads src/test/resources/testdata/purchase-addNearOutOfStockProduct.csv and
     * returns
     * Stream<Arguments>
     * Columns: productUrl,badgeText,address,paymentMethod,expected,expectedMessage
     */
    public static Stream<Arguments> purchaseAddNearOutOfStockData() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("testdata/purchase-addNearOutOfStockProduct.csv");
        if (is == null) {
            throw new IllegalStateException("Resource testdata/purchase-addNearOutOfStockProduct.csv not found");
        }
        List<Arguments> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 6)
                    continue;
                String productUrl = parts[0].trim();
                String badgeText = parts[1].trim();
                String address = parts[2].trim();
                String paymentMethod = parts[3].trim();
                String expected = parts[4].trim();
                String expectedMessage = parts[5].trim();
                out.add(Arguments.of(productUrl, badgeText, address, paymentMethod, expected, expectedMessage));
            }
        }
        return out.stream();
    }
}
