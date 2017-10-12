package com.caesar.phonelogs.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by heshengfang on 2017/4/21.
 */
public class CallLog extends Contact {
    public static enum Type {
        INCOMING_RECEIVED, OUTGOING_RECEIVED, INCOMING_MISSED, OUTGOING_MISSED;

        public static Type ordinalOf(int ordinal) {
            switch (ordinal) {
                case 0:
                    return INCOMING_RECEIVED;
                case 1:
                    return OUTGOING_RECEIVED;
                case 2:
                    return INCOMING_MISSED;
                case 3:
                    return OUTGOING_MISSED;
                default:
                    return null;
            }
        }
    }

    private int type;
    private String duration;
    private String callTime;
    private int countType;

    public int getCountType() {
        return countType;
    }

    public void setCountType(int countType) {
        this.countType = countType;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(Contact another) {
        if (-1 != type) {
            if (-1 == getDate())
                return 0;
            return -((int) getDate());
        }
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getNumber());
        dest.writeString(getName());
        dest.writeLong(getDate());
        dest.writeInt(type);
        dest.writeString(duration);
    }

    public static final Parcelable.Creator<CallLog> CREATOR = new Parcelable.Creator<CallLog>() {

        public CallLog createFromParcel(Parcel in) {
            CallLog calllog = new CallLog();
            calllog.setId(in.readLong());
            calllog.setNumber(in.readString());
            calllog.setName(in.readString());
            calllog.setDate(in.readLong());
            calllog.setType(in.readInt());
            calllog.setDuration(in.readString());
            return calllog;
        }

        public CallLog[] newArray(int size) {
            return new CallLog[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (null != o && o instanceof CallLog) {
            CallLog oc = (CallLog) o;

            if (oc.getName().equals(getName()) && oc.getNumber().equals(getNumber())) {

                if (-1 == oc.getDate() && -1 == getDate())
                    return true;

                if (-1 != oc.getDate() && -1 != getDate()) {
                    return oc.getDate() == getDate();
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = (null != getName()) ? getName().hashCode() : 0;
        code += (null != getNumber()) ? getNumber().hashCode() : 0;
        code += (-1 != getDate()) ? ((int) getDate()) : 0;
        return code;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("BTCalllog {");
        sb.append("id=" + getId() + ", ");
        sb.append("num=" + getNumber() + ", ");
        sb.append("name=" + getName() + ", ");
        sb.append("time=" + getDate() + ", ");
        sb.append("type=" + type + ",");
        sb.append("duration=" + duration + "}");
        return sb.toString();
    }
}
