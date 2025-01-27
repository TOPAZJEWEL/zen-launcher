package fr.neamar.kiss.pojo;

import android.net.Uri;

import fr.neamar.kiss.normalizer.StringNormalizer;

public class ContactsPojo extends Pojo {
    public final String lookupKey;

    public final String phone;
    //phone without special characters
    public final StringNormalizer.Result normalizedPhone;
    public final Uri icon;

    // Is this a primary phone?
    public Boolean primary;

    // How many times did we phone this contact?
    public final int timesContacted;

    // Is this contact starred ?
    public final Boolean starred;

    // Is this number a home (local) number ?
    public final Boolean homeNumber;

    public StringNormalizer.Result normalizedNickname = null;

    private String nickname = "";

    public StringNormalizer.Result normalizedTitle = null;

    public StringNormalizer.Result normalizedCompany = null;

    public StringNormalizer.Result normalizedEmail = null;

    public String title = "";

    public String company ="";

    public ContactsPojo(String id, String lookupKey, String phone, StringNormalizer.Result normalizedPhone,
                        Uri icon, Boolean primary, int timesContacted, Boolean starred,
                        Boolean homeNumber) {
        super(id);
        this.lookupKey = lookupKey;
        this.phone = phone;
        this.normalizedPhone = normalizedPhone;
        this.icon = icon;
        this.primary = primary;
        this.timesContacted = timesContacted;
        this.starred = starred;
        this.homeNumber = homeNumber;
    }

    //ZLauncher
    public int contactId;
    public int whatsAppCalling;
    public int whatsAppMessaging;
    public int signalCalling;
    public int signalMessaging;
    public int facebookCalling;
    public int facebookMessaging;
    public int emailLookupKey;

    public String getNickname() {
        return nickname;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }
    public void setNickname(String nickname) {
        if (nickname != null) {
            // Set the actual user-friendly name
            this.nickname = nickname;
            this.normalizedNickname = StringNormalizer.normalizeWithResult(this.nickname, false);
        } else {
            this.nickname = null;
            this.normalizedNickname = null;
        }
    }

    public void setCompany(String company) {
        if( company != null) {
            this.company = company;
            this.normalizedCompany = StringNormalizer.normalizeWithResult(this.company, false);
        } else {
            this.company = null;
            this.normalizedCompany = null;
        }
    }

    public void setTitle(String title) {
        if( title != null) {
            this.title = title;
            this.normalizedTitle = StringNormalizer.normalizeWithResult(this.title, false);
        } else {
            this.title = null;
            this.normalizedTitle = null;
        }
    }

    public void setNormalizedEmail(String normalizedEmail){
        if( normalizedEmail != null) {
            this.normalizedEmail = StringNormalizer.normalizeWithResult(normalizedEmail, false);
        } else {
            this.normalizedEmail = null;
        }
    }

    public void setEmailLookupKey(int emailLookupKey){
        this.emailLookupKey = emailLookupKey;
    }
}
