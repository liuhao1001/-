package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.Result;
import myproject.convert.PostsConvert;
import myproject.entity.PostsEntity;
import myproject.query.PostsQuery;
import myproject.service.PostsService;
import myproject.vo.PostsVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 帖子详情
 */
@RestController
@RequestMapping("posts")
@Tag(name = "帖子详情")
@AllArgsConstructor
public class PostsController {
	private final PostsService postsService;

	@PostMapping("page")
	@Operation(summary = "分页")
	@SysLog(title = "查看帖子分页")
	public Result<PageResult<PostsVO>> page(@RequestBody @Valid PostsQuery query) {
		PageResult<PostsVO> page = postsService.page(query);
		return Result.ok(page);
	}

	@PostMapping("list")
	@Operation(summary = "列表")
	@SaIgnore
	@SysLog(title = "查看帖子列表")
	public Result<List<PostsVO>> list(@RequestBody @Valid PostsQuery query) {
		List<PostsVO> list = postsService.queryList(query);
		return Result.ok(list);
	}

	@PostMapping("/info")
	@Operation(summary = "信息")
	@SaIgnore
	@SysLog(title = "查看帖子信息")
	public Result<PostsVO> get(@RequestSingleParam(value = "id") Long id) {
		PostsEntity entity = postsService.getById(id);
		return Result.ok(PostsConvert.INSTANCE.convert(entity));
	}

	@PostMapping("save")
	@Operation(summary = "保存")
	@SysLog(title = "发布帖子")
	public Result<String> save(@RequestBody PostsVO vo) {
		postsService.save(vo);
		return Result.ok();
	}

	@PostMapping("update")
	@Operation(summary = "修改")
	@SysLog(title = "修改帖子")
	public Result<String> update(@RequestBody @Valid PostsVO vo) {
		postsService.update(vo);
		return Result.ok();
	}

	@PostMapping("delete")
	@Operation(summary = "删除")
	@SysLog(title = "删除帖子")
	public Result<String> delete(@RequestBody List<Long> idList) {
		postsService.delete(idList);
		return Result.ok();
	}

	@PostMapping("export")
	@Operation(summary = "导出")
	@SysLog(title = "导出帖子")
	public void export(@RequestBody @Valid PostsQuery query) {
		postsService.export(query);
	}
}