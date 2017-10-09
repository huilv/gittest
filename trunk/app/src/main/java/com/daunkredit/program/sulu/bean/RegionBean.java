package com.daunkredit.program.sulu.bean;

import java.util.List;

/**
 * Created by Miaoke on 2017/3/10.
 */

public class RegionBean {

    private List<RegionsBean> regions;

    public List<RegionsBean> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionsBean> regions) {
        this.regions = regions;
    }

    public static class RegionsBean {
        /**
         * id : 88021
         * level : city
         * name : Kab. Badung
         */

        private int id;
        private String level;
        private String name;


        public RegionsBean() {
        }

        public RegionsBean(int id, String level, String name) {
            this.id = id;
            this.level = level;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
