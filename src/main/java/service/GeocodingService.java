package service;

<<<<<<< Updated upstream
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
=======
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
>>>>>>> Stashed changes
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
<<<<<<< Updated upstream
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
=======
 * Resolves a human-readable address to latitude / longitude.
 *
 * Strategy:
 *   1. Nominatim structured query  (street + city + state)
 *   2. Nominatim free-form query   (full address string)
 *   3. US Census Bureau geocoder    (US addresses only, no rate-limit)
 *
 * Returns null when every strategy fails.
 */
public class GeocodingService {

    private static final String NOMINATIM =
            "https://nominatim.openstreetmap.org/search?format=json&limit=1";
    private static final String CENSUS =
            "https://geocoding.geo.census.gov/geocoder/locations/onelineaddress"
          + "?benchmark=Public_AR_Current&format=json&address=";

    /* ------------------------------------------------------------ */
    /*  Public API                                                    */
    /* ------------------------------------------------------------ */

    /**
     * Best-effort geocode.  Tries Nominatim (structured, then free-form),
     * then falls back to the Census Bureau.
     */
    public GeoPosition geocode(String address) {
        if (address == null || address.isBlank()) return null;

        GeoPosition pos = nominatimStructured(address);
        if (pos != null) return pos;

        pos = nominatimFreeForm(address);
        if (pos != null) return pos;

        return censusBureau(address);
    }

    /* ------------------------------------------------------------ */
    /*  Nominatim — structured                                       */
    /* ------------------------------------------------------------ */

    private GeoPosition nominatimStructured(String address) {
        try {
            String[] parts = address.split(",");
            if (parts.length < 3) return null;

            String street = parts[0].trim();
            String city   = parts[1].trim();
            String state  = parts[2].trim().replaceAll("\\d", "")
                                            .replaceAll("\\s+", " ").trim();

            String url = NOMINATIM
                    + "&street="  + enc(street)
                    + "&city="    + enc(city)
                    + "&state="   + enc(state)
                    + "&country=" + enc("US");

            return parseNominatim(fetch(url));
        } catch (Exception e) {
            return null;
        }
    }

    /* ------------------------------------------------------------ */
    /*  Nominatim — free-form                                        */
    /* ------------------------------------------------------------ */

    private GeoPosition nominatimFreeForm(String address) {
        try {
            String url = NOMINATIM + "&q=" + enc(address);
            return parseNominatim(fetch(url));
        } catch (Exception e) {
            return null;
        }
    }

    private GeoPosition parseNominatim(String json) {
        if (json == null || json.equals("[]")) return null;

        String lat = extract(json, "\"lat\"");
        String lon = extract(json, "\"lon\"");
        if (lat == null || lon == null) return null;

        return new GeoPosition(Double.parseDouble(lat),
                               Double.parseDouble(lon));
    }

    /* ------------------------------------------------------------ */
    /*  Census Bureau (US only, no rate-limit)                       */
    /* ------------------------------------------------------------ */

    private GeoPosition censusBureau(String address) {
        try {
            String json = fetch(CENSUS + enc(address));
            if (json == null) return null;

            int idx = json.indexOf("\"coordinates\"");
            if (idx == -1) return null;

            int start = json.indexOf('{', idx);
            int end   = json.indexOf('}', start);
            if (start == -1 || end == -1) return null;

            String block = json.substring(start, end + 1);
            String x = extract(block, "\"x\"");   // longitude
            String y = extract(block, "\"y\"");   // latitude
            if (x == null || y == null) return null;

            return new GeoPosition(Double.parseDouble(y),
                                   Double.parseDouble(x));
        } catch (Exception e) {
            return null;
        }
    }

    /* ------------------------------------------------------------ */
    /*  Helpers                                                       */
    /* ------------------------------------------------------------ */

    private String fetch(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "CS480-MapApp/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),
                                          StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** Cheap JSON value extractor — no library needed. */
    private String extract(String json, String key) {
        int i = json.indexOf(key);
        if (i == -1) return null;
        int colon = json.indexOf(':', i);
        if (colon == -1) return null;

        int s = colon + 1;
        while (s < json.length() && (json.charAt(s) == ' '
                || json.charAt(s) == '"')) s++;
        int e = s;
        while (e < json.length() && json.charAt(e) != '"'
                && json.charAt(e) != ',' && json.charAt(e) != '}') e++;
        return json.substring(s, e).trim();
    }

    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
>>>>>>> Stashed changes
    }
}
