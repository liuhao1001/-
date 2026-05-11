package myproject.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import myproject.common.storage.service.StorageService;
import myproject.common.utils.Result;
import myproject.service.SysAttachmentService;
import myproject.vo.SysAttachmentVO;

/**
 * 文件上传
 */
@RestController
@RequestMapping("file")
@Tag(name = "文件上传")
@AllArgsConstructor
public class SysFileUploadController {
	private final StorageService storageService;
	private final SysAttachmentService sysAttachmentService;

	@PostMapping("upload")
	@Operation(summary = "上传")
	@SaIgnore
	public Result<SysAttachmentVO> upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			return Result.error("请选择需要上传的文件");
		}
		// 上传路径
		String path = storageService.getPath(file.getOriginalFilename());
		// 上传文件
		String url = storageService.upload(file.getBytes(), path);
		SysAttachmentVO vo = new SysAttachmentVO();
		vo.setUrl(url);
		vo.setSize(file.getSize());
		vo.setName(file.getOriginalFilename());
		vo.setPlatform(storageService.properties.getConfig().getType().name());
		sysAttachmentService.save(vo);
		return Result.ok(vo);
	}
}