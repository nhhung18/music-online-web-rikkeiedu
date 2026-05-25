package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.resp.SongResp;

import java.util.List;

public interface IDownloadService {
    void createDownload(Long userId, Long songId);
    List<SongResp> getDownloadsByUser(Long userId);
    void deleteDownload(Long userId, Long songId);
}
