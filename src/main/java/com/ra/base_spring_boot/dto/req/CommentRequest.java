
package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 255, message = "Content cannot exceed 255 characters")
    private String content;
    
    @NotNull(message = "Song ID is required")
    private Long songId;
    
    private Long parentId;
}
