package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.CreateAndUpdateBanner;
import com.ra.base_spring_boot.dto.req.BannerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IBannerService {
    Page<BannerDto> getAllBanner(Pageable pageable, String title);

    void deleteBannerById(Long id) throws Exception;

    BannerDto createBanner(BannerDto form, MultipartFile file) throws Exception;

    void uploadFile(final Long id, final MultipartFile file) throws Exception;

    BannerDto updateBannerById(Long id, CreateAndUpdateBanner form) throws Exception;
}
