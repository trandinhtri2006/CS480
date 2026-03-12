package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

        // Fallback: free-form query
        String encodedAddress = URLEncoder.encode(addressText.trim(), StandardCharsets.UTF_8.toString());
        String fallbackUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress
                + "&format=json&limit=1&addressdetails=1&countrycodes=us";
        result = queryNominatim(fallbackUrl);
        if (result != null) {
            return result;
        }

        // Drop the street number and try street name + city
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

        // US Census Bureau geocoder
        result = queryCensusGeocoder(addressText.trim());
        if (result != null) {
            return result;
        }

        throw new RuntimeException("Address not found: " + addressText);
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
