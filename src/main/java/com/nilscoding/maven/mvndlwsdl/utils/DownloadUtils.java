package com.nilscoding.maven.mvndlwsdl.utils;

import org.apache.maven.plugin.logging.Log;

/**
 * Download utils.
 * @author NilsCoding
 */
public final class DownloadUtils {

    private DownloadUtils() {
    }

    /**
     * Downloads the content from the given URL to a string.
     * @param url               URL to download from
     * @param log               logging
     * @param downloaderClass   class name of downloader, empty for default
     * @param downloaderOptions downloader options
     * @return URL content as string or null
     */
    public static String download(String url, Log log, String downloaderClass, String downloaderOptions) {
        try {
            IDownloader downloaderImpl = null;
            if (StringUtils.isEmpty(downloaderClass) == false) {
                Class<?> clazz = Class.forName(downloaderClass);
                Object downloaderImplObj = clazz.getDeclaredConstructor().newInstance();
                if (downloaderImplObj instanceof IDownloader) {
                    downloaderImpl = (IDownloader) downloaderImplObj;
                }
            } else {
                downloaderImpl = new OkHttp3Downloader();
            }
            if (downloaderImpl != null) {
                return downloaderImpl.downloadFile(url, log, downloaderOptions);
            }
            log.error("downloader '" + downloaderClass + "' not found");
            return null;
        } catch (Throwable ex) {
            log.error("could not fetch file from '" + url + "': " + ex, ex);
            return null;
        }
    }

}
