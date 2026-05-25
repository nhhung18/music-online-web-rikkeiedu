package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.CreateAndUpdateBanner;
import com.ra.base_spring_boot.dto.req.BannerDto;
import com.ra.base_spring_boot.dto.resp.CloudinaryResp;
import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.repository.IBannerRepository;
import com.ra.base_spring_boot.services.core.IBannerService;
import com.ra.base_spring_boot.services.external.cloudinary.CloudinaryService;
import com.ra.base_spring_boot.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class BannerServicelmpl implements IBannerService {
    @Autowired
    private IBannerRepository iBannerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CloudinaryResp cloudinaryResp;

    @Override
    public void deleteBannerById(Long id) throws Exception {
        Banner banner = iBannerRepository.findById(id).orElseThrow(() -> new Exception("Banner id does not exist!"));

        iBannerRepository.deleteById(id);
    }

    @Override
    public BannerDto createBanner(BannerDto form, MultipartFile file) throws Exception {
        Banner banner = new Banner();
        banner.setTitle(form.getTitle());
        iBannerRepository.save(banner);
        if (file != null) {
            uploadFile(banner.getId(), file);
        }
        return modelMapper.map(banner, BannerDto.class);
    }

    @Override
    public Page<BannerDto> getAllBanner(Pageable pageable, String title) {
        Page<Banner> bannerList = null;
        if (title != null) {
            bannerList = iBannerRepository.findByTitleContainingIgnoreCase(pageable, title);
        } else {
            bannerList = iBannerRepository.findAll(pageable);
        }
        return bannerList.map(banner -> modelMapper.map(banner, BannerDto.class));
    }

    @Transactional
    public void uploadFile(final Long id, final MultipartFile file) throws Exception {
        Banner banner = iBannerRepository.findById(id)
                .orElseThrow(() -> new Exception("Banner id does not exist!"));
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResp response = cloudinaryService.uploadBanner(file, fileName);
        banner.setImageUrl(response.getUrl());
//        banner.setPublicId(response.getPublicId());
        iBannerRepository.save(banner);
    }

    @Override
    public BannerDto updateBannerById(Long id, CreateAndUpdateBanner form) throws Exception {
        Banner banner = iBannerRepository.findById(id).orElseThrow(() -> new Exception("Banner id does not exist!"));
        modelMapper.map(form, banner);
        Banner bannerUpdate = iBannerRepository.save(banner);
        return modelMapper.map(bannerUpdate, BannerDto.class);
    }


}
