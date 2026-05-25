
package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.CreateAndUpdateBanner;
import com.ra.base_spring_boot.dto.req.BannerDto;
import com.ra.base_spring_boot.services.core.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/banner")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BannerController {

    private final IBannerService iBannerService;

    @GetMapping()
    public ResponseEntity<?> getAllBanner(Pageable pageable, @RequestParam(required = false) String title) {
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(iBannerService.getAllBanner(pageable, title))
                        .build()
        );
    }

    @DeleteMapping("{id}/delete")
    public ResponseEntity<?> deleteBannerById(@PathVariable Long id) throws Exception {
        iBannerService.deleteBannerById(id);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Banner deleted successfully")
                        .build()
        );
    }

    // @apiNote handle createBanner with title
    @PostMapping(value = "/create")
    public ResponseEntity<?> createBanner(@RequestPart("form") BannerDto form, @RequestPart(value = "file", required = false) final MultipartFile file) throws Exception{
        BannerDto banner = iBannerService.createBanner(form, file);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(banner)
                        .build()
        );
    }

    @PostMapping(value = "{id}/upload-banner")
    public ResponseEntity<?> uploadBannner(@PathVariable final Long id, @RequestPart("file") final MultipartFile file) throws Exception {
        iBannerService.uploadFile(id, file);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Banner image uploaded successfully")
                        .build()
        );
    }

    @PutMapping(value = "{id}/update")
    public ResponseEntity<?> updateBannerById(@PathVariable Long id, @RequestBody CreateAndUpdateBanner form) throws Exception {
        iBannerService.updateBannerById(id, form);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Banner updated successfully")
                        .build()
        );
    }
}
