package utils;

import java.util.List;
import java.util.Map;

public class WebDriverPreferences {

    private List<String> chromePreferences;
    private Map<String, String> edgePreferences;
    private Map<String, Object> firefoxPreferences;

    public WebDriverPreferences(
            List<String> chromePreferences,
            Map<String, String> edgePreferences,
            Map<String, Object> firefoxPreferences) {

        this.chromePreferences = chromePreferences;
        this.edgePreferences = edgePreferences;
        this.firefoxPreferences = firefoxPreferences;
    }

    public List<String> getChromePreferences() {
        return chromePreferences;
    }

    public void setChromePreferences(List<String> chromePreferences) {
        this.chromePreferences = chromePreferences;
    }

    public Map<String, String> getEdgePreferences() {
        return edgePreferences;
    }

    public void setEdgePreferences(Map<String, String> edgePreferences) {
        this.edgePreferences = edgePreferences;
    }

    public Map<String, Object> getFirefoxPreferences() {
        return firefoxPreferences;
    }

    public void setFirefoxPreferences(Map<String, Object> firefoxPreferences) {
        this.firefoxPreferences = firefoxPreferences;
    }
}
