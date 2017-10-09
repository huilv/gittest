package com.daunkredit.program.sulu.common.network;

import java.util.List;

/**
 * Created by localuser on 2017/3/1.
 */

public class RecordFilesResponse {
    private List<FilesBean> files;

    public List<FilesBean> getFiles() {
        return files;
    }

    public void setFiles(List<FilesBean> files) {
        this.files = files;
    }

    public static class FilesBean {
        /**
         * fileType : KTP_PHOTO
         * url : string
         */

        private String fileType;
        private String url;

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
