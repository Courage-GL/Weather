package com.xiaoalei.android.weather.bean;

import java.util.List;

public class CityBean {


    private List<RECORDSBean> RECORDS;

    public List<RECORDSBean> getRECORDS() {
        return RECORDS;
    }

    public void setRECORDS(List<RECORDSBean> RECORDS) {
        this.RECORDS = RECORDS;
    }


    public static class RECORDSBean {
        /**
         * _id : 1
         * name : 北京
         * posID : 101010100
         */

        private String _id;
        private String name;
        private String posID;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPosID() {
            return posID;
        }

        public void setPosID(String posID) {
            this.posID = posID;
        }
    }
}
