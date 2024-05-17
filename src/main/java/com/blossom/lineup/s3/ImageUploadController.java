package com.blossom.lineup.s3;

import com.blossom.lineup.base.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload-image")
public class ImageUploadController {

    private final S3Uploader s3Uploader;

    @PostMapping("/organization")
    public Response<ImageUrl> uploadOrganizationImage(@RequestPart("file")MultipartFile file) {
        log.debug("[주점 파일 업로드]");
        ImageUrl imageUrl = new ImageUrl(s3Uploader.saveFile(file, "organization"));
        return Response.ok(imageUrl);
    }
}
