package com.nilscoding.maven.mvndlwsdl.utils;

import org.apache.maven.plugin.logging.Log;

/**
 * Interface for downloaders.
 * @author NilsCoding
 */
public interface IDownloader {

    /**
     * Downloads a text file from given URL.
     * @param url        URL to download from
     * @param log        logging
     * @param optionsStr options string (specific to implementation)
     * @return downloaded text file as string or null if download was not possible
     */
    String downloadFile(String url, Log log, String optionsStr);

}
