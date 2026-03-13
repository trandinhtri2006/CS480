package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Geocoding service that resolves street addresses to lat/lon coordinates. Uses
 * OpenStreetMap Nominatim with US Census Bureau fallback.
 */
public class GeocodingService {

    /**
     * Geocode an address string to [latitude, longitude]. Tries Nominatim
     * structured query, free-form, reduced, then Census Bureau.
     */
    public double[] geocode(String addressText) throws Exception {
        String[] parts = addressText.trim().split(",");
        String queryUrl;
        if (parts.length >= 2) {
            String street = URLEncoder.encode(parts[0].trim(), StandardCharsets.UTF_8.toString());
            String city = URLEncoder.encode(parts[1].trim(), StandardCharsets.UTF_8.toString());
            String state = parts.length >= 3 ? URLEncoder.encode(parts[2].trim(), StandardCharsets.UTF_8.toString()) : "";
            queryUrl = "https://nominatim.openstreetmap.org/search?street=" + street
                    + "&city=" + city
                    + (state.isEmpty() ? "" : "&state=" + state)
                    + "&country=us&format=json&limit=1&addressdetails=1";
        } else {
            String encodedAddress = URLEncoder.encode(addressText.trim(), StandardCharsets.UTF_8.toString());
            queryUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress
                    + "&format=json&limit=1&addressdetails=1&countrycodes=us";
        }

        double[] result = queryNominatim(queryUrl);
        if (result != null) {
            return result;
        }

        // Fallback: free-form query via Nominatim
        String encodedAddress = URLEncoder.encode(addressText.trim(), StandardCharsets.UTF_8.toString());
        String fallbackUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress
                + "&format=json&limit=1&addressdetails=1&countrycodes=us";
        result = queryNominatim(fallbackUrl);
        if (result != null) {
            return result;
        }

        // Fallback: drop street number, try street name + city
        if (parts.length >= 2) {
            String streetName = parts[0].trim().replaceFirst("^\\d+\\s+", "");
            String reducedQuery = URLEncoder.encode(streetName + ", " + parts[1].trim()
                    + (parts.length >= 3 ? ", " + parts[2].trim() : ""), StandardCharsets.UTF_8.toString());
            String reducedUrl = "https://nominatim.openstreetmap.org/search?q=" + reducedQuery
                    + "&format=json&limit=1&countrycodes=us";
            result = queryNominatim(reducedUrl);
            if (result != null) {
                return result;
            }
        }



        // Fallback: US Census Bureau geocoder
        result = queryCensusGeocoder(addressText.trim());
        if (result != null) {
            return result;
        }

        throw new RuntimeException("Address not found: " + addressText);
    }

    public List<double[]> getCoordinates(List<String> addresses) throws Exception {

        List<double[]> coords = new ArrayList<>();

        for (String address : addresses) {
            double[] c = geocode(address); // your existing method
            coords.add(c);
        }

        return coords;
    }

    private double[] queryNominatim(String urlString) throws Exception {
        URL apiURL = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "JavaMapTester/1.0");

        if (connection.getResponseCode() != 200) {
            return null;
        }

        StringBuilder jsonResponse = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
        }

        String responseString = jsonResponse.toString();
        if (responseString.equals("[]")) {
            return null;
        }

        return new double[]{
            parseJsonStringValue(responseString, "lat"),
            parseJsonStringValue(responseString, "lon")
        };
    }

    private double[] queryCensusGeocoder(String address) throws Exception {
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
        URL apiURL = URI.create("https://geocoding.geo.census.gov/geocoder/locations/onelineaddress?address="
                + encoded + "&benchmark=Public_AR_Current&format=json").toURL();
        HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "JavaMapTester/1.0");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        if (connection.getResponseCode() != 200) {
            return null;
        }

        StringBuilder jsonResponse = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
        }

        String resp = jsonResponse.toString();
        if (resp.contains("\"addressMatches\":[]")) {
            return null;
        }

        String coordSection = resp.substring(resp.indexOf("\"coordinates\""));
        double lon = parseJsonNumericValue(coordSection, "x");
        double lat = parseJsonNumericValue(coordSection, "y");

        System.out.println("Geocoded via Census Bureau: " + address + " -> " + lat + ", " + lon);
        return new double[]{lat, lon};
    }

    private double parseJsonStringValue(String jsonString, String keyName) {
        String searchTarget = "\"" + keyName + "\":\"";
        int startIndex = jsonString.indexOf(searchTarget);
        if (startIndex == -1) {
            throw new RuntimeException("Key not found: " + keyName);
        }
        startIndex += searchTarget.length();
        int endIndex = jsonString.indexOf("\"", startIndex);
        return Double.parseDouble(jsonString.substring(startIndex, endIndex));
    }

    /**
     * Reverse geocode coordinates to a human-readable street address.
     * Returns format: "123 Main Street, City, ST"
     */
    public String reverseGeocode(double lat, double lon) {
        try {
            String urlString = "https://nominatim.openstreetmap.org/reverse?lat=" + lat
                    + "&lon=" + lon + "&format=json&addressdetails=1&zoom=18";
            URL apiURL = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "JavaMapTester/1.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                return String.format("%.5f, %.5f", lat, lon);
            }

            StringBuilder jsonResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
            }

            String resp = jsonResponse.toString();

            String houseNumber = extractAddressField(resp, "house_number");
            String road = extractAddressField(resp, "road");
            String city = extractAddressField(resp, "city");
            if (city == null) city = extractAddressField(resp, "town");
            if (city == null) city = extractAddressField(resp, "village");
            if (city == null) city = extractAddressField(resp, "hamlet");
            if (city == null) city = extractAddressField(resp, "suburb");
            if (city == null) city = extractAddressField(resp, "municipality");
            if (city == null) city = extractAddressField(resp, "county");
            String state = extractAddressField(resp, "state");

            if (state != null) {
                state = stateAbbreviation(state);
            }

            StringBuilder addr = new StringBuilder();
            if (houseNumber != null && road != null) {
                addr.append(houseNumber).append(" ").append(road);
            } else if (road != null) {
                addr.append(road);
            }
            if (city != null) {
                if (addr.length() > 0) addr.append(", ");
                addr.append(city);
            }
            if (state != null) {
                if (addr.length() > 0) addr.append(", ");
                addr.append(state);
            }
            return addr.length() > 0 ? addr.toString() : String.format("%.5f, %.5f", lat, lon);
        } catch (Exception e) {
            return String.format("%.5f, %.5f", lat, lon);
        }
    }

    private String extractAddressField(String json, String field) {
        int addrStart = json.indexOf("\"address\"");
        if (addrStart == -1) return null;
        String addrSection = json.substring(addrStart);

        String key = "\"" + field + "\":\"";
        int start = addrSection.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = addrSection.indexOf("\"", start);
        if (end == -1) return null;
        return addrSection.substring(start, end);
    }

    private String stateAbbreviation(String state) {
        return switch (state) {
            case "Alabama" -> "AL"; case "Alaska" -> "AK"; case "Arizona" -> "AZ";
            case "Arkansas" -> "AR"; case "California" -> "CA"; case "Colorado" -> "CO";
            case "Connecticut" -> "CT"; case "Delaware" -> "DE"; case "Florida" -> "FL";
            case "Georgia" -> "GA"; case "Hawaii" -> "HI"; case "Idaho" -> "ID";
            case "Illinois" -> "IL"; case "Indiana" -> "IN"; case "Iowa" -> "IA";
            case "Kansas" -> "KS"; case "Kentucky" -> "KY"; case "Louisiana" -> "LA";
            case "Maine" -> "ME"; case "Maryland" -> "MD"; case "Massachusetts" -> "MA";
            case "Michigan" -> "MI"; case "Minnesota" -> "MN"; case "Mississippi" -> "MS";
            case "Missouri" -> "MO"; case "Montana" -> "MT"; case "Nebraska" -> "NE";
            case "Nevada" -> "NV"; case "New Hampshire" -> "NH"; case "New Jersey" -> "NJ";
            case "New Mexico" -> "NM"; case "New York" -> "NY"; case "North Carolina" -> "NC";
            case "North Dakota" -> "ND"; case "Ohio" -> "OH"; case "Oklahoma" -> "OK";
            case "Oregon" -> "OR"; case "Pennsylvania" -> "PA"; case "Rhode Island" -> "RI";
            case "South Carolina" -> "SC"; case "South Dakota" -> "SD"; case "Tennessee" -> "TN";
            case "Texas" -> "TX"; case "Utah" -> "UT"; case "Vermont" -> "VT";
            case "Virginia" -> "VA"; case "Washington" -> "WA"; case "West Virginia" -> "WV";
            case "Wisconsin" -> "WI"; case "Wyoming" -> "WY";
            case "District of Columbia" -> "DC";
            default -> state;
        };
    }

    private double parseJsonNumericValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) {
            throw new RuntimeException("Key \"" + key + "\" not found");
        }
        start += search.length();
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return Double.parseDouble(json.substring(start, end).trim());
    }
}
