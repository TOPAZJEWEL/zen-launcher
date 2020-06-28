package fr.neamar.kiss.dataprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import fr.neamar.kiss.R;
import fr.neamar.kiss.dataprovider.simpleprovider.SimpleProvider;
import fr.neamar.kiss.pojo.Pojo;
import fr.neamar.kiss.pojo.SearchPojo;
import fr.neamar.kiss.searcher.Searcher;

import static fr.neamar.kiss.pojo.SearchPojo.URL_QUERY;
import static fr.neamar.kiss.pojo.SearchPojo.ZEN_ADD_LINK;
import static fr.neamar.kiss.pojo.ShortcutsPojo.SCHEME;

public class SearchProvider extends SimpleProvider {
    private static final String URL_REGEX = "^(?:[a-z]+://)?(?:[a-z0-9-]|[^\\x00-\\x7F])+(?:[.](?:[a-z0-9-]|[^\\x00-\\x7F])+)+.*$";
    public static final Pattern urlPattern = Pattern.compile(URL_REGEX);
    private final SharedPreferences prefs;

    public static Set<String> getDefaultSearchProviders(Context context) {
        String[] defaultSearchProviders = context.getResources().getStringArray(R.array.defaultSearchProviders);
        return new HashSet<>(Arrays.asList(defaultSearchProviders));
    }

    private final ArrayList<SearchPojo> searchProviders = new ArrayList<>();
    private final Context context;

    public SearchProvider(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        reload();
    }

    @Override
    public void reload() {
        searchProviders.clear();
        Set<String> selectedProviders = PreferenceManager.getDefaultSharedPreferences(context).getStringSet("selected-search-provider-names", new HashSet<>(Collections.singletonList("Google")));
        Set<String> availableProviders = PreferenceManager.getDefaultSharedPreferences(context).getStringSet("available-search-providers", SearchProvider.getDefaultSearchProviders(context));

        for (String searchProvider : selectedProviders) {
            String url = getProviderUrl(availableProviders, searchProvider);
            SearchPojo pojo = new SearchPojo("", url, SearchPojo.SEARCH_QUERY);
            // Super low relevance, should never be displayed before anything
            pojo.relevance = -500;
            pojo.setName(searchProvider, false);
            if (pojo.url != null) {
                searchProviders.add(pojo);
            }
        }
    }

    @Override
    public void requestResults(String s, Searcher searcher) {
        searcher.addResult(getResults(s).toArray(new Pojo[0]));
    }

    private ArrayList<Pojo> getResults(String query) {
        ArrayList<Pojo> records = new ArrayList<>();

        if (prefs.getBoolean("enable-search", true)) {
            for (SearchPojo pojo : searchProviders) {
                // Set the id, otherwise the result will be boosted since KISS will assume we've selected this search provider multiple times before"
                pojo.id = "search://" + query;
                pojo.query = query;
                records.add(pojo);
            }
        }

        // Open URLs directly (if I type http://something.com for instance)
        Matcher m = urlPattern.matcher(query);
        if (m.find()) {
            String guessedUrl = URLUtil.guessUrl(query);
            // URLUtil returns an http URL... we'll upgrade it to HTTPS
            // to avoid security issues on open networks,
            // technological problems when using HSTS
            // and do one less redirection to https
            // (tradeoff: non https URL will break, but they shouldn't exist anymore)
            guessedUrl = guessedUrl.replace("http://", "https://");
            if (URLUtil.isValidUrl(guessedUrl)) {
                SearchPojo pojo = new SearchPojo(query, guessedUrl, URL_QUERY);
                pojo.id = SCHEME+guessedUrl;
                pojo.setName(guessedUrl, false);
                records.add(pojo);

                SearchPojo pojo2 = new SearchPojo(query, guessedUrl, ZEN_ADD_LINK);
                pojo2.setName(guessedUrl, false);
                records.add(pojo2);
            }
        }
        return records;
    }

    @Nullable
    @SuppressWarnings("StringSplitter")
    // Find the URL associated with specified providerName
    private String getProviderUrl(Set<String> searchProviders, String searchProviderName) {
        for (String nameAndUrl : searchProviders) {
            if (nameAndUrl.contains(searchProviderName + "|")) {
                String[] arrayNameAndUrl = nameAndUrl.split("\\|");
                // sanity check
                if (arrayNameAndUrl.length == 2) {
                    return arrayNameAndUrl[1];
                }
            }
        }
        return null;
    }
}
