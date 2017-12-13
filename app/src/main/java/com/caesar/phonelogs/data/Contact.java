package com.caesar.phonelogs.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.caesar.phonelogs.utils.pinyinutil.PinyinUtil;

/**
 * Created by heshengfang on 2017/4/17.
 */
public class Contact implements Parcelable, Comparable<Contact> {
    private long id = 0;
    private boolean isFavorite;
    private Bitmap bitmap = null;
    private int contactId;
    private String number;
    private String jsonNumber;
    private String name;
    private String mimeType;
    private long date;
    private Object tag;
    private String key;

    public String getJsonNumber() {
        return jsonNumber;
    }

    public void setJsonNumber(String jsonNumber) {
        this.jsonNumber = jsonNumber;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    private String sort;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNumber() {
        return number != null ? number : "";
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
        if (!TextUtils.isEmpty(name)) {
            key = PinyinUtil.formatAlpha(name);
        }
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public int compareTo(Contact another) {
        return name.compareTo(another.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getMimeType());
        dest.writeString(getNumber());
        dest.writeString(getName());
        dest.writeLong(getDate());
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        public Contact createFromParcel(Parcel in) {
            Contact contact = new Contact();
            contact.setId(in.readLong());
            contact.setMimeType(in.readString());
            contact.setNumber(in.readString());
            contact.setName(in.readString());
            contact.setDate(in.readLong());
            return contact;
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (null != o && o instanceof Contact) {
            Contact oc = (Contact) o;
            if (oc.getName().equals(getName()) && oc.getNumber().equals(getNumber())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = (null != name) ? name.hashCode() : 0;
        code += (null != number) ? number.hashCode() : 0;
        return code;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("BTContact {");
        sb.append("id=" + id + ", ");
        sb.append("type=" + mimeType + ", ");
        sb.append("num=" + number + ", ");
        sb.append("name=" + name + ", ");
        sb.append("date=" + date + "}");
        return sb.toString();
    }
}