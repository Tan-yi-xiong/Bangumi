package com.TyxApp.bangumi.data.source.local;

public final class BangumiPresistenceContract {
    public final static class BangumiSource  {
        public static final String ZZZFUN = "Zzzfun";
        public static final String NiICO = "nico";
        public static final String SAKURA = "樱花动漫";
        public static final String DILIDLI = "dilidili";
    }

    public final static class BangumiFieldName {
        public static final String VOD_Id = "vod_Id";
        public static final String VIDEO_SOURE = "video_soure";
        public static final String NAME = "name";
        public static final String COVER = "cover";
        public static final String IMG = "img";
        public static final String INTRO = "intro";
        public static final String HITS = "hits";
        public static final String REMARKS = "remarks";
        public static final String ISCOLLECTED = "isFavorite";
        public static final String TOTAL = "total";
        public static final String SERIAL = "serial";
    }

    public final static class VideoDownloadInfoFieldName {
        public static final String BANGUMI_Id = "bangumi_id";
        public static final String BANGUMI_SOURE = "bangumi_soure";
        public static final String VIDEO_URL = "video_url";
        public static final String STATE = "state";
        public static final String FILE_NAME = "file_name";
    }
}
